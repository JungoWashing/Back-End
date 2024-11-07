package junggoin.Back_End.domain.chat.controller;

import junggoin.Back_End.domain.chat.ChatMessage;
import junggoin.Back_End.domain.chat.redis.RedisPublisher;
import junggoin.Back_End.domain.chat.service.ChatRoomService;
import junggoin.Back_End.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatRoomService chatRoomService;
    private final ChatService chatMessageService; // 추가

    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessage message) {
        if (isJoin(message)) {
            chatRoomService.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }

        // 메시지 Redis에 발행
        redisPublisher.publish(chatRoomService.getTopic(message.getRoomId()), message);

        // 메시지를 DB에 저장
        chatMessageService.saveMessage(message);
    }

    private boolean isJoin(ChatMessage messageType) {
        return messageType.getMessageType().equals(ChatMessage.MessageType.JOIN);
    }
}
