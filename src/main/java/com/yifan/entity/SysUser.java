package com.yifan.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年11月18日 下午3:48
 * @version 1.0
 */
@Entity
@Table(name = "sys_user")
@Data
public class SysUser implements UserDetails {

    //id
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    protected Integer userId;

    //用户名
    @Column(name = "user_name")
    private String username;

    //密码
    @Column(nullable = false)
    private String password;

    /**
     * 是否锁定
     * true: 未锁定
     * false: 锁定
     */
    @Column(name = "locked_flag")
    private Integer lockedFlag;

    @Column(name = "id_deleted")
    private Integer isDeleted;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
