package junggoin.Back_End.domain.chat.dto;

import lombok.Getter;

@Getter
public class CreateChatRoomRequest {
    String firstMemberEmail;
    String secondMemberEmail;
}
