package com.team2.Crowdsourced_Waste_Collection_Recycling_System.converter;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CollectorStatusConverter implements AttributeConverter<CollectorStatus, String> {

    @Override
    public String convertToDatabaseColumn(CollectorStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public CollectorStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return CollectorStatus.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; 
        }
    }
}
