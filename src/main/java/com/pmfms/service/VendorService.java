package com.pmfms.service;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.vendor.VendorRatingRequest;
import com.pmfms.dto.vendor.VendorRequest;
import com.pmfms.dto.vendor.VendorResponse;
import com.pmfms.enums.VendorCategory;
import com.pmfms.enums.VendorStatus;

public interface VendorService {

    VendorResponse create(VendorRequest request);

    VendorResponse getById(Long id);

    PageResponse<VendorResponse> list(VendorCategory category, VendorStatus status, int page, int size);

    VendorResponse update(Long id, VendorRequest request);

    VendorResponse updateRating(Long id, VendorRatingRequest request);

    VendorResponse changeStatus(Long id, VendorStatus status);
}
