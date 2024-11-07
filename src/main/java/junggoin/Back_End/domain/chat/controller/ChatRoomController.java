package junggoin.Back_End.domain.chat.controller;

import java.util.List;
import junggoin.Back_End.domain.chat.ChatMessage;
import junggoin.Back_End.domain.chat.ChatRoom;
import junggoin.Back_End.domain.chat.dto.CreateChatRoomRequest;
import junggoin.Back_End.domain.chat.service.ChatRoomService;
import junggoin.Back_End.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    // 모든 채팅방 조회 - JSON 응답으로 채팅방 리스트 반환
    @GetMapping("/rooms")
    public List<ChatRoom> getAllRooms() {
        return chatRoomService.findAll();
    }

    // Create a new room
    @PostMapping("/room/create")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody CreateChatRoomRequest request) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(request.getMemberId1(), request.getMemberId2());
        return ResponseEntity.ok(chatRoom);
    }


    /**
     * getChatRoomsByMemberId - 회원별 채팅방 목록 조회
     */
    /*
     * [
     *   {
     *     "roomId": "string",
     *     "name": "string",
     *     "lastMessageDate": "yyyy-MM-dd'T'HH:mm:ss",
     *     "lastMessage": "string"
     *   },
     *   {
     *     "roomId": "string",
     *     "name": "string",
     *     "lastMessageDate": "yyyy-MM-dd'T'HH:mm:ss",
     *     "lastMessage": "string"
     *   }
     * ]
     */

    @GetMapping("/member/chatrooms")
    public ResponseEntity<List<ChatRoom>> getChatRoomsByMemberId(@RequestParam(name = "memberId") Long memberId) {
        List<ChatRoom> chatRooms = chatRoomService.getChatRoomsByMemberId(memberId);
        return ResponseEntity.ok(chatRooms);
    }

    // 특정 채팅방 정보 조회
    @GetMapping("/room")
    public ResponseEntity<ChatRoom> getRoom(@RequestParam(name = "roomId") String roomId) {
        ChatRoom chatRoom = chatRoomService.findRoomById(roomId);
        return ResponseEntity.ok(chatRoom);
    }

    // 특정 채팅방 입장, 이전 채팅들 반환
    @GetMapping("/room/enter")
    public ResponseEntity<List<ChatMessage>> getChatMessagesOrderByCreatedTime(@RequestParam(name = "roomId") String roomId) {
        ChatRoom chatRoom = chatRoomService.findRoomById(roomId);
        chatRoomService.enterChatRoom(chatRoom.getRoomId());
        List<ChatMessage> messages = chatService.getMessagesByRoomIdOrderByTime(chatRoom.getRoomId());
        return ResponseEntity.ok(messages);
    }
}
