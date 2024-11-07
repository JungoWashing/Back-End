package junggoin.Back_End.domain.chat.dto;

import lombok.Getter;

@Getter
public class CreateChatRoomRequest {
    Long memberId1;
    Long memberId2;
}
