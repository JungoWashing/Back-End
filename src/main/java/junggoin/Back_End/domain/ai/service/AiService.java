package junggoin.Back_End.domain.ai.service;

import jakarta.annotation.PostConstruct;
import junggoin.Back_End.domain.ai.dto.RecommendAuctionResponseDto;
import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.dto.ProductRepDto;
import junggoin.Back_End.domain.auction.service.AuctionService;
import junggoin.Back_End.domain.ai.dto.AnalyzeStyleResponseDto;
import junggoin.Back_End.domain.member.dto.MemberHashtagResponseDto;
import junggoin.Back_End.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiService {
    private final MemberService memberService;
    private final AuctionService auctionService;

    @Value("${ai_server_url}")
    private String ai_server_url;

    private RestTemplate restTemplate;
    private static final String FIXED_URL = "https://jungowashing-bucket.s3.amazonaws.com/images/auctions/";

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplateBuilder()
                .rootUri(ai_server_url)
                .setConnectTimeout(Duration.ofSeconds(5))
                .build();
    }

    // 회원 스타일 분석
//    public AnalyzeStyleResponseDto analyzeStyle(List<MultipartFile> files) throws IOException {
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//
//        for (MultipartFile file : files) {
//            body.add("file", new ByteArrayResource(file.getBytes()) {
//                @Override
//                public String getFilename() {
//                    return file.getOriginalFilename();
//                }
//            });
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//        ResponseEntity<AnalyzeStyleResponseDto> response = restTemplate.postForEntity("/analyze-styles", requestEntity, AnalyzeStyleResponseDto.class);
//        // response
//        //{
//        //    "style1": "슬림핏",
//        //    "style2": "페미닌",
//        //    "style3": "모노톤",
//        //    "style4": "시크",
//        //    "style5": "포멀"
//        //}
//        return response.getBody();
//    }

    // 경매 추천
    public List<ProductRepDto> recommendAuction(String email){
        MemberHashtagResponseDto memberHashtagResponseDto = memberService.getMemberHashtags(email);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        for(String hashtag : memberHashtagResponseDto.getHashtags()){
            body.add("tags", hashtag);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body,headers);

        ResponseEntity<RecommendAuctionResponseDto> response = restTemplate.postForEntity("/recommend-auction", requestEntity, RecommendAuctionResponseDto.class);

        // response
        //{
        //    "representative_images": [
        //        "https://jungowashing-bucket.s3.amazonaws.com/images/auctions/86/0.jpg?AWSAccessKeyId=AKIA2OA...",
        //        "https://jungowashing-bucket.s3.amazonaws.com/images/auctions/44/0.jpg?AWSAccessKeyId=AKIA2OA..."
        //    ]
        //}
        List<ProductRepDto> productRepDtos = new ArrayList<>();
        for(String imageUrl : Objects.requireNonNull(response.getBody()).getRepresentative_images()){

            String remainingUrl = imageUrl.substring(FIXED_URL.length());
            Long id = Long.parseLong(remainingUrl.split("/")[0]); // 첫 번째 슬래시까지 추출

            try{
                Auction auction= auctionService.findById(id);
                productRepDtos.add(auctionService.toProductRepDto(auction));
            }catch (NoSuchElementException e){
                log.info(e.getMessage());
            }
        }

        return productRepDtos;
    }

    // 상품 이미지 분석 요청
    @Async
    public void analyzeProduct(Long auctionId){
        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문 설정
        String requestUrl = String.format("/analyze-product?auction_id=%d", auctionId);

        // HTTP 요청 엔티티 생성
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // FastAPI 서버로 POST 요청
        ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, requestEntity, String.class);

        // 요청 성공 시 결과 처리
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("분석 결과: {}", response.getBody());
        } else {
            log.info("분석 요청 실패: {}", response.getStatusCode());
        }
    }
}
