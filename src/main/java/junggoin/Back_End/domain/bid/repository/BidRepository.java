package junggoin.Back_End.domain.bid.repository;

import junggoin.Back_End.domain.bid.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid,Long> {
}
