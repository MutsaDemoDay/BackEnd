package backend.stamp.global.security;

import backend.stamp.account.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class PrincipalDetails implements UserDetails {
    private Account account;

    public PrincipalDetails(Account account) {
        this.account = account;
    }

    /**
     * 사용자의 권한(UserType) 목록을 반환합니다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + account.getUserType().name())
        );
    }

    /**
     * 사용자를 식별하는 고유한 값을 반환합니다. (여기서는 로그인 ID 사용)
     */
    @Override
    public String getUsername() {
        return account.getLoginId();
    }

    /**
     * 사용자의 암호화된 비밀번호를 반환합니다.
     */
    @Override
    public String getPassword() {
        return account.getPassword();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 필요시 Account 엔티티를 반환하는 Getter를 추가하여 서비스 레이어에서 활용할 수 있습니다.
    public Account getAccount() {
        return this.account;
    }

}
