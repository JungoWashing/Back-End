package junggoin.Back_End.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import junggoin.Back_End.domain.jwt.service.TokenService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException, AuthenticationException {
        // 토큰 추출
        try {
            String token = tokenService.resolveToken(request);

            if (tokenService.validateAccessToken(token)) {
                // 액세스 토큰이 유효하면 토큰으로부터 유저 정보를 받아옴
                Authentication authentication = getAuthentication(token);

                // SecurityContext 에 Authentication 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    // 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        Claims claims = tokenService.getAllClaims(token);
        String username = tokenService.getEmail(claims);
        List<SimpleGrantedAuthority> authorities = tokenService.getAuthorities(claims);

        User principal = new User(username,"",authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}