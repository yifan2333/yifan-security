package com.yifan.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yifan.entity.SysUser;

/** 
 * 
 *
 * @author: wuyifan
 * @since: 2019年12月05日 上午11:48
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<SysUser, Integer> {

    SysUser findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteByUsername(String username);
}
