package com.pmfms.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * springdoc / Swagger configuration (BRD 11.3 - OpenAPI documentation).
 * Swagger UI: /swagger-ui.html   |   Spec: /v3/api-docs
 * The "bearerAuth" scheme lets you paste an access token once via the
 * Authorize button and call every protected endpoint.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "PMFMS - Property Management & Facility Management API",
                version = "v1",
                description = "REST APIs covering the property management lifecycle "
                        + "(properties, units, leases, billing) and facility management "
                        + "(assets, work orders, vendors, compliance) as per BRD v1.0.",
                contact = @Contact(name = "PMFMS Team")
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
