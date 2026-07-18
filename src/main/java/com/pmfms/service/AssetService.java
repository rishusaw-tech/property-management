package com.pmfms.service;

import com.pmfms.dto.asset.AssetRequest;
import com.pmfms.dto.asset.AssetResponse;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.enums.AssetCategory;
import com.pmfms.enums.AssetStatus;

public interface AssetService {

    AssetResponse create(AssetRequest request);

    AssetResponse getById(Long id);

    AssetResponse getByTag(String tag);

    PageResponse<AssetResponse> list(Long propertyId, AssetCategory category, AssetStatus status, int page, int size);

    AssetResponse update(Long id, AssetRequest request);

    AssetResponse changeStatus(Long id, AssetStatus status);
}
