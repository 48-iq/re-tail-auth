package dev.ilya_anna.auth_service.security;

import dev.ilya_anna.auth_service.entities.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


@Getter
@AllArgsConstructor
public class DaoUserDetails implements UserDetails {

    private User user;

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(u -> new SimpleGrantedAuthority(u.getName()))
                .toList();
    }

}
