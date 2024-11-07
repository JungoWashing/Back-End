package junggoin.Back_End.domain.auction.service;

import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    public ResponseEntity<Map<String, Object>> createAuction(Auction auction) {
        Auction savedAuction = auctionRepository.save(Auction.builder()
                .itemName(auction.getItemName())
                .description(auction.getDescription())
                .startingPrice(auction.getStartingPrice())
                .immediatePurchasePrice(auction.getImmediatePurchasePrice())
                .expiredAt(auction.getExpiredAt())
                .winningPrice(auction.getWinningPrice())
                .status(auction.getStatus())
                .member(auction.getMember())
                .build());
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedAuction.getId());
        response.put("message", "Auction created successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Auction> getAuctionById(Long id) {
        return auctionRepository.findById(id)
                .map(auction -> ResponseEntity.ok().body(auction))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<List<Auction>> getAllAuctions() {
        List<Auction> auctions = auctionRepository.findAll();
        return ResponseEntity.ok(auctions);
    }

    public ResponseEntity<Map<String, Object>> updateAuction(Long id, Auction auctionDetails) {
        return auctionRepository.findById(id).map(auction -> {
            Auction updatedAuction = Auction.builder()
                    .id(auction.getId())
                    .itemName(auctionDetails.getItemName())
                    .description(auctionDetails.getDescription())
                    .startingPrice(auctionDetails.getStartingPrice())
                    .immediatePurchasePrice(auctionDetails.getImmediatePurchasePrice())
                    .expiredAt(auctionDetails.getExpiredAt())
                    .winningPrice(auctionDetails.getWinningPrice())
                    .status(auctionDetails.getStatus())
                    .member(auctionDetails.getMember())
                    .build();
            auctionRepository.save(updatedAuction);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Auction updated successfully");
            return ResponseEntity.ok(response);
        }).orElseGet(() -> new ResponseEntity<>(Map.of("message", "Auction not found"), HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<Map<String, Object>> deleteAuction(Long id) {
        return auctionRepository.findById(id).map(auction -> {
            auctionRepository.delete(auction);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Auction deleted successfully");
            return ResponseEntity.ok(response);
        }).orElseGet(() -> new ResponseEntity<>(Map.of("message", "Auction not found"), HttpStatus.NOT_FOUND));
    }
}
