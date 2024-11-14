package junggoin.Back_End.domain.jwt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    public static final long ACCESS_TOKEN_TIME = Duration.ofDays(30).toMillis(); // 30분
//    public static final long REFRESH_TOKEN_TIME = Duration.ofDays(7).toMillis(); // 7일
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.jwt.secret}")
    private String secretString;
    private SecretKey secretKey;
    private JwtParser jwtParser;

    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        secretKey = new SecretKeySpec(secretString.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    // 토큰의 Claim 디코딩
    public Claims getAllClaims(String token) {
        String cleanedToken = token.split(" ")[1].trim();
        return jwtParser.parseSignedClaims(cleanedToken).getPayload();
    }

    public String getEmail(Claims claims) {
        return claims.get("email", String.class);
    }

    public List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get("role", String.class)));
    }

    // request에서 token 추출
    public String resolveToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(TokenService.HEADER);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(TokenService.TOKEN_PREFIX)) {
            return headerAuth;
        }
        return null;
    }

    // 입력 받은 email을 key값으로 하는 access token을 반환, 없으면 null 반환
    public String findAccessToken(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    // 토큰 생성
    public String createAccessToken(String email, String role) {
        String token = TOKEN_PREFIX + Jwts.builder()
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_TIME))
                .signWith(secretKey)
                .compact();
        redisTemplate.opsForValue().set(email, token, ACCESS_TOKEN_TIME, TimeUnit.SECONDS);
        return token;
    }

    public void deleteAccessToken(String token){
        // 토큰에서 claim 추출
        Claims claims = getAllClaims(token);

        String email = getEmail(claims);

        redisTemplate.delete(email);
    }

    // bearer 확인 + 만료일자 확인
    public boolean validateAccessToken(String token) {
        // 텍스트 있는지 확인
        if (!StringUtils.hasText(token)) {
            throw new JwtException("token 없음");
        }

        // Bearer 검증
        if (!token.startsWith(TOKEN_PREFIX)) {
            throw new JwtException("유효하지 않은 token");
        }

        // 토큰에서 claim 추출
        Claims claims = getAllClaims(token);

        String email = getEmail(claims);

        // email : access token 쌍 있는지 확인
        if(!this.isLoggedIn(email)){
            throw new JwtException("유효하지 않은 token");
        }

        // 유효기간 확인
        Date expiration = claims.getExpiration();
        log.info("expire at: {}", expiration);

        if (expiration.before(new Date())) {
            throw new ExpiredJwtException(Jwts.header().build(), claims, expiration.toString());
        }

        return true;
    }

    public Boolean isLoggedIn(String email) {
        return redisTemplate.hasKey(email);
    }


    // 만료된 토큰에서 정보 가져올 때 사용. (토큰 유효성 검사 x) base64로 디코딩
    public Map<String,Object> getPayloadFromExpiredToken(String expiredToken) throws JsonProcessingException {
        String[] tokenParts = expiredToken.split("\\.");
        if (tokenParts.length < 2) {
            throw new IllegalArgumentException("잘못된 토큰 형식");
        }

        String payloadJson = new String(Base64.getDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);
        Map<String, Object> payload = objectMapper.readValue(payloadJson, Map.class);
        return payload;
    }
}