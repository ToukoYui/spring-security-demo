package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled =true) // 用来开启@PreAuthorize
public class SecurityConfiguration {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        /*
                         权限验证两种方式，一种在SecurityFilterChain中配置，另一种在controller方法中使用注解@PreAuthorize
                         */
//                        .antMatchers("/test/product").hasAnyRole("ADMIN", "USER")
//                        .antMatchers("/test/admin").hasRole("ADMIN") // 该路径只能admin角色访问

                        /*
                         hasRole方法验证数据时会加上ROLE_前缀，因此数据库数据要加上前缀，比如ROLE_ADMIN
                         但在方法中不需要加上
                         */
//                        .antMatchers("/test/product").hasAnyAuthority("ADMIN", "USER")
//                        .antMatchers("/test/admin").hasAuthority("ADMIN") // 该路径只能admin角色访问
                        .anyRequest().authenticated() // 设置所有请求都要认证，该句要在后面加上，在前面加会报错
                )
                .httpBasic(withDefaults());
        return http.build();
    }

//      基于内存的查询
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("yukino").password("123456").roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }

    @Bean
    public AuthenticationProvider getProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                final String username = authentication.getName();
                final String password = authentication.getCredentials().toString();
                // 获取封装用户信息的对象
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                // 进行密码的比对
//                boolean flag = bCryptPasswordEncoder.matches(password, userDetails.getPassword());
                // 校验通过
                if (password.equals(userDetails.getPassword())) {
                    System.out.println("密码正确");
                    // 将权限信息也封装进去
                    return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
                }
                throw new AuthenticationException("用户密码错误"){};
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return true;
            }
        };

    }


}
