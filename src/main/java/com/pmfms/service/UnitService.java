package com.pmfms.service;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.unit.UnitRequest;
import com.pmfms.dto.unit.UnitResponse;
import com.pmfms.enums.UnitStatus;

public interface UnitService {

    UnitResponse create(UnitRequest request);

    UnitResponse getById(Long id);

    PageResponse<UnitResponse> list(Long propertyId, UnitStatus status, int page, int size);

    UnitResponse update(Long id, UnitRequest request);

    UnitResponse changeStatus(Long id, UnitStatus status);
}
