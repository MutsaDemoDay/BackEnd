package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.account.repository.AccountRepository;
import backend.stamp.auth.dto.request.ResetPasswordRequest;
import backend.stamp.auth.entity.ResetToken;
import backend.stamp.auth.repository.ResetTokenRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class ResetPasswordService {
    private final AccountRepository accountRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ResetTokenRepository resetTokenRepository;

    public void resetPassword(String resetToken, ResetPasswordRequest request) {

        if (!tokenProvider.validateToken(resetToken)) {
            throw new ApplicationException(ErrorCode.INVALID_JWT_EXCEPTION);
        }

        ResetToken tokenEntity = resetTokenRepository.findByToken(resetToken)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_JWT_EXCEPTION));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND);
        }

        Long accountId = tokenEntity.getAccountId();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        resetTokenRepository.delete(tokenEntity);
    }
}
