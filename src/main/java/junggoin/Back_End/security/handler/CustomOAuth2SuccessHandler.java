package junggoin.Back_End.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import junggoin.Back_End.security.CustomOAuth2User;
import junggoin.Back_End.security.GoogleOAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_OK);

        // 사용자 정보 가져오기
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        CustomOAuth2User customOAuth2User = (CustomOAuth2User)oauthToken.getPrincipal();

        // OAuth2 사용자 정보
        GoogleOAuth2UserInfo oAuth2UserInfo = customOAuth2User.getOAuth2UserInfo();

        Map<String, String> responseMap = new LinkedHashMap<>();
        responseMap.put("message", "닉네임 작성 필요");
        // email, name 은 프론트에서 read-only 로 보여주기만 하면 될 듯
        responseMap.put("email", oAuth2UserInfo.getEmail());
        responseMap.put("name", oAuth2UserInfo.getName());

        response.getWriter().write(objectMapper.writeValueAsString(responseMap));
        log.debug("{}",authentication);
    }
}