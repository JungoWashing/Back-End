package junggoin.Back_End.domain.hashtag.repository;

import junggoin.Back_End.domain.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);
}
