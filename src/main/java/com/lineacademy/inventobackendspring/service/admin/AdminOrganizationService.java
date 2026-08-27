package com.lineacademy.inventobackendspring.service.admin;

import com.lineacademy.inventobackendspring.domain.organization.Organization;
import com.lineacademy.inventobackendspring.dto.admin.organization.request.AdminUpdateOrganizationRequest;
import com.lineacademy.inventobackendspring.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrganizationService {

    private final OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    public List<Organization> getOrganizationList() {
        return organizationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Organization getOrganizationById(Long orgId) {
        return organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("ORGANIZATION_NOT_FOUND"));
    }

    @Transactional
    public Organization updateOrganization(Long orgId, AdminUpdateOrganizationRequest request) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_ORGANIZATION"));

        if (request.getName() != null) {
            org.updateName(request.getName());
        }
        if (request.getDescription() != null) {
            org.updateDescription(request.getDescription());
        }
        if (Boolean.TRUE.equals(request.getIsSuspended())) {
            org.markAsDeleted();
        } else if (Boolean.FALSE.equals(request.getIsSuspended())) {
            org.restore();
        }

        return org;
    }
}
