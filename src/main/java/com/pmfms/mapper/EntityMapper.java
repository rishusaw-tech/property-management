package com.pmfms.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The single mapping component used by EVERY service for entity <-> DTO
 * conversion. Wraps one shared ModelMapper instance (see ModelMapperConfig)
 * so that mapping behavior is consistent across the whole application.
 */
@Component
@RequiredArgsConstructor
public class EntityMapper {

    private final ModelMapper modelMapper;

    /** Map a single object to the target type. */
    public <S, T> T map(S source, Class<T> targetClass) {
        return source == null ? null : modelMapper.map(source, targetClass);
    }

    /** Map a list of objects to a list of the target type. */
    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source.stream().map(e -> map(e, targetClass)).toList();
    }

    /** Map a Spring Data page's content to a list of the target type. */
    public <T> List<T> mapPage(Page<?> page, Class<T> targetClass) {
        return page.getContent().stream().map(e -> map(e, targetClass)).toList();
    }

    /**
     * Copy non-null properties of source onto an existing target instance.
     * Used for partial updates (PUT/PATCH) so unspecified fields are preserved.
     */
    public <S, T> void mapTo(S source, T target) {
        modelMapper.map(source, target);
    }
}
