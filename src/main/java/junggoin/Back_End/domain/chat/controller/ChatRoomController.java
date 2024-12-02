package junggoin.Back_End.domain.chat.controller;

import java.util.List;
import java.util.stream.Collectors;
import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.service.AuctionService;
import junggoin.Back_End.domain.chat.ChatMessage;
import junggoin.Back_End.domain.chat.ChatRoom;
import junggoin.Back_End.domain.chat.dto.ChatEnterResponse;
import junggoin.Back_End.domain.chat.dto.ChatRoomResponseDto;
import junggoin.Back_End.domain.chat.dto.CreateChatRoomRequest;
import junggoin.Back_End.domain.chat.dto.CreateChatRoomResponse;
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
    private final AuctionService auctionService;

    // Create a new room
    @PostMapping("/room/create/seller")
    public ResponseEntity<CreateChatRoomResponse> createChatRoomBySeller(
            @RequestBody CreateChatRoomRequest request) {
        ChatRoom chatRoom = chatRoomService.createChatRoomBySeller(request);
        return ResponseEntity.ok(CreateChatRoomResponse.builder()
                .roomId(chatRoom.getRoomId())
                .name(chatRoom.getName()).build());
    }


    // buyer requestDTO -> email 한개밖에 없음
    @PostMapping("/room/create/buyer")
    public ResponseEntity<CreateChatRoomResponse> createChatRoomByBuyer(
            @RequestBody CreateChatRoomRequest request) {
        ChatRoom chatRoom = chatRoomService.createChatRoomByBuyer(request);
        return ResponseEntity.ok(CreateChatRoomResponse.builder()
                .roomId(chatRoom.getRoomId())
                .name(chatRoom.getName()).build());
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
    @GetMapping("/member/rooms")
    public ResponseEntity<List<ChatRoomResponseDto>> getChatRoomsByMemberId(
            @RequestParam(name = "email") String email) {
        List<ChatRoomResponseDto> chatRooms = chatRoomService.getChatRoomsByMember(email).stream().map(chatRoomService::toChatRoomResponseDto).collect(Collectors.toList());
        return ResponseEntity.ok(chatRooms);
    }

    // 특정 채팅방 정보 조회
    @GetMapping("/room")
    public ResponseEntity<ChatRoomResponseDto> getRoom(@RequestParam(name = "roomId") String roomId) {
        ChatRoomResponseDto chatRoom = chatRoomService.toChatRoomResponseDto(chatRoomService.findRoomById(roomId));
        return ResponseEntity.ok(chatRoom);
    }

    // 특정 채팅방 입장, 이전 채팅들 반환
    @GetMapping("/room/enter")
    public ResponseEntity<List<ChatEnterResponse>> enterChatRoomByRoomIdAndGetChatMessagesOrderByCreatedTime(
            @RequestParam(name = "roomId") String roomId) {
        ChatRoom chatRoom = chatRoomService.findRoomById(roomId);
        chatRoomService.enterChatRoom(chatRoom.getRoomId());
        List<ChatMessage> messages = chatService.getMessagesByRoomIdOrderByTime(
                chatRoom.getRoomId());
        return ResponseEntity.ok(messages.stream()
                .map(message -> ChatEnterResponse.builder()
                        .senderEmail(message.getSender()
                                .getEmail())
                        .senderNickname(message.getSender()
                                .getNickname())
                        .message(message.getMessage())
                        .createdAt(message.getCreatedAt())
                        .build())
                .collect(Collectors.toList())
        );
    }

    // 경매정보로 채팅방 입장, 이전 채팅들 반환
    @GetMapping("/room/auction/enter")
    public ResponseEntity<List<ChatEnterResponse>> enterChatRoomByAuctionAndGetChatMessagesOrderByCreatedTime(
            @RequestParam(name = "auctionId") Long auctionId) {
        Auction auction = auctionService.findById(auctionId);
        ChatRoom chatRoom = auction.getChatRoom();
        chatRoomService.enterChatRoom(chatRoom.getRoomId());
        List<ChatMessage> messages = chatService.getMessagesByRoomIdOrderByTime(
                chatRoom.getRoomId());
        return ResponseEntity.ok(messages.stream()
                .map(message -> ChatEnterResponse.builder()
                        .senderEmail(message.getSender()
                                .getEmail())
                        .senderNickname(message.getSender()
                                .getNickname())
                        .message(message.getMessage())
                        .createdAt(message.getCreatedAt())
                        .build())
                .collect(Collectors.toList())
        );
    }
}
