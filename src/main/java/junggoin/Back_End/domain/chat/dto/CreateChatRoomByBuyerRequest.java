package junggoin.Back_End.domain.chat.dto;

import lombok.Getter;

@Getter
public class CreateChatRoomByBuyerRequest {
    String email;
    Long auctionId;
}
