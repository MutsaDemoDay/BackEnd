package backend.stamp.auth.service;

import backend.stamp.account.entity.Account;
import backend.stamp.account.repository.AccountRepository;
import backend.stamp.auth.dto.request.TokenReissueRequest;
import backend.stamp.auth.dto.response.TokenReissueResponse;
import backend.stamp.auth.entity.RefreshToken;
import backend.stamp.auth.repository.RefreshTokenRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenReissueService {
    private final TokenProvider tokenProvider;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 리프레시 토큰을 검증하고 새로운 Access Token과 Refresh Token을 발급합니다.
     */
    public TokenReissueResponse reissueToken(TokenReissueRequest request) {
        String oldRefreshToken = request.getRefreshToken();

        // 1. Refresh Token 유효성 검증 (서명 및 만료)
        if (!tokenProvider.validateToken(oldRefreshToken)) {
            // 유효하지 않은 리프레시 토큰 (만료되었거나 변조됨)
            throw new ApplicationException(ErrorCode.INVALID_JWT_EXCEPTION);
        }

        // 2. Refresh Token이 DB에 저장된 유효한 토큰인지 검증
        RefreshToken savedToken = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_JWT_EXCEPTION));

        // 3. 토큰 소유자(Account) 정보 획득
        Long accountId = savedToken.getAccountId();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND)); // 사용자 정보를 찾을 수 없음

        // 4. 새로운 Access Token 및 Refresh Token 생성
        String newAccessToken = tokenProvider.createAccessToken(account);
        String newRefreshToken = tokenProvider.createRefreshToken(account);

        // 5. DB에 기존 Refresh Token 삭제 및 새 Refresh Token 저장
        refreshTokenRepository.delete(savedToken);

        RefreshToken newSavedToken = RefreshToken.builder()
                .token(newRefreshToken)
                .accountId(account.getAccountId())
                .build();
        refreshTokenRepository.save(newSavedToken);

        // 6. 응답 반환
        return TokenReissueResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
