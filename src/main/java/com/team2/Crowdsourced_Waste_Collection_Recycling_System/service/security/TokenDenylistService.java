package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.security;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.InvalidatedToken;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.InvalidatedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TokenDenylistService {
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long negativeTtlMillis = 10_000L;
    private final int maxEntries = 50_000;

    public boolean isTokenRevoked(String jti, Instant tokenExpiresAt) {
        if (jti == null || jti.isBlank()) {
            return false;
        }

        long now = System.currentTimeMillis();
        CacheEntry cached = cache.get(jti);
        if (cached != null) {
            if (cached.revokedUntilMillis > now) {
                return true;
            }
            if (cached.nextCheckMillis > now) {
                return false;
            }
            cache.remove(jti, cached);
        }

        boolean revoked = invalidatedTokenRepository.existsById(jti);
        if (revoked) {
            long until = tokenExpiresAt != null ? tokenExpiresAt.toEpochMilli() : now + 3_600_000L;
            cache.put(jti, CacheEntry.revokedUntil(until));
            trim(now);
            return true;
        }

        cache.put(jti, CacheEntry.notRevoked(now + negativeTtlMillis));
        trim(now);
        return false;
    }

    public void invalidate(String jti, Instant tokenExpiresAt) {
        if (jti == null || jti.isBlank()) {
            return;
        }

        Date expiryTime = tokenExpiresAt != null ? Date.from(tokenExpiresAt) : null;
        invalidatedTokenRepository.save(InvalidatedToken.builder().id(jti).expiryTime(expiryTime).build());

        long now = System.currentTimeMillis();
        long until = tokenExpiresAt != null ? tokenExpiresAt.toEpochMilli() : now + 3_600_000L;
        cache.put(jti, CacheEntry.revokedUntil(until));
        trim(now);
    }

    private void trim(long now) {
        if (cache.size() <= maxEntries) {
            return;
        }

        int removed = 0;
        Iterator<Map.Entry<String, CacheEntry>> it = cache.entrySet().iterator();
        while (it.hasNext() && removed < 5_000 && cache.size() > maxEntries) {
            Map.Entry<String, CacheEntry> e = it.next();
            if (e.getValue().isExpired(now)) {
                it.remove();
                removed++;
            }
        }

        if (cache.size() <= maxEntries) {
            return;
        }

        it = cache.entrySet().iterator();
        while (it.hasNext() && removed < 10_000 && cache.size() > maxEntries) {
            it.next();
            it.remove();
            removed++;
        }
    }

    private static final class CacheEntry {
        final long revokedUntilMillis;
        final long nextCheckMillis;

        private CacheEntry(long revokedUntilMillis, long nextCheckMillis) {
            this.revokedUntilMillis = revokedUntilMillis;
            this.nextCheckMillis = nextCheckMillis;
        }

        static CacheEntry revokedUntil(long revokedUntilMillis) {
            return new CacheEntry(revokedUntilMillis, 0L);
        }

        static CacheEntry notRevoked(long nextCheckMillis) {
            return new CacheEntry(0L, nextCheckMillis);
        }

        boolean isExpired(long now) {
            if (revokedUntilMillis > 0) {
                return revokedUntilMillis <= now;
            }
            return nextCheckMillis <= now;
        }
    }
}
