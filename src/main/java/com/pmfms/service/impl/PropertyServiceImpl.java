package com.pmfms.service.impl;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.property.PropertyRequest;
import com.pmfms.dto.property.PropertyResponse;
import com.pmfms.entity.Property;
import com.pmfms.entity.User;
import com.pmfms.enums.PropertyStatus;
import com.pmfms.enums.PropertyType;
import com.pmfms.mapper.EntityMapper;
import com.pmfms.repository.PropertyRepository;
import com.pmfms.repository.UserRepository;
import com.pmfms.service.PropertyService;
import com.pmfms.util.CodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public PropertyResponse create(PropertyRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("Owner user not found with id " + request.getOwnerId()));

        Property property = mapper.map(request, Property.class);
        property.setId(null);
        property.setOwner(owner);
        property.setCode(CodeGenerator.generate("PROP"));
        property.setStatus(PropertyStatus.DRAFT); // BRD 8.1: enters the approval workflow

        return mapper.map(propertyRepository.save(property), PropertyResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyResponse getById(Long id) {
        return mapper.map(findProperty(id), PropertyResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PropertyResponse> list(PropertyStatus status, PropertyType type, String city, int page, int size) {
        Page<Property> result = propertyRepository.search(status, type, city,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return PageResponse.of(result, mapper.mapPage(result, PropertyResponse.class));
    }

    @Override
    @Transactional
    public PropertyResponse update(Long id, PropertyRequest request) {
        Property property = findProperty(id);

        if (!property.getOwner().getId().equals(request.getOwnerId())) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Owner user not found with id " + request.getOwnerId()));
            property.setOwner(owner);
        }

        mapper.mapTo(request, property); // partial copy of scalar fields (nulls skipped)
        return mapper.map(propertyRepository.save(property), PropertyResponse.class);
    }

    @Override
    @Transactional
    public PropertyResponse changeStatus(Long id, PropertyStatus status) {
        Property property = findProperty(id);
        property.setStatus(status);
        return mapper.map(propertyRepository.save(property), PropertyResponse.class);
    }

    private Property findProperty(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id " + id));
    }
}
