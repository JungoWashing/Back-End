package junggoin.Back_End.domain.auction.repository;

import junggoin.Back_End.domain.auction.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
