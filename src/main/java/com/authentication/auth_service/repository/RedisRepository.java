package com.authentication.auth_service.repository;

import com.authentication.auth_service.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String prefix = "refresh_token:";

    public void save(String token, String userName, long exp){
        redisTemplate.opsForValue().set(
                prefix+token,
                userName,
                Duration.ofMillis(exp)
        );
    }

    public String validate(String token){
        String username = redisTemplate.opsForValue().get(prefix+token);
        if(username==null){
            throw new InvalidCredentialsException("Invalid Refresh Token!!!");
        }
        return  username;
    }

    public void delete(String token){
        redisTemplate.delete(prefix+token);
    }

}
