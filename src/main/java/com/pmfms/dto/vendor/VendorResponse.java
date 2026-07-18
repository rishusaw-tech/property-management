package com.pmfms.dto.vendor;

import com.pmfms.enums.VendorCategory;
import com.pmfms.enums.VendorStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class VendorResponse {
    private Long id;
    private String companyName;
    private VendorCategory category;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private BigDecimal rating;
    private VendorStatus status;
    private LocalDate contractStart;
    private LocalDate contractEnd;
    private Long userId;
    private Instant createdAt;
    private Instant updatedAt;
}
