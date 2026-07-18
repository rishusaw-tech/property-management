package com.pmfms.service.impl;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.unit.UnitRequest;
import com.pmfms.dto.unit.UnitResponse;
import com.pmfms.entity.Property;
import com.pmfms.entity.Unit;
import com.pmfms.enums.UnitStatus;
import com.pmfms.mapper.EntityMapper;
import com.pmfms.repository.PropertyRepository;
import com.pmfms.repository.UnitRepository;
import com.pmfms.service.UnitService;
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
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final PropertyRepository propertyRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public UnitResponse create(UnitRequest request) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id " + request.getPropertyId()));

        Unit unit = mapper.map(request, Unit.class);
        unit.setId(null);
        unit.setProperty(property);
        unit.setCode(CodeGenerator.generate("UNIT"));
        unit.setStatus(UnitStatus.VACANT);

        return mapper.map(unitRepository.save(unit), UnitResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UnitResponse getById(Long id) {
        return mapper.map(findUnit(id), UnitResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UnitResponse> list(Long propertyId, UnitStatus status, int page, int size) {
        Page<Unit> result = unitRepository.search(propertyId, status,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return PageResponse.of(result, mapper.mapPage(result, UnitResponse.class));
    }

    @Override
    @Transactional
    public UnitResponse update(Long id, UnitRequest request) {
        Unit unit = findUnit(id);

        if (!unit.getProperty().getId().equals(request.getPropertyId())) {
            Property property = propertyRepository.findById(request.getPropertyId())
                    .orElseThrow(() -> new EntityNotFoundException("Property not found with id " + request.getPropertyId()));
            unit.setProperty(property);
        }

        mapper.mapTo(request, unit);
        if (request.getAmenities() != null) {
            unit.getAmenities().clear();
            unit.getAmenities().addAll(request.getAmenities());
        }

        return mapper.map(unitRepository.save(unit), UnitResponse.class);
    }

    @Override
    @Transactional
    public UnitResponse changeStatus(Long id, UnitStatus status) {
        Unit unit = findUnit(id);
        unit.setStatus(status);
        return mapper.map(unitRepository.save(unit), UnitResponse.class);
    }

    private Unit findUnit(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found with id " + id));
    }
}
