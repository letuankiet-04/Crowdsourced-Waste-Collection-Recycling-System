package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  CollectorTaskRepository extends JpaRepository<Collector, Long> {
}
