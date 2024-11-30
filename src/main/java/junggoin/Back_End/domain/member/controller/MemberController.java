package junggoin.Back_End.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 회원의 정보를 조회한다"
    )
    @ApiResponse(
            responseCode = "200",
            description = "내 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))
    )
    @GetMapping("/my-info")
    public ResponseEntity<MemberInfoResponse> getMyInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(memberService.getMemberInfo(user.getUsername()));
    }

    // 회원 가입
    @Operation(
            summary = "회원 가입",
            description = "OAuth2 인증된 사용자의 닉네임을 설정하고 회원으로 가입한다. 여기에 요청을 보내기 전에  이름, 프로필 이미지 Url 같은 정보들은 이미 입력받은 상태."
    )
    @ApiResponse(
            responseCode = "200",
            description = "회원 가입 성공",
            content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))
    )
    @PutMapping("/join")
    public ResponseEntity<MemberInfoResponse> joinMember(@AuthenticationPrincipal User user,
                                                         @NotBlank @RequestParam String nickname) {
        return ResponseEntity.ok().body(memberService.joinMember(user.getUsername(), nickname));
    }

    // 닉네임 수정
    @Operation(
            summary = "닉네임 수정",
            description = "현재 로그인한 회원의 닉네임을 수정한다"
    )
    @ApiResponse(
            responseCode = "200",
            description = "닉네임 수정 성공",
            content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))
    )
    @PutMapping("/edit-nickname")
    public ResponseEntity<MemberInfoResponse> editMemberNickname(@AuthenticationPrincipal User user,
                                                                 @NotBlank @RequestParam String nickname) {
        return ResponseEntity.ok().body(memberService.updateMemberNickname(user.getUsername(), nickname));
    }

    // 닉네임 사용 가능 여부 확인
    // 닉네임 수정 전에/ROLE_GUEST인 상태에서 회원가입을 마무리를 하기 전에 확인하는 용도
    @Operation(
            summary = "닉네임 사용 가능 여부 확인",
            description = "해당 닉네임을 사용할 수 있는지 확인한다. 현재는 중복 여부만 확인"
    )
    @ApiResponse(
            responseCode = "200",
            description = "닉네임 사용 가능 여부",
            content = @Content(schema = @Schema(implementation = MemberCheckNicknameResponse.class))
    )
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

    // 회원 해시태그 저장
    @PostMapping("/{email}/hashtags")
    public ResponseEntity<MemberHashtagResponseDto> saveHashtags(@PathVariable String email, @RequestBody MemberHashtagRequestDto request ) {
        return ResponseEntity.ok(memberService.saveMemberHashtag(email, request));
    }
}