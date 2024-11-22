package junggoin.Back_End.domain.bid;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

@RequiredArgsConstructor
@Configuration
public class LockConfig {
    private final RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisLockRegistry lockRegistry() {
        RedisLockRegistry redisLockRegistry = new RedisLockRegistry(redisConnectionFactory, "Bid");
        redisLockRegistry.setRedisLockType(RedisLockRegistry.RedisLockType.PUB_SUB_LOCK);
        return redisLockRegistry;
    }
}