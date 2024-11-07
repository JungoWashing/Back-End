package junggoin.Back_End.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import junggoin.Back_End.domain.member.RoleType;
import junggoin.Back_End.domain.member.dto.MemberInfoResponse;
import junggoin.Back_End.security.CustomOAuth2User;
import junggoin.Back_End.security.GoogleOAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

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
        String redirectUri;

        String jsessionid =request.getRequestedSessionId();

        // 닉네임 작성 필요
        if (isGuest) {
            redirectUri = targetUrl+"?"
            + "role="+URLEncoder.encode(RoleType.ROLE_GUEST.name(), StandardCharsets.UTF_8)+"&"
            + "name="+URLEncoder.encode(oAuth2UserInfo.getName(), StandardCharsets.UTF_8)+"&"
            + "email="+URLEncoder.encode(oAuth2UserInfo.getEmail(), StandardCharsets.UTF_8);

        } else {
            MemberInfoResponse memberInfoResponse = customOAuth2User.getMemberInfo();
            redirectUri = targetUrl+"?"
            + "role="+URLEncoder.encode(memberInfoResponse.getRole().name(), StandardCharsets.UTF_8)+"&"
            + "name="+URLEncoder.encode(memberInfoResponse.getName(), StandardCharsets.UTF_8)+"&"
            + "email="+URLEncoder.encode(memberInfoResponse.getEmail(), StandardCharsets.UTF_8)+"&"
            + "nickname="+URLEncoder.encode(memberInfoResponse.getNickname(), StandardCharsets.UTF_8)+"&"
            + "profile_image_url"+URLEncoder.encode(memberInfoResponse.getProfileImageUrl(), StandardCharsets.UTF_8)+"&"
            + "create_at"+URLEncoder.encode(memberInfoResponse.getCreateAt().toString(), StandardCharsets.UTF_8);
        }
        if (jsessionid != null) {
            redirectUri += "&jsessionid=" + URLEncoder.encode(jsessionid, StandardCharsets.UTF_8);
        }

        response.sendRedirect(redirectUri);
        log.info("{}",redirectUri);
    }
}