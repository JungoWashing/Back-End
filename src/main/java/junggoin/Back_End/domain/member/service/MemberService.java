package junggoin.Back_End.domain.member.service;

import junggoin.Back_End.domain.member.dto.MemberCheckNicknameResponse;
import junggoin.Back_End.domain.member.dto.MemberInfoResponse;
import junggoin.Back_End.security.CustomOAuth2User;
import junggoin.Back_End.security.GoogleOAuth2UserInfo;
import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.RoleType;
import junggoin.Back_End.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
    // CustomOAuth2Service 에서 구글 로그인 후 임시 회원을 만드는데 사용
    @Transactional
    public Member createMember(GoogleOAuth2UserInfo oAuth2UserInfo) {
        Member member = Member.builder()
                .socialId(oAuth2UserInfo.getSocialId())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .role(RoleType.ROLE_GUEST)
                .profileImageUrl(oAuth2UserInfo.getPictureUrl())
                .build();
        return memberRepository.save(member);
    }

    // 회원 가입
    // 최초 구글 로그인 시 받은 정보를 토대로 입력할 수 있는 것들만 입력한 후 ROLE_GUEST 권한을 주고 Member Entity 를 생성함(회원 생성)
    // 이후 FrontEnd 에 유저 정보를 넘겨준 뒤, /api/members/join 에 request param 으로 nickname 을 put 요청으로 보내면 회원 가입 완료
    @Transactional
    public MemberInfoResponse joinMember(CustomOAuth2User customOAuth2User, String nickname) {
        Member member = memberRepository.findMemberByEmail(customOAuth2User.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원: " + customOAuth2User.getUsername()));

        // 닉네임 중복 확인
        if (memberRepository.existsMemberByNickname(nickname)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다");
        }

        // 회원가입 처리
        member.updateNickname(nickname);
        member.updateRole(RoleType.ROLE_USER);

        // SecurityContext 갱신
        updateSecurityContextWithNewUser(new CustomOAuth2User(customOAuth2User.getOAuth2UserInfo(), member));
        return toMemberInfo(member);
    }

    // 닉네임 수정
    @Transactional
    public MemberInfoResponse updateMemberNickname(CustomOAuth2User customOAuth2User, String nickname) {
        Member member = memberRepository.findMemberByEmail(customOAuth2User.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원: " + customOAuth2User.getUsername()));

        // 닉네임 중복 확인
        if (memberRepository.existsMemberByNickname(nickname)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다");
        }

        member.updateNickname(nickname);

        // SecurityContext 갱신
        updateSecurityContextWithNewUser(new CustomOAuth2User(customOAuth2User.getOAuth2UserInfo(), member));
        return toMemberInfo(member);
    }

    // 닉네임 중복 확인
    public MemberCheckNicknameResponse checkNickname(String nickname) {
        return new MemberCheckNicknameResponse(!memberRepository.existsMemberByNickname(nickname));
    }

    // SecurityContext 갱신
    // SecurityContextHolder 에 저장되는 인증 정보(CustomOAuth2User)는 로그인 된 상태에서 유저의 정보를 불러올 때에도 사용됨
    // Member 의 닉네임, Role 수정 후 CustomOAuth2User 에 저장된 Member 도 동기화 시켜주는 용도
    // 이름과 프로필 이미지 Url 은 구글 로그인 할 때만 변경 됨
    private void updateSecurityContextWithNewUser(CustomOAuth2User updatedUser) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        updatedUser,  // 새 CustomOAuth2User
                        null,
                        updatedUser.getAuthorities()
                );

        // SecurityContextHolder 에 수정된 Member 가 들어간 Authentication 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static MemberInfoResponse toMemberInfo(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("회원은 null 일 수 없습니다");
        }

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
