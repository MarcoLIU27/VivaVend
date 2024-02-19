package com.qiuzhitech.onlineshopping_03.db.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OnlineShoppingUserDetail implements UserDetails {
    private OnlineShoppingUser user;

    private boolean enable;

    private List<GrantedAuthority> authorities;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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
        return this.enable;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof OnlineShoppingUser && this.user.getName().equals(((OnlineShoppingUser) obj).getName());
    }

    @Override
    public int hashCode() {
        return this.user.getName().hashCode();
    }
}