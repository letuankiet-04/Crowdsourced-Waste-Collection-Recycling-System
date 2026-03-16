package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.RedisToken;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.RedisTokenRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.RedisTokenService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedisTokenServiceImpl implements RedisTokenService {

    private final RedisTokenRepository redisTokenRepository;

    public RedisTokenServiceImpl(RedisTokenRepository redisTokenRepository) {
        this.redisTokenRepository = redisTokenRepository;
    }

    @Override
    public void save(RedisToken redisToken) {
        redisTokenRepository.save(redisToken);
    }

    @Override
    public Optional<RedisToken> findById(String id) {
        return redisTokenRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        redisTokenRepository.deleteById(id);
    }
}
