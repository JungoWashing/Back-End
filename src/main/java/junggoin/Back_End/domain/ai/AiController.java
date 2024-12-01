package junggoin.Back_End.domain.ai;

import junggoin.Back_End.domain.ai.dto.AnalyzeStyleResponseDto;
import junggoin.Back_End.domain.ai.service.AiService;
import junggoin.Back_End.domain.auction.dto.ProductRepDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {
    private final AiService aiService;

//    // 회원 스타일 분석
//    @PostMapping("/styles/{email}")
//    public ResponseEntity<AnalyzeStyleResponseDto> analyzeStyle(@PathVariable String email, @RequestPart List<MultipartFile> files) throws IOException {
//        return ResponseEntity.ok(aiService.analyzeStyle(files));
//    }

    // 경매 추천
    @GetMapping("/recommended/{email}")
    public ResponseEntity<List<ProductRepDto>> getAuctionByRecommended(@PathVariable("email") String email) {
        return ResponseEntity.ok(aiService.recommendAuction(email));
    }
}
