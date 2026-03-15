package com.team2.Crowdsourced_Waste_Collection_Recycling_System.util;

import java.util.Locale;
import java.util.Set;

public final class WasteCategoryAllowlist {
    private WasteCategoryAllowlist() {
    }

    private static final Set<String> ALLOWED = Set.of(
            normalize("Giấy"),
            normalize("Báo"),
            normalize("Giấy, hồ sơ"),
            normalize("Giấy tập"),
            normalize("Lon bia"),
            normalize("Sắt"),
            normalize("Sắt lon"),
            normalize("Inox"),
            normalize("Đồng"),
            normalize("Nhôm"),
            normalize("Chai thủy tinh"),
            normalize("Bao bì, hỗn hợp"),
            normalize("Meca"),
            normalize("Mủ"),
            normalize("Mủ bình"),
            normalize("Mủ tôn"),
            normalize("Mủ đen")
    );

    public static boolean isAllowed(String name) {
        if (name == null) {
            return false;
        }
        return ALLOWED.contains(normalize(name));
    }

    private static String normalize(String input) {
        return input.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
