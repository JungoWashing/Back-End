package junggoin.Back_End.domain.auction.repository;

import junggoin.Back_End.domain.auction.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByMemberEmail(String email);
}
