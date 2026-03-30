package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.reward;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Integer> {
    void deleteByCitizenId(Integer citizenId);

    long countByCitizenId(Integer citizenId);
}
