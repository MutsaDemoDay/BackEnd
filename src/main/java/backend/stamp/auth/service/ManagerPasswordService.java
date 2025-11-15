package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.account.repository.AccountRepository;
import backend.stamp.auth.dto.request.FindManagerPasswordRequest;
import backend.stamp.auth.dto.response.FindPasswordResponse;
import backend.stamp.auth.entity.ResetToken;
import backend.stamp.auth.repository.ResetTokenRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.TokenProvider;
import backend.stamp.manager.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Transactional
public class ManagerPasswordService {
    private final AccountRepository accountRepository;
    private final ManagerRepository managerRepository;
    private final TokenProvider tokenProvider;
    private final ResetTokenRepository resetTokenRepository;

    private static final long RESET_TOKEN_EXPIRY_MINUTES = 60;


    public FindPasswordResponse findManagerPassword(FindManagerPasswordRequest request) {

        Account account = accountRepository.findByLoginId(request.getLoginId())
                .filter(a -> a.getEmail().equals(request.getEmail()))
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        managerRepository.findByAccount(account)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        String resetToken = tokenProvider.createResetToken(account);

        ResetToken resetTokenEntity = ResetToken.builder()
                .token(resetToken)
                .accountId(account.getAccountId())
                .expiryDate(LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRY_MINUTES))
                .build();

        resetTokenRepository.save(resetTokenEntity);

        return FindPasswordResponse.builder()
                .resetToken(resetToken)
                .build();
    }
}
