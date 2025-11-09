package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.auth.repository.RefreshTokenRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 사용자의 Refresh Token을 DB에서 삭제하여 토큰을 무효화합니다.
     */
    public void logout() {

        Account currentAccount = SecurityUtil.getCurrentAccount();

        try {
            refreshTokenRepository.deleteByAccountId(currentAccount.getAccountId());
        } catch (Exception e) {

            throw new ApplicationException(ErrorCode.INTERNAL_SERVER_EXCEPTION);
        }

    }
}
