package junggoin.Back_End.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberCheckNicknameResponse {
    private Boolean available;
}