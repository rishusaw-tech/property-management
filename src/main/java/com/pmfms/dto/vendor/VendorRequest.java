package com.pmfms.dto.vendor;

import com.pmfms.enums.VendorCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VendorRequest {

    @NotBlank @Size(max = 160)
    private String companyName;

    @NotNull
    private VendorCategory category;

    @Size(max = 120)
    private String contactName;

    @NotBlank @Email @Size(max = 160)
    private String contactEmail;

    @Pattern(regexp = "^$|^[+0-9][0-9 \\-]{6,18}$", message = "Invalid phone number")
    private String contactPhone;

    private LocalDate contractStart;

    private LocalDate contractEnd;

    /** Optional: link an existing User (role VENDOR) for portal login. */
    private Long userId;
}
