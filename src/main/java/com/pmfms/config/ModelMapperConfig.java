package com.pmfms.config;

import com.pmfms.dto.asset.AssetRequest;
import com.pmfms.dto.compliance.ComplianceRecordRequest;
import com.pmfms.dto.lease.LeaseRequest;
import com.pmfms.dto.property.PropertyRequest;
import com.pmfms.dto.unit.UnitRequest;
import com.pmfms.dto.vendor.VendorRequest;
import com.pmfms.entity.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * One shared ModelMapper bean for the entire application (injected only
 * through {@link com.pmfms.mapper.EntityMapper}).
 *
 * STRICT matching + flattening gives us, on the RESPONSE side:
 *   property.getId()    -> propertyId
 *   owner.getFullName() -> ownerFullName
 * with zero per-field configuration.
 *
 * On the REQUEST side we explicitly SKIP association fields (owner, unit,
 * tenant, ...) so ids like `ownerId` can never be flattened INTO a managed
 * entity's association. Services resolve and set associations deliberately
 * after validating that the referenced rows exist.
 */
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        mapper.typeMap(PropertyRequest.class, Property.class)
                .addMappings(m -> m.skip(Property::setOwner));

        mapper.typeMap(UnitRequest.class, Unit.class)
                .addMappings(m -> m.skip(Unit::setProperty));

        mapper.typeMap(LeaseRequest.class, Lease.class)
                .addMappings(m -> {
                    m.skip(Lease::setUnit);
                    m.skip(Lease::setTenant);
                });

        mapper.typeMap(AssetRequest.class, Asset.class)
                .addMappings(m -> m.skip(Asset::setProperty));

        mapper.typeMap(VendorRequest.class, Vendor.class)
                .addMappings(m -> m.skip(Vendor::setUser));

        mapper.typeMap(ComplianceRecordRequest.class, ComplianceRecord.class)
                .addMappings(m -> m.skip(ComplianceRecord::setProperty));

        return mapper;
    }
}
