package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.feedback;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectorFeedbackRepository extends JpaRepository<CollectorFeedback, Integer> {
    List<CollectorFeedback> findByCollector_IdOrderByCreatedAtDesc(Integer collectorId);
}
