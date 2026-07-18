package com.pmfms.service;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.lease.LeaseRenewRequest;
import com.pmfms.dto.lease.LeaseRequest;
import com.pmfms.dto.lease.LeaseResponse;
import com.pmfms.enums.LeaseStatus;

public interface LeaseService {

    LeaseResponse create(LeaseRequest request);

    LeaseResponse getById(Long id);

    PageResponse<LeaseResponse> list(LeaseStatus status, Long tenantId, Long unitId, int page, int size);

    LeaseResponse submit(Long id);

    LeaseResponse approve(Long id);

    LeaseResponse renew(Long id, LeaseRenewRequest request);

    LeaseResponse serveNotice(Long id);

    LeaseResponse terminate(Long id);

    LeaseResponse close(Long id);
}
