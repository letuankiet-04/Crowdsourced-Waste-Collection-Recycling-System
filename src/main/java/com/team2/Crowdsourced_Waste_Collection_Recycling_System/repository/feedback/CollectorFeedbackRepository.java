package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.feedback;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorFeedback;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectorFeedbackRepository extends JpaRepository<CollectorFeedback, Integer> {
    @Override
    @EntityGraph(attributePaths = { "collector", "collector.user", "collectionRequest" })
    List<CollectorFeedback> findAll();

    @Override
    @EntityGraph(attributePaths = { "collector", "collector.user", "collectionRequest", "collectionRequest.collector" })
    Optional<CollectorFeedback> findById(Integer id);

    List<CollectorFeedback> findByCollector_IdOrderByCreatedAtDesc(Integer collectorId);

    void deleteByCollectionRequest_Report_Citizen_Id(Integer citizenId);

    void deleteByCollector_Id(Integer collectorId);

    long countByCollector_Id(Integer collectorId);
}
