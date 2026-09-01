package com.lineacademy.inventobackendspring.service.admin;

import com.lineacademy.inventobackendspring.domain.organization.Organization;
import com.lineacademy.inventobackendspring.dto.admin.organization.request.AdminUpdateOrganizationRequest;
import com.lineacademy.inventobackendspring.dto.admin.organization.response.AdminOrganizationResponseDTO.OrganizationDetail;
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
    public List<OrganizationDetail> getOrganizationList() {
        return organizationRepository.findAll().stream()
                .map(OrganizationDetail::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrganizationDetail getOrganizationById(Long orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("ORGANIZATION_NOT_FOUND"));
        return OrganizationDetail.from(org);
    }

    @Transactional
    public OrganizationDetail updateOrganization(Long orgId, AdminUpdateOrganizationRequest request) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("ORGANIZATION_NOT_FOUND"));

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

        return OrganizationDetail.from(org);
    }
}