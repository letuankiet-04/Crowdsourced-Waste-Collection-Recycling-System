package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateCitizenProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateCollectorProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateEnterpriseProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.exception.AppException;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.exception.ErrorCode;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.profile.CitizenRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final CitizenRepository citizenRepository;
    private final CollectorRepository collectorRepository;
    private final EnterpriseRepository enterpriseRepository;

    @Override
    @Transactional
    public Citizen updateCitizenProfile(String citizenEmail, UpdateCitizenProfileRequest request) {
        Citizen citizen = citizenRepository.findByUser_Email(citizenEmail)
                .orElseThrow(() -> new AppException(ErrorCode.CITIZEN_NOT_FOUND));

        if (request.getFullName() != null) {
            citizen.setFullName(request.getFullName());
        }
        if (request.getAddress() != null) {
            citizen.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            citizen.setPhone(request.getPhone());
        }
        if (request.getWard() != null) {
            citizen.setWard(request.getWard());
        }
        if (request.getCity() != null) {
            citizen.setCity(request.getCity());
        }

        return citizenRepository.save(citizen);
    }

    @Override
    @Transactional
    public Collector updateCollectorProfile(Integer collectorId, UpdateCollectorProfileRequest request) {
        Collector collector = collectorRepository.findById(collectorId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getFullName() != null) {
            collector.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            collector.setEmail(request.getEmail());
        }
        if (request.getVehicleType() != null) {
            collector.setVehicleType(request.getVehicleType());
        }
        if (request.getVehiclePlate() != null) {
            collector.setVehiclePlate(request.getVehiclePlate());
        }

        return collectorRepository.save(collector);
    }

    @Override
    @Transactional
    public Enterprise updateEnterpriseProfile(Integer enterpriseId, UpdateEnterpriseProfileRequest request) {
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getName() != null) {
            enterprise.setName(request.getName());
        }
        if (request.getAddress() != null) {
            enterprise.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            enterprise.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            enterprise.setEmail(request.getEmail());
        }
        if (request.getServiceWards() != null) {
            enterprise.setServiceWards(request.getServiceWards());
        }
        if (request.getServiceCities() != null) {
            enterprise.setServiceCities(request.getServiceCities());
        }

        return enterpriseRepository.save(enterprise);
    }
}

