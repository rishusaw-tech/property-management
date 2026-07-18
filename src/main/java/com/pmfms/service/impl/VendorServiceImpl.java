package com.pmfms.service.impl;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.vendor.VendorRatingRequest;
import com.pmfms.dto.vendor.VendorRequest;
import com.pmfms.dto.vendor.VendorResponse;
import com.pmfms.entity.User;
import com.pmfms.entity.Vendor;
import com.pmfms.enums.Role;
import com.pmfms.enums.VendorCategory;
import com.pmfms.enums.VendorStatus;
import com.pmfms.mapper.EntityMapper;
import com.pmfms.repository.UserRepository;
import com.pmfms.repository.VendorRepository;
import com.pmfms.service.VendorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public VendorResponse create(VendorRequest request) {
        Vendor vendor = mapper.map(request, Vendor.class);
        vendor.setId(null);
        vendor.setStatus(VendorStatus.ACTIVE);
        vendor.setUser(resolveVendorUser(request.getUserId()));

        return mapper.map(vendorRepository.save(vendor), VendorResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getById(Long id) {
        return mapper.map(findVendor(id), VendorResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VendorResponse> list(VendorCategory category, VendorStatus status, int page, int size) {
        Page<Vendor> result = vendorRepository.search(category, status,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return PageResponse.of(result, mapper.mapPage(result, VendorResponse.class));
    }

    @Override
    @Transactional
    public VendorResponse update(Long id, VendorRequest request) {
        Vendor vendor = findVendor(id);
        mapper.mapTo(request, vendor);
        if (request.getUserId() != null) {
            vendor.setUser(resolveVendorUser(request.getUserId()));
        }
        return mapper.map(vendorRepository.save(vendor), VendorResponse.class);
    }

    @Override
    @Transactional
    public VendorResponse updateRating(Long id, VendorRatingRequest request) {
        Vendor vendor = findVendor(id);
        vendor.setRating(request.getRating());
        return mapper.map(vendorRepository.save(vendor), VendorResponse.class);
    }

    @Override
    @Transactional
    public VendorResponse changeStatus(Long id, VendorStatus status) {
        Vendor vendor = findVendor(id);
        vendor.setStatus(status);
        return mapper.map(vendorRepository.save(vendor), VendorResponse.class);
    }

    private User resolveVendorUser(Long userId) {
        if (userId == null) return null;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));
        if (user.getRole() != Role.VENDOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Linked user must have role VENDOR");
        }
        return user;
    }

    private Vendor findVendor(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found with id " + id));
    }
}
