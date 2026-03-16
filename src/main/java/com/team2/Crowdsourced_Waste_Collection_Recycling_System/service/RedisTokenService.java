package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.RedisToken;

import java.util.Optional;

public interface RedisTokenService {

    void save(RedisToken redisToken);

    Optional<RedisToken> findById(String id);

    void deleteById(String id);
}
