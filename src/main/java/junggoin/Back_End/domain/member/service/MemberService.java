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
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService  {
    private final MemberRepository memberRepository;

    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public Optional<Member> findMemberBySocialId(String socialId) {
        return memberRepository.findMemberBySocialId(socialId);
    }
    
    @Transactional
    public Member createMember(GoogleOAuth2UserInfo oAuth2UserInfo) {
        Member member = Member.builder()
                .socialId(oAuth2UserInfo.getSocialId())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .nickname(generateUniqueNickname(oAuth2UserInfo.getName()))
                .role(RoleType.ROLE_USER)
                .profileImageUrl(oAuth2UserInfo.getPictureUrl())
                .build();
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMemberName(Member member, String newName) {
        member.updateName(newName);
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMemberProfileImage(Member member, String profileImageUrl) {
        member.updateProfileImageUrl(profileImageUrl);
        return memberRepository.save(member);
    }

    /**
     *      OAuth를 통해 받아온 이름을 닉네임으로 설정하는데
     *      이미 해당 닉네임을 가진 회원이 존재하면 이름+0000~ 9999 랜덤 문자열로 닉네임 설정
     */
    private String generateUniqueNickname(String baseName) {
        StringBuilder nickname = new StringBuilder(baseName);
        while (memberRepository.existsMemberByNickname(nickname.toString())) {
            int randomSuffix = new Random().nextInt(10000);  // 0 ~ 9999 랜덤 숫자 생성
            nickname.setLength(baseName.length());
            nickname.append(String.format("%04d", randomSuffix));
        }
        return nickname.toString();
    }
}
