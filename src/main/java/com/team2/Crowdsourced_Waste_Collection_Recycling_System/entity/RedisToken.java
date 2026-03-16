package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@RedisHash("redis_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisToken implements Serializable {

    /**
     * Khóa chính trong Redis, ví dụ: userId hoặc tokenId.
     */
    @Id
    private String id;

    private String accessToken;

    private String refreshToken;

    /**
     * Thời gian sống của bản ghi trong Redis (tính bằng giây).
     * Ví dụ: 3600L = 1 giờ.
     */
    @TimeToLive
    private Long ttl;
}
