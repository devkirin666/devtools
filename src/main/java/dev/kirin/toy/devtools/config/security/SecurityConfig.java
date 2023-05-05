package dev.kirin.toy.devtools.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, @Value("${server.port}") int port) throws Exception {
        return http.requestMatcher(new PortMatcher(port, true))
                .authorizeRequests()
                    .antMatchers("/docs/**").hasRole("ADMIN")
                    .antMatchers("/api/**").hasRole("ADMIN")
                    .antMatchers("/view/**").hasRole("ADMIN")
                    .anyRequest().permitAll()
                .and()
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                    .formLogin()
                .and()
                .build();
    }
//
//    @Bean
//    @Order(BEAN_ORDER)
//    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .antMatcher("/docs/**")
//                .antMatcher("/api/**")
//                .antMatcher("/view/**")
//                .authorizeRequests()
//                .anyRequest()
//                .hasRole("ADMIN")
////                .and()
////                    .antMatcher("/login**")
////                    .antMatcher("/logout**")
////                    .antMatcher("/error**")
////                    .authorizeRequests()
////                    .anyRequest()
////                    .permitAll()
//                .and()
//                .sessionManagement()
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                .and()
//                    .formLogin()
//                    .permitAll()
//                .and()
//                .build();
//    }
}
