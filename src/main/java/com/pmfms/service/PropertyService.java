package com.pmfms.service;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.property.PropertyRequest;
import com.pmfms.dto.property.PropertyResponse;
import com.pmfms.enums.PropertyStatus;
import com.pmfms.enums.PropertyType;

public interface PropertyService {

    PropertyResponse create(PropertyRequest request);

    PropertyResponse getById(Long id);

    PageResponse<PropertyResponse> list(PropertyStatus status, PropertyType type, String city, int page, int size);

    PropertyResponse update(Long id, PropertyRequest request);

    PropertyResponse changeStatus(Long id, PropertyStatus status);
}
