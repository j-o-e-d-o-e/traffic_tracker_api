package net.joedoe.traffictracker.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeparturesDto {
    private int continental;
    private int continental_abs;
    private int international;
    private int international_abs;
    private int national;
    private int national_abs;
    private int unknown;
    private int unknown_abs;
}
