package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
public class PageRequestDto {
    @Min(value = 0, message = "requested page number must not be negative")
    private int page;
    @Min(value = 0, message = "requested page size must not be negative")
    @Max(value = 20, message = "requested page size must be <= 20")
    private int size;
}
