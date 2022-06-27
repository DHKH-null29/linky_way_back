package com.wnis.linkyway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.redis.RedisProvider;
import com.wnis.linkyway.security.filter.JwtAuthenticationFilter;
import com.wnis.linkyway.security.filter.JwtAuthorizationFilter;
import com.wnis.linkyway.security.jwt.JwtProvider;
import com.wnis.linkyway.security.provider.JwtAuthenticationProvider;
import com.wnis.linkyway.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;


@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final RedisProvider redisProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .antMatcher("/h2-console/**").headers().frameOptions().disable()
                .and()
                .antMatcher("/**").cors().configurationSource(corsConfigurationSource())
                .and()
                .logout().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/**")
                .permitAll()
                .anyRequest()
                .authenticated();
        http
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthenticationProvider authenticationProvider(MemberService memberService) {
        return new JwtAuthenticationProvider(memberService);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtLoginFilter = new JwtAuthenticationFilter(objectMapper, jwtProvider, redisProvider);
        jwtLoginFilter.setAuthenticationManager(authenticationManagerBean());
        return jwtLoginFilter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JwtAuthorizationFilter(authenticationManagerBean(), jwtProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        return new CorsConfig().corsConfigurationSource();
    }

}
