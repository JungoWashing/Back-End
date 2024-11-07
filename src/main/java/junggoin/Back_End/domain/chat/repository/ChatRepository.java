package junggoin.Back_End.domain.chat.repository;

import junggoin.Back_End.domain.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    // roomId로 특정 채팅방의 모든 메시지를 가져오는 메서드
    List<ChatMessage> findByRoomId(String roomId);
}
