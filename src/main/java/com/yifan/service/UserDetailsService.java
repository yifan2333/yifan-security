package com.yifan.service;

import javax.annotation.Resource;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.yifan.dao.UserRepository;
import com.yifan.entity.SysUser;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年12月05日 上午10:54
 * @version 1.0
 */
@Service
public class UserDetailsService {

    @Resource
    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;

    public SysUser queryByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public void createUser(UserDetails user) {
        userRepository.save((SysUser) user);
    }


    public void updateUser(UserDetails user) {
        userRepository.saveAndFlush((SysUser) user);
    }


    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }


    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext()
                .getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context "
                            + "for current user.");
        }
        String username = currentUser.getName();
        SysUser sysUser = userRepository.findByUsername(username);
        if (sysUser == null) {
            throw new IllegalStateException("Current user doesn't exist in database.");
        }

        // If an authentication manager has been set, re-authenticate the user with the
        // supplied password.
        if (authenticationManager != null) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
        }
        sysUser.setPassword(newPassword);
        userRepository.saveAndFlush(sysUser);
    }


    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = userRepository.findByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("can`t load user by username: " + username);
        }

        return sysUser;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
