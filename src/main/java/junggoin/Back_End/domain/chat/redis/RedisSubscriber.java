package junggoin.Back_End.domain.chat.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import junggoin.Back_End.domain.chat.ChatMessage;
import junggoin.Back_End.domain.chat.dto.MessageDTO;
import junggoin.Back_End.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 수신한 메시지를 변환
            String publishMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
            MessageDTO roomMessage = objectMapper.readValue(publishMessage, MessageDTO.class);
            ChatMessage chat = chatService.findChat(roomMessage.getId());
            // WebSocket을 통해 특정 채팅방의 구독자들에게 메시지 전송
            System.out.println("chatMessage = " + chat.getMessage() + ", chatId = " + chat.getId() + ", chatRoomId = " + chat.getChatRoom().getRoomId());
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getRoomName(), chat);
        } catch (JsonProcessingException e) {
            log.error("메시지 변환 오류: {}", e.getMessage());
        }
    }
}
