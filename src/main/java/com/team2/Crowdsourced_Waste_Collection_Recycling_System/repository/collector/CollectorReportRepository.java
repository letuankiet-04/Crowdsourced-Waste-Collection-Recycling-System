package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectorReportRepository extends JpaRepository<CollectorReport, Integer> {
    boolean existsByCollectionRequest_Id(Integer requestId);

    Optional<CollectorReport> findTopByCollectionRequest_IdOrderByCreatedAtDesc(Integer requestId);

    default Optional<CollectorReport> findByCollectionRequest_Id(Integer requestId) {
        return findTopByCollectionRequest_IdOrderByCreatedAtDesc(requestId);
    }

    default Optional<CollectorReport> findByCollectionRequestId(Integer requestId) {
        return findTopByCollectionRequest_IdOrderByCreatedAtDesc(requestId);
    }

    List<CollectorReport> findByCollector_Id(Integer collectorId);

    List<CollectorReport> findByCollector_IdOrderByCreatedAtDesc(Integer collectorId);

    @Query("""
            select cr
            from CollectorReport cr
            join fetch cr.collectionRequest req
            join fetch cr.collector c
            where req.id = :requestId
            order by cr.createdAt desc, cr.id desc
            """)
    List<CollectorReport> findByCollectionRequestIdWithRequestAndCollector(@Param("requestId") Integer requestId);

    @Query("""
            select cr
            from CollectorReport cr
            join fetch cr.collectionRequest req
            join fetch cr.collector c
            where c.id = :collectorId
            order by cr.createdAt desc, cr.id desc
            """)
    List<CollectorReport> findByCollectorIdWithRequest(@Param("collectorId") Integer collectorId);

    List<CollectorReport> findByCollectionRequest_Enterprise_IdOrderByCreatedAtDesc(Integer enterpriseId);

    void deleteByCollectionRequest_Report_Citizen_Id(Integer citizenId);

    void deleteByCollector_Id(Integer collectorId);

    long countByCollector_Id(Integer collectorId);
}
