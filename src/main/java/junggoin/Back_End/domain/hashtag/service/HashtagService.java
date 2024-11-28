package junggoin.Back_End.domain.hashtag.service;

import junggoin.Back_End.domain.hashtag.Hashtag;
import junggoin.Back_End.domain.hashtag.repository.HashtagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public Hashtag saveHashtag(String name) {
        Hashtag hashtag = Hashtag.builder()
                .name(name)
                .build();
        return hashtagRepository.save(hashtag);
    }

    public void removeHashtag(Hashtag hashtag) {
        hashtagRepository.delete(hashtag);
    }
}
