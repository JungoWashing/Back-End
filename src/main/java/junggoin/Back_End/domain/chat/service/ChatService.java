package junggoin.Back_End.domain.chat.service;

import java.util.List;
import junggoin.Back_End.domain.chat.ChatMessage;
import junggoin.Back_End.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRepository chatMessageRepository;

    public void saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
    }

    public List<ChatMessage> findAll() {
        return chatMessageRepository.findAll();
    }

    public ChatMessage findChat(Long id) {return chatMessageRepository.findById(id).get();}
    // 특정 채팅방(roomId)의 모든 메시지 불러오기
    public List<ChatMessage> getMessagesByRoomIdOrderByTime(String roomId) {
        return chatMessageRepository.findChatMessagesByChatRoomRoomIdOrderByCreatedAtAsc(roomId);
    }
}
