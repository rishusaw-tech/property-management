package com.pmfms.service.impl;

import com.pmfms.dto.asset.AssetRequest;
import com.pmfms.dto.asset.AssetResponse;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.entity.Asset;
import com.pmfms.entity.Property;
import com.pmfms.enums.AssetCategory;
import com.pmfms.enums.AssetStatus;
import com.pmfms.mapper.EntityMapper;
import com.pmfms.repository.AssetRepository;
import com.pmfms.repository.PropertyRepository;
import com.pmfms.service.AssetService;
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
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final PropertyRepository propertyRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public AssetResponse create(AssetRequest request) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id " + request.getPropertyId()));

        Asset asset = mapper.map(request, Asset.class);
        asset.setId(null);
        asset.setProperty(property);
        asset.setTag(CodeGenerator.generate("AST")); // printed as QR/barcode label (BRD 9.1)
        asset.setStatus(AssetStatus.OPERATIONAL);

        return mapper.map(assetRepository.save(asset), AssetResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getById(Long id) {
        return mapper.map(findAsset(id), AssetResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getByTag(String tag) {
        Asset asset = assetRepository.findByTag(tag)
                .orElseThrow(() -> new EntityNotFoundException("Asset not found with tag " + tag));
        return mapper.map(asset, AssetResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AssetResponse> list(Long propertyId, AssetCategory category, AssetStatus status, int page, int size) {
        Page<Asset> result = assetRepository.search(propertyId, category, status,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return PageResponse.of(result, mapper.mapPage(result, AssetResponse.class));
    }

    @Override
    @Transactional
    public AssetResponse update(Long id, AssetRequest request) {
        Asset asset = findAsset(id);

        if (!asset.getProperty().getId().equals(request.getPropertyId())) {
            Property property = propertyRepository.findById(request.getPropertyId())
                    .orElseThrow(() -> new EntityNotFoundException("Property not found with id " + request.getPropertyId()));
            asset.setProperty(property);
        }

        mapper.mapTo(request, asset);
        return mapper.map(assetRepository.save(asset), AssetResponse.class);
    }

    @Override
    @Transactional
    public AssetResponse changeStatus(Long id, AssetStatus status) {
        Asset asset = findAsset(id);
        asset.setStatus(status);
        return mapper.map(assetRepository.save(asset), AssetResponse.class);
    }

    private Asset findAsset(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asset not found with id " + id));
    }
}
