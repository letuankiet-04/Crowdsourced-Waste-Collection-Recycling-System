package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateWasteReportRequest {
    MultipartFile image;
    Double latitude;
    Double longitude;
    String description;

    @NotEmpty(message = "Phải chọn ít nhất 1 loại rác")
    @Size(min = 1, max = 3, message = "Chỉ được chọn tối đa 3 loại rác")
    List<String> wasteTypes; // HOUSEHOLD, RECYCLABLE, HAZARDOUS
}
