package backend.stamp.global.security;


import backend.stamp.account.entity.Account;
import backend.stamp.account.repository.AccountRepository;
import backend.stamp.auth.entity.RefreshToken;
import backend.stamp.auth.repository.RefreshTokenRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${spring.security.oauth2.jwt.secret}")
    private String secretKey;

    private SecretKey key;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final long accessTokenExpirationTime = 7 * 24 * 60 * 60 * 1000L;
    private static final long refreshTokenExpirationTime = 30 * 24 * 60 * 60 * 1000L; //30일

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String createAccessToken(Account account) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + accessTokenExpirationTime);

        return Jwts.builder()
                .setSubject(account.getLoginId())
                .claim("accountId", account.getAccountId())
                .claim("userType", account.getUserType().name())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token을 생성합니다.
     */
    public String createRefreshToken(Account account) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + refreshTokenExpirationTime);
        String token = Jwts.builder()
                .setSubject(account.getLoginId())
                .claim("accountId", account.getAccountId())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token DB 저장
        refreshTokenRepository.deleteByAccountId(account.getAccountId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .accountId(account.getAccountId())
                .build();
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return claims.getBody().getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getLoginId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Authentication getAuthentication(String token) {
        String loginId = getLoginId(token);

        Account account = accountRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        PrincipalDetails principalDetails = new PrincipalDetails(account);

        return new UsernamePasswordAuthenticationToken(
                principalDetails,
                null,
                principalDetails.getAuthorities()
        );
    }
}
