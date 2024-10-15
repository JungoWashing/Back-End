package junggoin.Back_End.security;

import junggoin.Back_End.security.handler.CustomLogoutSuccessHandler;
import junggoin.Back_End.security.handler.CustomOAuth2FailureHandler;
import junggoin.Back_End.security.handler.CustomOAuth2SuccessHandler;
import junggoin.Back_End.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))
                .authorizeHttpRequests(auth -> auth
                        // 인증이 필요없는 url
                        .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/login/**", "/auth/**", "/oauth2/**")
                        .permitAll()
                        .requestMatchers("/", "/error", "/favicon.ico", "/*.png", "/*.gif", "/*.svg", "/*.jpg", "/*.html", "/*.css", "/*.js")
                        .permitAll()
//                        .requestMatchers("/api/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                        // swagger 인증 설정이 안돼있어서 일단 permitAll
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated())
                .logout(logoutConfigurer ->
                        logoutConfigurer
                                .logoutUrl("/logout")
                                .invalidateHttpSession(true)    // 로그아웃 후 세션 초기화
                                .deleteCookies("JSESSIONID")    // 로그아웃 후 쿠키 제거, 하지만 제거가 안됨...
                                .logoutSuccessHandler(customLogoutSuccessHandler)
                )
                // oauth2 로그인 url : /oauth2/authorization/google
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(
                                userInfoEndPointConfig ->
                                        userInfoEndPointConfig.userService(customOAuth2UserService)
                        )
                        .failureHandler(customOAuth2FailureHandler)
                        .successHandler(customOAuth2SuccessHandler))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    // 인증되지 않은 요청에 대해 401 반환
                    httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                })
        ;
        return http.build();
    }

    @Value("${server_url}")
    private String serverUrl;

    // cors 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin(serverUrl);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}