package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WasteCategoryRepository extends JpaRepository<WasteCategory, Integer> {
    Optional<WasteCategory> findByName(String name);

    Optional<WasteCategory> findByNameIgnoreCase(String name);

    @Query("select wc from WasteCategory wc where lower(wc.name) in :names")
    List<WasteCategory> findByLowerNameIn(@Param("names") List<String> names);
}

