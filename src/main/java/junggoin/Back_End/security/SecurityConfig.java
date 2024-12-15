package junggoin.Back_End.security;

import junggoin.Back_End.security.filter.JwtAuthenticationFilter;
import junggoin.Back_End.security.handler.*;
import junggoin.Back_End.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomLogoutHandler customLogoutHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 인증이 필요없는 url
                        .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/login/**", "/auth/**", "/oauth2/**")
                        .permitAll()
                        .requestMatchers("/", "/error", "/favicon.ico", "/*.png", "/*.gif", "/*.svg", "/*.jpg", "/*.html", "/*.css", "/*.js")
                        .permitAll()
                        .requestMatchers("/chat/message/**","/ws-stomp/**")
                        .permitAll()
//                        .requestMatchers("/api/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER","ROLE_GUEST")
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated())
                .logout(logoutConfigurer ->
                        logoutConfigurer
                                .logoutUrl("/logout")
                                .logoutSuccessHandler(customLogoutHandler)
                                .addLogoutHandler(customLogoutHandler)
                )
                // oauth2 로그인 url : /oauth2/authorization/google
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(
                                userInfoEndPointConfig ->
                                        userInfoEndPointConfig.userService(customOAuth2UserService)
                        )
                        // 인하대 이메일만 로그인 가능하게 하기 위해 customAuthorizationRequestResolver 설정
                        .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                                .authorizationRequestResolver(customAuthorizationRequestResolver))
                        .failureHandler(customOAuth2FailureHandler)
                        .successHandler(customOAuth2SuccessHandler))
                // 인증 관련 예외처리
                .exceptionHandling((exceptionConfig) -> exceptionConfig.authenticationEntryPoint(customAuthenticationEntryPoint))
                // jwt 인증 필터
                .addFilterBefore(jwtAuthenticationFilter, LogoutFilter.class)
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
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}