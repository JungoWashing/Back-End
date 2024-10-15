package junggoin.Back_End.security.service;

import junggoin.Back_End.security.GoogleOAuth2UserInfo;
import junggoin.Back_End.security.CustomOAuth2User;
import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2 제공자로부터 사용자 정보 불러오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 로그
        oAuth2User.getAttributes().forEach((key, value) -> log.info("{}: {}", key, value));
        log.info("getAccessToken: {}",userRequest.getAccessToken().getTokenValue());

        // OAuth2 사용자 정보를 객체로 변환
        GoogleOAuth2UserInfo oauth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());

        // 소셜 ID를 이용해 DB에서 멤버를 찾아 없으면 신규 생성
        Member member = memberService.findMemberBySocialId(oauth2UserInfo.getSocialId())
                .orElseGet(()->memberService.createMember(oauth2UserInfo));

        // 프로필 이미지가 변경된 경우
        if (!member.getProfileImageUrl().equals(oauth2UserInfo.getPictureUrl())) {
            member = memberService.updateMemberProfileImage(member,oauth2UserInfo.getPictureUrl());
        }

        // 이름이 변경된 경우
        if (!member.getName().equals(oauth2UserInfo.getName())) {
            member = memberService.updateMemberName(member,oauth2UserInfo.getName());
        }

        return new CustomOAuth2User(oauth2UserInfo, member);
    }
}
