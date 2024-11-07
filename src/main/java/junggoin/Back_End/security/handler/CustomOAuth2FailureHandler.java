package junggoin.Back_End.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        // 순서 지키고 싶어서 LinkedHashMap 사용
        // Response 클래스 만들어도 되지만 여기서 더 수정될 것 같진 않아서 이렇게 구현
        Map<String, String> responseMap = new LinkedHashMap<>();
        responseMap.put("error", "로그인 실패");
        responseMap.put("message", exception.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(responseMap));
    }
}