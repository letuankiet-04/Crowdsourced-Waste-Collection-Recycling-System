package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorReportImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectorReportImageRepository extends JpaRepository<CollectorReportImage, Integer> {
    List<CollectorReportImage> findByCollectorReport_Id(Integer collectorReportId);

    List<CollectorReportImage> findByCollectorReport_IdIn(List<Integer> collectorReportIds);

    void deleteByCollectorReport_CollectionRequest_Report_Citizen_Id(Integer citizenId);

    void deleteByCollectorReport_Collector_Id(Integer collectorId);
}
