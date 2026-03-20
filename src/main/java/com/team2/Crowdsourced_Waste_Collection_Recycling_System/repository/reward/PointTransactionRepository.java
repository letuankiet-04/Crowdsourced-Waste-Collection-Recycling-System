package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.reward;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.PointTransaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Integer> {
    
    @EntityGraph(attributePaths = { "citizen" })
    List<PointTransaction> findByCitizenId(Integer citizenId);
    
    List<PointTransaction> findByTransactionType(String transactionType);
    
    List<PointTransaction> findByCollectionRequestId(Integer collectionRequestId);
    
    List<PointTransaction> findByReportId(Integer reportId);
    boolean existsByCollectionRequestId(Integer collectionRequestId);
    boolean existsByCollectionRequestIdAndTransactionType(Integer collectionRequestId, String transactionType);
    boolean existsByReportId(Integer reportId);
    
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.citizen.id = :citizenId ORDER BY pt.createdAt DESC")
    List<PointTransaction> findByCitizenIdOrderByCreatedAtDesc(@Param("citizenId") Integer citizenId);
    
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.citizen.id = :citizenId AND pt.transactionType = :type")
    List<PointTransaction> findByCitizenIdAndTransactionType(
        @Param("citizenId") Integer citizenId,
        @Param("type") String type
    );
    
    @EntityGraph(attributePaths = { "citizen" })
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.citizen.id = :citizenId AND pt.createdAt BETWEEN :startDate AND :endDate")
    List<PointTransaction> findByCitizenIdAndDateRange(
        @Param("citizenId") Integer citizenId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT SUM(pt.points) FROM PointTransaction pt WHERE pt.citizen.id = :citizenId")
    Integer getTotalPointsByCitizenId(@Param("citizenId") Integer citizenId);
    
    @Query("SELECT SUM(pt.points) FROM PointTransaction pt WHERE pt.citizen.id = :citizenId AND pt.transactionType = :type")
    Integer getTotalPointsByCitizenIdAndType(
        @Param("citizenId") Integer citizenId,
        @Param("type") String type
    );

    @Query("SELECT SUM(pt.points) FROM PointTransaction pt WHERE pt.transactionType = 'EARN'")
    Long sumTotalPointsDistributed();

    @Query("""
        SELECT YEAR(pt.createdAt), MONTH(pt.createdAt), SUM(pt.points) 
        FROM PointTransaction pt 
        WHERE pt.transactionType = 'EARN'
        GROUP BY YEAR(pt.createdAt), MONTH(pt.createdAt)
        ORDER BY YEAR(pt.createdAt) DESC, MONTH(pt.createdAt) DESC
    """)
    List<Object[]> sumPointsDistributedPerMonth();

    @Query("""
            SELECT COALESCE(SUM(pt.points), 0)
            FROM PointTransaction pt
            WHERE pt.citizen.id = :citizenId
              AND pt.transactionType = :type
              AND (:from is null OR pt.createdAt >= :from)
              AND (:to is null OR pt.createdAt < :to)
            """)
    Long sumPointsByCitizenIdAndTypeAndRange(
            @Param("citizenId") Integer citizenId,
            @Param("type") String type,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
    
    PointTransaction findTopByCitizenIdOrderByCreatedAtDesc(Integer citizenId);

    @EntityGraph(attributePaths = { "citizen", "citizen.user", "collectionRequest", "collectionRequest.enterprise", "collectionRequest.report", "createdBy" })
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.id = :id")
    Optional<PointTransaction> findOneWithDetailsById(@Param("id") Integer id);
    
    @Query("SELECT COUNT(pt) FROM PointTransaction pt WHERE pt.citizen.id = :citizenId AND pt.createdAt BETWEEN :startDate AND :endDate")
    Long countTransactionsByCitizenIdAndDateRange(
        @Param("citizenId") Integer citizenId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("""
        SELECT SUM(pt.points) 
        FROM PointTransaction pt 
        JOIN pt.collectionRequest req 
        WHERE req.enterprise.id = :enterpriseId 
          AND pt.transactionType = 'EARN'
    """)
    Long sumTotalPointsDistributedByEnterprise(@Param("enterpriseId") Integer enterpriseId);

    @Query("""
        SELECT YEAR(pt.createdAt), MONTH(pt.createdAt), SUM(pt.points) 
        FROM PointTransaction pt 
        JOIN pt.collectionRequest req 
        WHERE req.enterprise.id = :enterpriseId 
          AND pt.transactionType = 'EARN'
        GROUP BY YEAR(pt.createdAt), MONTH(pt.createdAt)
        ORDER BY YEAR(pt.createdAt) DESC, MONTH(pt.createdAt) DESC
    """)
    List<Object[]> sumPointsDistributedByEnterprisePerMonth(@Param("enterpriseId") Integer enterpriseId);

    /**
     * Lấy bảng xếp hạng Citizen dựa trên tổng điểm tích lũy (chỉ tính điểm kiếm được 'EARN').
     * Có thể lọc theo ngày, tháng, năm.
     *
     * @param day   Ngày lọc (có thể null)
     * @param month Tháng lọc (có thể null)
     * @param year  Năm lọc (có thể null)
     * @return List Object[] gồm: id, fullName, ward, city, totalPoints
     */
    @Query("""
        SELECT 
            c.id, 
            c.fullName, 
            c.ward, 
            c.city, 
            SUM(pt.points)
        FROM PointTransaction pt
        JOIN pt.citizen c
        WHERE pt.transactionType = 'EARN'
          AND (:day IS NULL OR DAY(pt.createdAt) = :day)
          AND (:month IS NULL OR MONTH(pt.createdAt) = :month)
          AND (:year IS NULL OR YEAR(pt.createdAt) = :year)
        GROUP BY c.id, c.fullName, c.ward, c.city
        ORDER BY SUM(pt.points) DESC
    """)
    List<Object[]> findCitizenLeaderboard(
            @Param("day") Integer day,
            @Param("month") Integer month,
            @Param("year") Integer year);
}

