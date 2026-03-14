package com.team2.Crowdsourced_Waste_Collection_Recycling_System.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressMatchUtilTest {

    @Test
    void seedAddress_isAlwaysInServiceArea() {
        assertThat(AddressMatchUtil.isInServiceArea("Seed address", "Ward 1,Ward 2", "City A")).isTrue();
    }

    @Test
    void vietnameseDiacriticsAndAbbreviations_matchServiceArea() {
        String address = "Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh";
        assertThat(AddressMatchUtil.isInServiceArea(address, "Bến Nghé;Đa Kao", "HCM")).isTrue();
    }

    @Test
    void englishDistrictAndCity_matchVietnameseTokens() {
        String address = "District 1, Ho Chi Minh City";
        assertThat(AddressMatchUtil.isInServiceArea(address, null, "Quận 1")).isTrue();
    }
}
