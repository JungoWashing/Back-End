package junggoin.Back_End.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class JoinRequestDto {
    private String nickname;
    private List<String> hashtags;
}