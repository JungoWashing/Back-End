package junggoin.Back_End.domain.member.controller;

import jakarta.validation.constraints.NotBlank;
import junggoin.Back_End.domain.member.dto.*;
import junggoin.Back_End.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {
    private final MemberService memberService;

    // 내 정보 조회
    @GetMapping("/my-info")
    public ResponseEntity<MemberInfoResponse> getMyInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(memberService.getMemberInfo(user.getUsername()));
    }

    // 회원 가입
    @PutMapping("/join")
    public ResponseEntity<MemberInfoResponse> joinMember(@AuthenticationPrincipal User user,
                                                         @RequestBody JoinRequestDto joinRequestDto) {
        return ResponseEntity.ok().body(memberService.joinMember(user.getUsername(), joinRequestDto));
    }

    // 닉네임 수정
    @PutMapping("/edit-nickname")
    public ResponseEntity<MemberInfoResponse> editMemberNickname(@AuthenticationPrincipal User user,
                                                                 @NotBlank @RequestParam String nickname) {
        return ResponseEntity.ok().body(memberService.updateMemberNickname(user.getUsername(), nickname));
    }

    // 닉네임 사용 가능 여부 확인
    // 닉네임 수정 전에/ROLE_GUEST인 상태에서 회원가입을 마무리를 하기 전에 확인하는 용도

    @GetMapping("/check-nickname")
    public ResponseEntity<MemberCheckNicknameResponse> checkNickname(@NotBlank @RequestParam String nickname) {
        return ResponseEntity.ok().body(memberService.checkNickname(nickname));
    }

    // 회원 해시태그 조회
    @GetMapping("/{email}/hashtags")
    public ResponseEntity<MemberHashtagResponseDto> getHashtags(@PathVariable String email) {
        return ResponseEntity.ok(memberService.getMemberHashtags(email));
    }

    // 회원 해시태그 삭제
    @DeleteMapping("/{email}/hashtags")
    public ResponseEntity<Object> removeHashtag(@PathVariable String email, @RequestParam String hashtag) {
        memberService.removeMemberHashtag(email,hashtag);
        return ResponseEntity.ok().build();
    }

    // 회원 해시태그 저장/수정
    @PostMapping("/{email}/hashtags")
    public ResponseEntity<MemberHashtagResponseDto> saveHashtags(@PathVariable String email, @RequestBody MemberHashtagRequestDto request ) {
        return ResponseEntity.ok(memberService.saveMemberHashtag(email, request.getHashtags()));
    }
}