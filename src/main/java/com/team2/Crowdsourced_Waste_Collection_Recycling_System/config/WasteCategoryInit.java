package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteCategory;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.WasteUnit;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteCategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class WasteCategoryInit implements CommandLineRunner {
    private final WasteCategoryRepository wasteCategoryRepository;

    public WasteCategoryInit(WasteCategoryRepository wasteCategoryRepository) {
        this.wasteCategoryRepository = wasteCategoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        LocalDateTime now = LocalDateTime.now();
        for (CategorySeed seed : seeds()) {
            upsertCategory(seed, now);
        }
    }

    private void upsertCategory(CategorySeed seed, LocalDateTime now) {
        WasteCategory category = wasteCategoryRepository.findByNameIgnoreCase(seed.name())
                .orElseGet(WasteCategory::new);

        if (category.getCreatedAt() == null) {
            category.setCreatedAt(now);
        }

        category.setName(seed.name());
        category.setDescription(seed.description());
        category.setUnit(seed.unit());
        category.setPointPerUnit(seed.pointPerUnit());
        category.setUpdatedAt(now);

        wasteCategoryRepository.save(category);
    }

    private List<CategorySeed> seeds() {
        return List.of(
                new CategorySeed("Giấy", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("2250")),
                new CategorySeed("Báo", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("3600")),
                new CategorySeed("Giấy, hồ sơ", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("3150")),
                new CategorySeed("Giấy tập", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("3600")),
                new CategorySeed("Lon bia", "RECYCLABLE WASTE", WasteUnit.CAN, new BigDecimal("180")),
                new CategorySeed("Sắt", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("3600")),
                new CategorySeed("Sắt lon", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("1440")),
                new CategorySeed("Inox", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("5400")),
                new CategorySeed("Đồng", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("67500")),
                new CategorySeed("Nhôm", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("16200")),
                new CategorySeed("Chai thủy tinh", "RECYCLABLE WASTE", WasteUnit.BOTTLE, new BigDecimal("450")),
                new CategorySeed("Bao bì, hỗn hợp", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("1600")),
                new CategorySeed("Meca", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("450")),
                new CategorySeed("Mủ", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("3600")),
                new CategorySeed("Mủ bình", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("4500")),
                new CategorySeed("Mủ tôn", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("1800")),
                new CategorySeed("Mủ đen", "RECYCLABLE WASTE", WasteUnit.KG, new BigDecimal("150")),

                new CategorySeed("Pin tiểu", "HAZARDOUS WASTE (đơn vị: viên)", WasteUnit.CAN, new BigDecimal("2000")),
                new CategorySeed("Pin sạc", "HAZARDOUS WASTE (đơn vị: viên)", WasteUnit.CAN, new BigDecimal("3000")),
                new CategorySeed("Ắc quy", "HAZARDOUS WASTE", WasteUnit.KG, new BigDecimal("8000")),
                new CategorySeed("Bóng đèn huỳnh quang", "HAZARDOUS WASTE (đơn vị: bóng)", WasteUnit.CAN, new BigDecimal("1500")),
                new CategorySeed("Bóng đèn LED hỏng", "HAZARDOUS WASTE (đơn vị: bóng)", WasteUnit.CAN, new BigDecimal("1200")),
                new CategorySeed("Thuốc quá hạn", "HAZARDOUS WASTE", WasteUnit.KG, new BigDecimal("4000")),
                new CategorySeed("Hóa chất gia dụng", "HAZARDOUS WASTE", WasteUnit.KG, new BigDecimal("6000")),
                new CategorySeed("Thiết bị điện tử nhỏ (rác điện tử)", "HAZARDOUS WASTE", WasteUnit.KG, new BigDecimal("7500"))
        );
    }

    private record CategorySeed(String name, String description, WasteUnit unit, BigDecimal pointPerUnit) {
    }
}
