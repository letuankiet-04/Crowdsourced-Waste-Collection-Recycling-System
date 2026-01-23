package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.RecyclingEnterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecyclingEnterpriseRepository extends JpaRepository<RecyclingEnterprise, Integer> {
}
