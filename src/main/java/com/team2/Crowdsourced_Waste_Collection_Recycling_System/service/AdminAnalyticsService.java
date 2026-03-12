package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminCollectedWeightChartResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminCollectedWeightDailyChartResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CitizenLeaderboardResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorLeaderboardResponse;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminSystemAnalyticsResponse;

import java.util.List;

public interface AdminAnalyticsService {
    AdminSystemAnalyticsResponse getSystemAnalytics();

    AdminCollectedWeightChartResponse getCollectedWeightChart(Integer year);

    AdminCollectedWeightDailyChartResponse getCollectedWeightDailyChart(Integer year, Integer month);

    List<CollectorLeaderboardResponse> getCollectorLeaderboard(Integer day, Integer month, Integer year);

    List<CitizenLeaderboardResponse> getCitizenLeaderboard(Integer day, Integer month, Integer year);

    com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminCollectedWasteByUnitResponse getCollectedWasteByUnit();
}
