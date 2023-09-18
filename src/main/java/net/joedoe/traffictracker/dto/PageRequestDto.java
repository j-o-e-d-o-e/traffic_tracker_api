package net.joedoe.traffictracker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageRequestDto {
    @Min(value = 0, message = "requested page number must not be negative")
    private int page;
    @Min(value = 0, message = "requested page size must not be negative")
    @Max(value = 20, message = "requested page size must be <= 20")
    private int size;
}
