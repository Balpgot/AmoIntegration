package com.sender.security;

import com.sender.PropertiesStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public SecurityConfig() {
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(PropertiesStorage.SECURITY_LOGIN).password(passwordEncoder().encode(PropertiesStorage.SECURITY_PASSWORD))
                .authorities("ROLE_ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/webhook").permitAll()
                .antMatchers(HttpMethod.GET, "/reg").permitAll()
                .antMatchers("/", "/admin", "/admin/**", "/admin/search/**").authenticated()
                .antMatchers("/login").permitAll()
                .and()
                .csrf().disable()
                .formLogin();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
