package junggoin.Back_End.security;

import java.util.Map;

/**
 * Google OAuth2 사용자 정보를 담는 클래스
 * 사용자의 기본 정보(이메일, 이름, 프로필 사진 등)를 OAuth2 인증 응답에서 추출함
 */
public record GoogleOAuth2UserInfo(Map<String, Object> attributes) {
    public String getSocialId() {
        return String.valueOf(attributes.get("sub"));
    }

    public String getEmail() {
        return String.valueOf(attributes.get("email"));
    }

    public String getName() {
        return String.valueOf(attributes.get("name"));
    }

    public String getPictureUrl() {
        return String.valueOf(attributes.get("picture"));
    }

    public String getDomain() {
        return String.valueOf(attributes.get("hd"));
    }
}