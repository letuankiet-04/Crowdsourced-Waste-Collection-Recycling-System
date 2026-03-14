package com.team2.Crowdsourced_Waste_Collection_Recycling_System.util;

import lombok.experimental.UtilityClass;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@UtilityClass
public class AddressMatchUtil {
    public static boolean isInServiceArea(String address, String serviceWards, String serviceCities) {
        if (address == null || address.isBlank()) {
            return true;
        }

        String normalizedAddress = normalize(address);
        if (normalizedAddress.isBlank()) {
            return true;
        }
        if (normalizedAddress.contains("seed address")) {
            return true;
        }

        boolean wardOk = matches(normalizedAddress, tokenize(serviceWards));
        boolean cityOk = matches(normalizedAddress, tokenize(serviceCities));
        return wardOk && cityOk;
    }

    public static String normalize(String input) {
        if (input == null) {
            return "";
        }
        String normalized = input.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return "";
        }
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd');

        normalized = normalized.replaceAll("[^a-z0-9]+", " ").trim().replaceAll("\\s+", " ");

        normalized = normalized.replaceAll("\\btp\\b", "thanh pho");
        normalized = normalized.replaceAll("\\bhcmc\\b", "ho chi minh");
        normalized = normalized.replaceAll("\\bhcm\\b", "ho chi minh");
        normalized = normalized.replaceAll("\\bho chi minh city\\b", "ho chi minh");
        normalized = normalized.replaceAll("\\bhochiminh\\b", "ho chi minh");
        normalized = normalized.replaceAll("\\bdistrict\\b", "quan");
        normalized = normalized.replaceAll("\\bward\\b", "phuong");
        normalized = normalized.replaceAll("\\bq\\b", "quan");
        normalized = normalized.replaceAll("\\bp\\b", "phuong");

        return normalized.trim().replaceAll("\\s+", " ");
    }

    public static List<String> tokenize(String list) {
        if (list == null || list.isBlank()) {
            return List.of();
        }
        String cleaned = list.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .replace("'", "")
                .trim();
        if (cleaned.isBlank()) {
            return List.of();
        }
        String[] parts = cleaned.split("[,;|\\n]");
        List<String> tokens = new ArrayList<>();
        for (String part : parts) {
            String token = normalize(part);
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }
        return List.copyOf(tokens);
    }

    private static boolean matches(String normalizedAddress, List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return true;
        }
        for (String token : tokens) {
            if (token != null && !token.isBlank() && normalizedAddress.contains(token)) {
                return true;
            }
        }
        return false;
    }
}
