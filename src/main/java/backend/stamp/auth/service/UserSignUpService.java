
package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.account.entity.UserType;
import backend.stamp.account.repository.AccountRepository;
import backend.stamp.auth.dto.request.UserSignUpRequest;
import backend.stamp.auth.dto.response.SignUpResponse;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.TokenProvider;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSignUpService {

    private final UsersRepository usersRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;


    public SignUpResponse signUp(UserSignUpRequest request) {

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            // TODO: ErrorCode.PASSWORD_MISMATCH 로 변경 필요
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND);
        }
        if (accountRepository.findByLoginId(request.getLoginId()).isPresent()) {
            // TODO: ErrorCode.DUPLICATE_LOGIN_ID 로 변경 필요
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND);
        }
        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            // TODO: ErrorCode.DUPLICATE_EMAIL 로 변경 필요
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Account newAccount = new Account(
                null,
                request.getLoginId(),
                request.getEmail(),
                encodedPassword,
                UserType.USER,
                request.getPhone(),
                LocalDateTime.now()
        );
        newAccount = accountRepository.save(newAccount);

        Users newUser = Users.builder()
                .account(newAccount)
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .nickname("임시닉네임_" + newAccount.getLoginId())
                .stampSum(0)
                .build();
        newUser = usersRepository.save(newUser);

        String accessToken = tokenProvider.createAccessToken(newAccount);

        String refreshToken = tokenProvider.createRefreshToken(newAccount);

        return SignUpResponse.builder()
                .message("User 계정 생성이 완료되었습니다.")
                .userId(newUser.getUserId())
                .managerId(null)
                .userType(UserType.USER.name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
