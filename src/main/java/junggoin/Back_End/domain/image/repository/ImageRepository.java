package junggoin.Back_End.domain.image.repository;
import junggoin.Back_End.domain.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
