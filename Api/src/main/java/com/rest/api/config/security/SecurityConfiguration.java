package com.rest.api.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable()  // Rest API 임에 따라 HTTP 기본 설정 사용하지 않음. 기본 설정은 인증 오류시 로그인 화면으로 Redirect 됨.
                .csrf().disable()       // Rest API 임에 따라 CSRF 보안 불필요.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)     // Token 사용으로 Session 불필요.
                .and()
                    .authorizeRequests()    // 다음의 Request에 대한 사용 권한 확인
                        .antMatchers("/*/signin", "/*/singup", "/*/signin/**", "/*/signup/**","/social/**").permitAll()          // 가입 및 인증은 제한 없음
                        .antMatchers(HttpMethod.GET, "helloworld/**").permitAll()   // helloworld는 제한 없음
                    .antMatchers("/*users").hasRole("ADMIN")                        // Access Denied 확인용 설정
                        .anyRequest().hasAnyRole("USER")                                 // 그외는 회원만 접근 가능
                .and()
                    .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())     // 권한 오류
                .and()
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);    // JWT Token 필터를 사용자 id, password 인증 전에 넣음.
    }

    @Override   // Swagger Resource 예외 처리
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**");
    }

}
