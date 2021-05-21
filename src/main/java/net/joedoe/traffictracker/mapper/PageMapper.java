package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.dto.PageDto;
import org.springframework.data.domain.Page;

public class PageMapper<T> {

    public PageDto<T> toDto(Page<T> page) {
        return new PageDto<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getPageable().getPageNumber());
    }
}
