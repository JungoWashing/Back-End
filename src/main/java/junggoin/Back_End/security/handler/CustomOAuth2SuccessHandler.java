package junggoin.Back_End.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import junggoin.Back_End.domain.jwt.service.TokenService;
import junggoin.Back_End.domain.member.RoleType;
import junggoin.Back_End.domain.member.dto.MemberInfoResponse;
import junggoin.Back_End.security.CustomOAuth2User;
import junggoin.Back_End.security.GoogleOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final TokenService tokenService;

    @Value("${flutter.uri-scheme}")
    private String flutterUri;

    @Value("${server_url}")
    private String serverUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // ROLE_GUEST 인 경우 회원 가입을 정상적으로 마치려면 /api/memebers/join 에 회원가입 요청을 보내야함

        // 사용자 정보 가져오기
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        CustomOAuth2User customOAuth2User = (CustomOAuth2User)oauthToken.getPrincipal();

        // OAuth2 사용자 정보
        GoogleOAuth2UserInfo oAuth2UserInfo = customOAuth2User.getOAuth2UserInfo();

        // 사용자 역할 ROLE_GUEST 인지 확인
        boolean isGuest = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(RoleType.ROLE_GUEST.name()));

        // User Agent 확인
        String userAgent = request.getHeader("User-Agent");
        boolean isMobile = userAgent != null && (userAgent.contains("Android") || userAgent.contains("iPhone"));
        String targetUrl;

        if(isMobile){
            targetUrl = flutterUri+"://";
        }else{
            targetUrl = serverUrl;
        }

        String redirectUri, accessToken;
        String email = oAuth2UserInfo.getEmail();
        String role = isGuest ? RoleType.ROLE_GUEST.name() : customOAuth2User.getMemberInfo().getRole().name();

        if (tokenService.isLoggedIn(email)) {
            accessToken = tokenService.findAccessToken(email);
        } else {
            accessToken = tokenService.createAccessToken(email, role);
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("email", oAuth2UserInfo.getEmail());
        if (isGuest) {
            uriBuilder.queryParam("name", oAuth2UserInfo.getName());
        }
        uriBuilder.queryParam("role",  role)
                .queryParam("token", accessToken);

        redirectUri = uriBuilder.toUriString();
        response.sendRedirect(redirectUri);
        log.info("{}",redirectUri);
    }
}