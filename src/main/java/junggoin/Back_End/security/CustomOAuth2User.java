package junggoin.Back_End.security;

import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.dto.MemberInfoResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * SpringSecurity 의 SecurityContextHolder 에 들어갈 인증 정보
 */
@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User, UserDetails {
    private final GoogleOAuth2UserInfo oAuth2UserInfo;
    private final Member member;

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2UserInfo.attributes();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().name()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public String getName() {
        return member.getName();
    }

    public MemberInfoResponse getMemberInfo() {
        return MemberInfoResponse.builder()
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .createAt(member.getCreated_at())
                .role(member.getRole())
                .build();
    }
}