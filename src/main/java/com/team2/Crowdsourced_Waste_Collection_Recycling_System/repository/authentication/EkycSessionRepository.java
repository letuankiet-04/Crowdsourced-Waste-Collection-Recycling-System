package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.EkycSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EkycSessionRepository extends JpaRepository<EkycSession, String> {
    Optional<EkycSession> findByIdAndUser_Id(String id, Integer userId);

    void deleteByUser_Id(Integer userId);
}

