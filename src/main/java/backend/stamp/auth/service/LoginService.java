package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.account.entity.UserType;
import backend.stamp.account.repository.AccountRepository;
import backend.stamp.auth.dto.request.LoginRequest;
import backend.stamp.auth.dto.response.SignUpResponse;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.TokenProvider;
import backend.stamp.manager.entity.Manager;
import backend.stamp.manager.repository.ManagerRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {
    private final AccountRepository accountRepository;
    private final UsersRepository usersRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    /**
     * ID/PW를 검증하고 로그인에 성공하면 토큰과 사용자 식별 정보를 반환합니다.
     */
    public SignUpResponse login(LoginRequest request) {

        Account account = accountRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND);
        }

        UserType userType = account.getUserType();
        Long entityId = null;

        if (userType == UserType.USER) {
            Users user = usersRepository.findByAccount(account)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
            entityId = user.getUserId();
        } else if (userType == UserType.MANAGER) {
            Manager manager = managerRepository.findByAccount(account)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
            entityId = manager.getManagerId();
        }

        String accessToken = tokenProvider.createAccessToken(account);
        String refreshToken = tokenProvider.createRefreshToken(account);

        return SignUpResponse.builder()
                .message("로그인 성공")
                .userId(userType == UserType.USER ? entityId : null)
                .managerId(userType == UserType.MANAGER ? entityId : null)
                .userType(userType.name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
