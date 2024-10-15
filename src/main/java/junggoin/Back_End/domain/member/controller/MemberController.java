package junggoin.Back_End.domain.member.controller;

import junggoin.Back_End.security.CustomOAuth2User;
import junggoin.Back_End.domain.member.Member;
import junggoin.Back_End.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {
    private final MemberService memberService;

    // 회원 정보 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<Member> getMember(@PathVariable Long memberId) {
        return ResponseEntity.ok().body(memberService.findMemberById(memberId));
    }

    // 내 정보 조회
    @GetMapping("/my-info")
    public ResponseEntity<Member> getMember(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ResponseEntity.ok().body(customOAuth2User.getMember());
    }
}
