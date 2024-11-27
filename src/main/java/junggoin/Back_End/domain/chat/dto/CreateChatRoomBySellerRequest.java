package junggoin.Back_End.domain.chat.dto;

import lombok.Getter;

@Getter
public class CreateChatRoomBySellerRequest {
    String firstMemberEmail;
    String secondMemberEmail;
    Long auctionId;
}
