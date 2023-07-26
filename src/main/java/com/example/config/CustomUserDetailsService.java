package com.example.config;

import com.example.mapper.UserMapper;
import com.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户
        User user = userMapper.findOneByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " was not found in db");
        }
        // 2. 设置角色
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        String dbRole = user.getRole(); //获取权限
        if(StringUtils.isEmpty(dbRole)){
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("DEFAULT_ROLE");
            grantedAuthorities.add(grantedAuthority);
        }else{
            String [] roles = dbRole.split(",");
            for (String r : roles){
                System.out.println("r = " + r);
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(r);
                grantedAuthorities.add(grantedAuthority);
            }
        }
        return new org.springframework.security.core.userdetails.User(username,
                user.getPassword(), grantedAuthorities);
    }
}
