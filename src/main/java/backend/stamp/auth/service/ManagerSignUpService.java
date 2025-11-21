package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.account.entity.UserType;
import backend.stamp.account.repository.AccountRepository;
import backend.stamp.auth.dto.request.ManagerSignUpRequest;
import backend.stamp.auth.dto.response.SignUpResponse;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.TokenProvider;
import backend.stamp.manager.entity.Manager;
import backend.stamp.manager.repository.ManagerRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagerSignUpService {
    private final AccountRepository accountRepository;
    private final ManagerRepository managerRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public SignUpResponse signUp(ManagerSignUpRequest request) {

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new ApplicationException(ErrorCode.PASSWORD_MISMATCH);
        }
        if (accountRepository.findByLoginId(request.getLoginId()).isPresent()) {
            throw new ApplicationException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApplicationException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Account newAccount = new Account(
                null,
                request.getLoginId(),
                request.getEmail(),
                encodedPassword,
                UserType.MANAGER,
                LocalDateTime.now()
        );
        Account savedAccount = accountRepository.save(newAccount);

        Manager newManager = Manager.builder()
                .account(savedAccount)
                .businessNum(request.getBusinessNum())
                .build();
        Manager savedManager = managerRepository.save(newManager);

        Store newStore = Store.builder()
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .manager(savedManager)
                .build();

        storeRepository.save(newStore);


        String accessToken = tokenProvider.createAccessToken(savedAccount);
        String refreshToken = tokenProvider.createRefreshToken(savedAccount);

        return SignUpResponse.builder()
                .message("Manager 계정 생성이 완료되었습니다.")
                .userId(null)
                .managerId(savedManager.getManagerId())
                .userType(UserType.MANAGER.name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
