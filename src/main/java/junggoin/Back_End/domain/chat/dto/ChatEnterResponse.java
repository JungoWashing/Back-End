package junggoin.Back_End.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ChatEnterResponse {
    private String senderEmail;
    private String senderNickname;
    private String message;
    private LocalDateTime createdAt;

    @Builder
    public ChatEnterResponse(String senderEmail, String senderNickname, String message, LocalDateTime createdAt) {
        this.senderEmail = senderEmail;
        this.senderNickname = senderNickname;
        this.message = message;
        this.createdAt = createdAt;
    }
}
