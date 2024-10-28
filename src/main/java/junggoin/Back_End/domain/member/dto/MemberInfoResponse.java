package junggoin.Back_End.domain.member.dto;

import junggoin.Back_End.domain.member.RoleType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class MemberInfoResponse {
    private String email;
    private String name;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createAt;
    private RoleType role;
}