package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapEntryDto {
    private String name;
    private int flights;
}

