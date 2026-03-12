package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteCategory;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.WasteUnit;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteCategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
@ConditionalOnProperty(name = "app.seed.modular", havingValue = "true")
public class SeedWasteCatalogInitializer {

    @Bean
    public CommandLineRunner seedWasteCategories(WasteCategoryRepository wasteCategoryRepository) {
        return args -> {
            LocalDateTime now = LocalDateTime.now();
            
            // Giấy & Bìa
            createOrUpdateWasteCategory(wasteCategoryRepository, "Giấy", "Giấy vụn, giấy bìa các loại", WasteUnit.KG, new BigDecimal("2250.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Báo", "Giấy báo, tạp chí cũ", WasteUnit.KG, new BigDecimal("3600.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Giấy, hồ sơ", "Giấy hồ sơ, văn phòng phẩm", WasteUnit.KG, new BigDecimal("3150.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Giấy tập", "Tập vở học sinh, sách cũ", WasteUnit.KG, new BigDecimal("3600.0000"), now);
            
            // Kim loại
            createOrUpdateWasteCategory(wasteCategoryRepository, "Lon bia", "Vỏ lon bia, nước ngọt (nhôm)", WasteUnit.CAN, new BigDecimal("180.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Sắt", "Sắt vụn, sắt đặc", WasteUnit.KG, new BigDecimal("3600.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Sắt lon", "Vỏ lon sắt, hộp thiếc", WasteUnit.KG, new BigDecimal("1440.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Inox", "Thép không gỉ, đồ gia dụng inox", WasteUnit.KG, new BigDecimal("5400.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Đồng", "Dây đồng, ống đồng, đồ đồng", WasteUnit.KG, new BigDecimal("67500.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Nhôm", "Nhôm khung, nhôm dẻo", WasteUnit.KG, new BigDecimal("16200.0000"), now);
            
            // Nhựa & Khác
            createOrUpdateWasteCategory(wasteCategoryRepository, "Chai thủy tinh", "Chai lọ thủy tinh các loại", WasteUnit.BOTTLE, new BigDecimal("450.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Bao bì, hỗn hợp", "Bao bì nilon, nhựa hỗn hợp", WasteUnit.KG, new BigDecimal("1600.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Meca", "Tấm mica, nhựa cứng trong suốt", WasteUnit.KG, new BigDecimal("450.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Mủ", "Nhựa mủ các loại", WasteUnit.KG, new BigDecimal("3600.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Mủ bình", "Bình nhựa HDPE, PP", WasteUnit.KG, new BigDecimal("4500.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Mủ tôn", "Tấm lợp nhựa, nhựa tôn", WasteUnit.KG, new BigDecimal("1800.0000"), now);
            createOrUpdateWasteCategory(wasteCategoryRepository, "Mủ đen", "Nhựa đen, ống nhựa đen", WasteUnit.KG, new BigDecimal("150.0000"), now);
        };
    }

    private WasteCategory createOrUpdateWasteCategory(
            WasteCategoryRepository repo,
            String name,
            String description,
            WasteUnit unit,
            BigDecimal pointPerUnit,
            LocalDateTime now) {
        return repo.findByNameIgnoreCase(name)
                .map(existingCategory -> {
                    boolean updated = false;
                    // Update unit if different
                    if (existingCategory.getUnit() != unit) {
                        existingCategory.setUnit(unit);
                        updated = true;
                    }
                    // Update description if missing
                    if (existingCategory.getDescription() == null && description != null) {
                        existingCategory.setDescription(description);
                        updated = true;
                    }
                    
                    if (updated) {
                        existingCategory.setUpdatedAt(now);
                        return repo.save(existingCategory);
                    }
                    return existingCategory;
                })
                .orElseGet(() -> {
                    WasteCategory category = new WasteCategory();
                    category.setName(name);
                    category.setDescription(description);
                    category.setUnit(unit);
                    category.setPointPerUnit(pointPerUnit);
                    category.setCreatedAt(now);
                    category.setUpdatedAt(now);
                    return repo.save(category);
                });
    }
}

