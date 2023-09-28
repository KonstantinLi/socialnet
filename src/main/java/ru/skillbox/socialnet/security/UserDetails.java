package ru.skillbox.socialnet.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

//TODO Опять сонар. Хочет переименовать класс, например в UserDetailsImpl можно?
@SuppressWarnings("unused")
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
