package com.pmfms.service;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.compliance.ComplianceRecordRequest;
import com.pmfms.dto.compliance.ComplianceRecordResponse;
import com.pmfms.enums.ComplianceStatus;

public interface ComplianceService {

    ComplianceRecordResponse create(ComplianceRecordRequest request);

    ComplianceRecordResponse getById(Long id);

    PageResponse<ComplianceRecordResponse> list(Long propertyId, ComplianceStatus status, int page, int size);
}
