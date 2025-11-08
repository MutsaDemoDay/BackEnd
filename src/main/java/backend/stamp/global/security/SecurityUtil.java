package backend.stamp.global.security;

import backend.stamp.account.entity.Account;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security Context에서 사용자 정보를 가져오는 유틸리티 클래스
 */
public class SecurityUtil {
    private SecurityUtil() {}

    /**
     * 현재 Security Context에 저장된 Account 정보를 가져옵니다.
     * @return 현재 로그인된 Account 객체
     * @throws ApplicationException 인증 정보가 없을 경우
     */
    public static Account getCurrentAccount() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ApplicationException(ErrorCode.INVALID_JWT_EXCEPTION);
        }

        if (authentication.getPrincipal() instanceof PrincipalDetails principalDetails) {
            return principalDetails.getAccount();
        }
        
        throw new ApplicationException(ErrorCode.INVALID_JWT_EXCEPTION);
    }
}
