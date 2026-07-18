package com.pmfms.service.impl;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.lease.LeaseRenewRequest;
import com.pmfms.dto.lease.LeaseRequest;
import com.pmfms.dto.lease.LeaseResponse;
import com.pmfms.entity.Lease;
import com.pmfms.entity.Unit;
import com.pmfms.entity.User;
import com.pmfms.enums.LeaseStatus;
import com.pmfms.enums.Role;
import com.pmfms.enums.UnitStatus;
import com.pmfms.mapper.EntityMapper;
import com.pmfms.repository.LeaseRepository;
import com.pmfms.repository.UnitRepository;
import com.pmfms.repository.UserRepository;
import com.pmfms.service.LeaseService;
import com.pmfms.util.CodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * Implements the lease lifecycle of BRD 14.2:
 * Draft -> Pending Approval -> Active -> Renewal-Due -> Notice-Served -> Terminated -> Closed.
 */
@Service
@RequiredArgsConstructor
public class LeaseServiceImpl implements LeaseService {

    private static final List<LeaseStatus> OPEN_STATUSES =
            List.of(LeaseStatus.DRAFT, LeaseStatus.PENDING_APPROVAL, LeaseStatus.ACTIVE,
                    LeaseStatus.RENEWAL_DUE, LeaseStatus.NOTICE_SERVED);

    private final LeaseRepository leaseRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public LeaseResponse create(LeaseRequest request) {
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new EntityNotFoundException("Unit not found with id " + request.getUnitId()));

        if (unit.getStatus() != UnitStatus.VACANT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unit is not VACANT (current: " + unit.getStatus() + ")");
        }
        if (leaseRepository.existsByUnitIdAndStatusIn(unit.getId(), OPEN_STATUSES)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unit already has an open lease");
        }

        User tenant = userRepository.findById(request.getTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found with id " + request.getTenantId()));
        if (tenant.getRole() != Role.TENANT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected user is not a TENANT");
        }
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }

        Lease lease = mapper.map(request, Lease.class);
        lease.setId(null);
        lease.setUnit(unit);
        lease.setTenant(tenant);
        lease.setCode(CodeGenerator.generate("LSE"));
        lease.setStatus(LeaseStatus.DRAFT);

        return mapper.map(leaseRepository.save(lease), LeaseResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaseResponse getById(Long id) {
        return mapper.map(findLease(id), LeaseResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LeaseResponse> list(LeaseStatus status, Long tenantId, Long unitId, int page, int size) {
        Page<Lease> result = leaseRepository.search(status, tenantId, unitId,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return PageResponse.of(result, mapper.mapPage(result, LeaseResponse.class));
    }

    @Override
    @Transactional
    public LeaseResponse submit(Long id) {
        Lease lease = requireStatus(findLease(id), LeaseStatus.DRAFT);
        lease.setStatus(LeaseStatus.PENDING_APPROVAL);
        return mapper.map(leaseRepository.save(lease), LeaseResponse.class);
    }

    @Override
    @Transactional
    public LeaseResponse approve(Long id) {
        Lease lease = requireStatus(findLease(id), LeaseStatus.PENDING_APPROVAL);
        lease.setStatus(LeaseStatus.ACTIVE);

        // Move-in (BRD 8.3): unit becomes occupied
        Unit unit = lease.getUnit();
        unit.setStatus(UnitStatus.OCCUPIED);
        unitRepository.save(unit);

        return mapper.map(leaseRepository.save(lease), LeaseResponse.class);
    }

    @Override
    @Transactional
    public LeaseResponse renew(Long id, LeaseRenewRequest request) {
        Lease lease = findLease(id);
        if (lease.getStatus() != LeaseStatus.ACTIVE && lease.getStatus() != LeaseStatus.RENEWAL_DUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only ACTIVE or RENEWAL_DUE leases can be renewed (current: " + lease.getStatus() + ")");
        }
        if (!request.getNewEndDate().isAfter(lease.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New end date must be after current end date");
        }

        // Renewal with rent revision (BRD 8.5)
        lease.setEndDate(request.getNewEndDate());
        lease.setMonthlyRent(request.getRevisedMonthlyRent());
        lease.setStatus(LeaseStatus.ACTIVE);

        return mapper.map(leaseRepository.save(lease), LeaseResponse.class);
    }

    @Override
    @Transactional
    public LeaseResponse serveNotice(Long id) {
        Lease lease = findLease(id);
        if (lease.getStatus() != LeaseStatus.ACTIVE && lease.getStatus() != LeaseStatus.RENEWAL_DUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Notice can only be served on ACTIVE or RENEWAL_DUE leases (current: " + lease.getStatus() + ")");
        }

        // Notice-period validation (BRD 8.5): notice must be served while the
        // lease still has at least `noticePeriodDays` remaining OR before end date.
        if (LocalDate.now().isAfter(lease.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lease has already ended; use terminate instead");
        }

        lease.setStatus(LeaseStatus.NOTICE_SERVED);
        return mapper.map(leaseRepository.save(lease), LeaseResponse.class);
    }

    @Override
    @Transactional
    public LeaseResponse terminate(Long id) {
        Lease lease = findLease(id);
        if (lease.getStatus() == LeaseStatus.TERMINATED || lease.getStatus() == LeaseStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lease is already terminated/closed");
        }

        lease.setStatus(LeaseStatus.TERMINATED);

        // Move-out (BRD 8.6): unit becomes vacant again
        Unit unit = lease.getUnit();
        unit.setStatus(UnitStatus.VACANT);
        unitRepository.save(unit);

        return mapper.map(leaseRepository.save(lease), LeaseResponse.class);
    }

    @Override
    @Transactional
    public LeaseResponse close(Long id) {
        Lease lease = requireStatus(findLease(id), LeaseStatus.TERMINATED);
        lease.setStatus(LeaseStatus.CLOSED);
        return mapper.map(leaseRepository.save(lease), LeaseResponse.class);
    }

    /**
     * Daily job: flag ACTIVE leases expiring within 90 days as RENEWAL_DUE
     * (BRD 8.5 - automated lease expiry alerts).
     */
    @Scheduled(cron = "${app.scheduler.lease-renewal-cron:0 0 1 * * *}")
    @Transactional
    public void markLeasesRenewalDue() {
        List<Lease> expiring = leaseRepository.findByStatusAndEndDateBetween(
                LeaseStatus.ACTIVE, LocalDate.now(), LocalDate.now().plusDays(90));
        expiring.forEach(l -> l.setStatus(LeaseStatus.RENEWAL_DUE));
        leaseRepository.saveAll(expiring);
    }

    private Lease requireStatus(Lease lease, LeaseStatus expected) {
        if (lease.getStatus() != expected) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lease must be " + expected + " (current: " + lease.getStatus() + ")");
        }
        return lease;
    }

    private Lease findLease(Long id) {
        return leaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lease not found with id " + id));
    }
}
