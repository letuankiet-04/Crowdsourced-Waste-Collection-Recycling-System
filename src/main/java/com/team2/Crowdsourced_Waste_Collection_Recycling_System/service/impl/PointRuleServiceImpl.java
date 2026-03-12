package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.PointRuleRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.PointRuleResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.PointRule;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.PointRuleRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.PointRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointRuleServiceImpl implements PointRuleService {

    private static final int SINGLETON_ID = 1;
    private final PointRuleRepository pointRuleRepository;

    @Override
    @Transactional(readOnly = true)
    public PointRuleResponse getPointRule() {
        PointRule rule = pointRuleRepository.findById(SINGLETON_ID)
                .orElse(new PointRule(SINGLETON_ID, null, null));
        return toResponse(rule);
    }

    @Override
    @Transactional
    public PointRuleResponse updatePointRule(PointRuleRequest request) {
        PointRule rule = pointRuleRepository.findById(SINGLETON_ID)
                .orElse(new PointRule(SINGLETON_ID, null, null));
        rule.setContent(request.getContent());
        rule.setUpdatedAt(LocalDateTime.now());
        return toResponse(pointRuleRepository.save(rule));
    }

    private PointRuleResponse toResponse(PointRule rule) {
        return PointRuleResponse.builder()
                .content(rule.getContent())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}
