package junggoin.Back_End.domain.chat.service;

import junggoin.Back_End.domain.chat.ChatMessage;
import junggoin.Back_End.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRepository chatMessageRepository;

    public void saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
    }

    // 특정 채팅방(roomId)의 모든 메시지 불러오기
    public List<ChatMessage> getMessagesByRoomId(String roomId) {
        return chatMessageRepository.findByRoomId(roomId);
    }
}
