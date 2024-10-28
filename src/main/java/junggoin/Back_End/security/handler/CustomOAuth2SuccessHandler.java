package junggoin.Back_End.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import junggoin.Back_End.domain.member.RoleType;
import junggoin.Back_End.domain.member.dto.MemberInfoResponse;
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

        // ROLE_GUEST 인 경우 회원 가입을 정상적으로 마치려면 /api/memebers/join 에 회원가입 요청을 보내야함

        // 사용자 정보 가져오기
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        CustomOAuth2User customOAuth2User = (CustomOAuth2User)oauthToken.getPrincipal();

        // OAuth2 사용자 정보
        GoogleOAuth2UserInfo oAuth2UserInfo = customOAuth2User.getOAuth2UserInfo();

        // 사용자 역할 ROLE_GUEST 인지 확인
        boolean isGuest = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(RoleType.ROLE_GUEST.name()));

        // 닉네임 작성 필요
        if(isGuest){
            Map<String, String> responseMap = new LinkedHashMap<>();
            responseMap.put("message", "닉네임 작성 필요");
            // email, name 은 프론트에서 read-only 로 보여주기만 하면 될 듯
            responseMap.put("email", oAuth2UserInfo.getEmail());
            responseMap.put("name", oAuth2UserInfo.getName());
            responseMap.put("role", RoleType.ROLE_GUEST.name());

            response.getWriter().write(objectMapper.writeValueAsString(responseMap));
        }else{
            MemberInfoResponse memberInfoResponse = customOAuth2User.getMemberInfo();
            // localdatetime 직렬화 하기위해 아래와 같이 작성
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            response.getWriter().write(objectMapper.writeValueAsString(memberInfoResponse));
        }

        log.debug("Authentication successful: {}", authentication);
    }
}