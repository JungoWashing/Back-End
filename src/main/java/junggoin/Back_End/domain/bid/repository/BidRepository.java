package junggoin.Back_End.domain.bid.repository;

import junggoin.Back_End.domain.auction.AuctionView;
import junggoin.Back_End.domain.bid.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid,Long> {
    List<Bid> findAllByAuctionId(Long auctionId);

    Optional<Bid> findTopByAuctionIdOrderByBidPriceDesc(Long auctionId);

    List<AuctionView> findDistinctAuctionByBidderEmail(String email);
}
