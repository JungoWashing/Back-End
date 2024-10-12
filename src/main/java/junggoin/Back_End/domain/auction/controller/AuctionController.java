package junggoin.Back_End.domain.auction.controller;

import junggoin.Back_End.domain.auction.Auction;
import junggoin.Back_End.domain.auction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createAuction(@RequestBody Auction auction) {
        return auctionService.createAuction(auction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable("id") Long id) {
        return auctionService.getAuctionById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Auction>> getAllAuctions() {
        return auctionService.getAllAuctions();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateAuction(@PathVariable("id") Long id, @RequestBody Auction auctionDetails) {
        return auctionService.updateAuction(id, auctionDetails);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteAuction(@PathVariable("id") Long id) {
        return auctionService.deleteAuction(id);
    }
}
