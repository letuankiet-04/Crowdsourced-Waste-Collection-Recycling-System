package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectorRepository extends JpaRepository<Collector, Integer> {
    boolean existsByIdAndEnterprise_IdAndStatus(Integer id, Integer enterpriseId, CollectorStatus status);

    Optional<Collector> findByUserId(Integer userId);

    List<Collector> findByEnterprise_IdOrderByCreatedAtDesc(Integer enterpriseId);

    List<Collector> findByEnterprise_IdAndStatusOrderByCreatedAtDesc(Integer enterpriseId, CollectorStatus status);

    @Query("select c from Collector c join fetch c.user where c.enterprise.id = :enterpriseId order by c.createdAt desc")
    List<Collector> findByEnterpriseIdWithUserOrderByCreatedAtDesc(@Param("enterpriseId") Integer enterpriseId);

    @Query("select c from Collector c join fetch c.user where c.enterprise.id = :enterpriseId and c.status = :status order by c.createdAt desc")
    List<Collector> findByEnterpriseIdAndStatusWithUserOrderByCreatedAtDesc(
            @Param("enterpriseId") Integer enterpriseId,
            @Param("status") CollectorStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Collector c WHERE c.enterprise.id = :enterpriseId AND c.status = com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorStatus.ONLINE")
    List<Collector> findAvailableCollectors(@org.springframework.data.repository.query.Param("enterpriseId") Integer enterpriseId);
}
