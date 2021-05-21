package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageDto<T> {
    private List<T> content;
    private Long totalElements;
    private int totalPages;
    private int pageNumber;
}
