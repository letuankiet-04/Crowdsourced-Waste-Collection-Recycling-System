package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.EnterpriseAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseAdminRepository extends JpaRepository<EnterpriseAdmin, Integer> {
}
