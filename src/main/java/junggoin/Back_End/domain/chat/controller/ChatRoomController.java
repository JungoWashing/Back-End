package junggoin.Back_End.domain.chat.controller;

import junggoin.Back_End.domain.chat.ChatMessage;
import junggoin.Back_End.domain.chat.ChatRoom;
import junggoin.Back_End.domain.chat.service.ChatRoomService;
import junggoin.Back_End.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * todo : 채팅기록 불러올 때 클라이언트 아이디 비교해서 본인아이디로 보내진것이면 오른쪽, 상대가 보낸것이면 왼쪽 표시
 */
@RequiredArgsConstructor
@RestController  // @RestController를 사용하여 모든 메서드가 JSON 응답을 반환하도록 설정
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    // 모든 채팅방 조회 - JSON 응답으로 채팅방 리스트 반환
    @GetMapping("/rooms")
    public List<ChatRoom> getAllRooms() {
        return chatRoomService.findAll();
    }
    @GetMapping("/room/{roomId}/exists")
    public ResponseEntity<Map<String, Boolean>> checkRoomExists(@PathVariable("roomId") String roomId) {
        boolean exists = chatRoomService.doesRoomExist(roomId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    // Create a new room
    @PostMapping("/room/create")
    public ResponseEntity<ChatRoom> createRoom(@RequestBody Map<String, String> request) {
        String roomId = request.get("roomId");
        ChatRoom newRoom = chatRoomService.createRoom(roomId);
        return ResponseEntity.ok(newRoom);
    }

    // 특정 채팅방 정보 조회
    @GetMapping("/room/{roomId}")
    public Optional<ChatRoom> getRoom(@PathVariable("roomId") String roomId) {
        return chatRoomService.findRoomById(roomId);
    }

    // 특정 채팅방의 이전 메시지 불러오기
    @GetMapping("/room/{roomId}/messages")
    public List<ChatMessage> getChatMessages(@PathVariable("roomId") String roomId) {
        List<ChatMessage> messages = chatService.getMessagesByRoomId(roomId);
        System.out.println("getChatMessagesHere");
        System.out.println("roomId = " + roomId);
        for (ChatMessage message : messages) {
            System.out.println("message = " + message);
        }
        return messages;
    }
}
