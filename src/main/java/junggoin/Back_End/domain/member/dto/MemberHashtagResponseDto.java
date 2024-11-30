package junggoin.Back_End.domain.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MemberHashtagResponseDto {
    private List<String> hashtags;
}
