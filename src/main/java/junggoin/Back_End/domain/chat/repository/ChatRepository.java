package junggoin.Back_End.domain.chat.repository;

import java.util.List;
import junggoin.Back_End.domain.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    // roomId로 특정 채팅방의 모든 메시지를 가져오는 메서드
    List<ChatMessage> findChatMessagesByChatRoomRoomIdOrderByCreatedAtAsc(String roomId);
}
