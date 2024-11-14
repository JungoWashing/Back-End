package junggoin.Back_End.domain.member.service;

import junggoin.Back_End.domain.jwt.service.TokenService;
import junggoin.Back_End.domain.member.dto.MemberCheckNicknameResponse;
import junggoin.Back_End.domain.member.dto.MemberInfoResponse;
import junggoin.Back_End.security.GoogleOAuth2UserInfo;
import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.RoleType;
import junggoin.Back_End.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

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
    public MemberInfoResponse joinMember(String email, String nickname) {
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원: " + email));

        // 닉네임 중복 확인
        if (memberRepository.existsMemberByNickname(nickname)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다");
        }

        // 회원가입 처리
        member.updateNickname(nickname);
        member.updateRole(RoleType.ROLE_USER);

        String token = tokenService.findAccessToken(email);

        // SecurityContext 갱신
        updateSecurityContextWithNewUser(new User(email,token,Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name()))));
        return toMemberInfo(member);
    }

    // 닉네임 수정
    @Transactional
    public MemberInfoResponse updateMemberNickname(String email, String nickname) {
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원: " + email));

        // 닉네임 중복 확인
        if (memberRepository.existsMemberByNickname(nickname)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다");
        }

        member.updateNickname(nickname);

        return toMemberInfo(member);
    }

    // 닉네임 중복 확인
    public MemberCheckNicknameResponse checkNickname(String nickname) {
        return new MemberCheckNicknameResponse(!memberRepository.existsMemberByNickname(nickname));
    }
    
    // 이메일로 회원정보 조회
    public MemberInfoResponse getMemberInfo(String email) {
        Member member =  memberRepository.findMemberByEmail(email).orElseThrow(() ->  new RuntimeException("존재하지 않는 회원: " + email));
        return toMemberInfo(member);
    }

    // SecurityContext 갱신
    private void updateSecurityContextWithNewUser(User updatedUser) {
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
