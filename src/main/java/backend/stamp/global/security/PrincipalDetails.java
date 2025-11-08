package backend.stamp.global.security;

import backend.stamp.users.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class PrincipalDetails implements UserDetails {
    private Users users;
    public PrincipalDetails(Users users) {
        this.users = users;
    }

    //인증된 user 객체 반환
    public Users getUser() {
        return users;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 또는 사용자 권한 목록
    }


    @Override
    public String getUsername() {
        return users.getEmail(); // 사용자 식별용
    }
    @Override
    public String getPassword() {
        return null;
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

}
