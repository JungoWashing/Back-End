package junggoin.Back_End.domain.member.service;

import junggoin.Back_End.security.GoogleOAuth2UserInfo;
import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.RoleType;
import junggoin.Back_End.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email);
    }

    // 회원 생성
    @Transactional
    public Member createMember(GoogleOAuth2UserInfo oAuth2UserInfo) {
        Member member = Member.builder()
                .socialId(oAuth2UserInfo.getSocialId())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .role(RoleType.ROLE_USER)
                .profileImageUrl(oAuth2UserInfo.getPictureUrl())
                .build();
        return memberRepository.save(member);
    }
}
