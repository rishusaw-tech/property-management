package com.pmfms.service.impl;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.compliance.ComplianceRecordRequest;
import com.pmfms.dto.compliance.ComplianceRecordResponse;
import com.pmfms.entity.ComplianceRecord;
import com.pmfms.entity.Property;
import com.pmfms.enums.ComplianceStatus;
import com.pmfms.mapper.EntityMapper;
import com.pmfms.repository.ComplianceRecordRepository;
import com.pmfms.repository.PropertyRepository;
import com.pmfms.service.ComplianceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Compliance certificate tracker (BRD 9.6) with the 30-day expiring-soon
 * window from BRD 15.1 ("certificate expiry within 30 days").
 */
@Service
@RequiredArgsConstructor
public class ComplianceServiceImpl implements ComplianceService {

    private static final int EXPIRING_SOON_DAYS = 30;

    private final ComplianceRecordRepository complianceRepository;
    private final PropertyRepository propertyRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public ComplianceRecordResponse create(ComplianceRecordRequest request) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id " + request.getPropertyId()));

        ComplianceRecord record = mapper.map(request, ComplianceRecord.class);
        record.setId(null);
        record.setProperty(property);
        record.setStatus(deriveStatus(record.getExpiryDate()));

        return mapper.map(complianceRepository.save(record), ComplianceRecordResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplianceRecordResponse getById(Long id) {
        ComplianceRecord record = complianceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compliance record not found with id " + id));
        return mapper.map(record, ComplianceRecordResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ComplianceRecordResponse> list(Long propertyId, ComplianceStatus status, int page, int size) {
        Page<ComplianceRecord> result = complianceRepository.search(propertyId, status,
                PageRequest.of(page, size, Sort.by("expiryDate").ascending()));
        return PageResponse.of(result, mapper.mapPage(result, ComplianceRecordResponse.class));
    }

    /** Daily refresh of VALID / EXPIRING_SOON / EXPIRED statuses. */
    @Scheduled(cron = "${app.scheduler.compliance-status-cron:0 15 0 * * *}")
    @Transactional
    public void refreshStatuses() {
        List<ComplianceRecord> all = complianceRepository.findAll();
        all.forEach(r -> r.setStatus(deriveStatus(r.getExpiryDate())));
        complianceRepository.saveAll(all);
    }

    private ComplianceStatus deriveStatus(LocalDate expiryDate) {
        LocalDate today = LocalDate.now();
        if (!expiryDate.isAfter(today)) {
            return ComplianceStatus.EXPIRED;
        }
        if (!expiryDate.isAfter(today.plusDays(EXPIRING_SOON_DAYS))) {
            return ComplianceStatus.EXPIRING_SOON;
        }
        return ComplianceStatus.VALID;
    }
}
