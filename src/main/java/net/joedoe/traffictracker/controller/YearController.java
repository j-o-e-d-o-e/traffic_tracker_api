package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.hateoas.YearAssembler;
import net.joedoe.traffictracker.dto.YearDto;
import net.joedoe.traffictracker.service.YearService;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/years")
public class YearController {
    private final YearService service;
    private final YearAssembler assembler;

    public YearController(YearService service, YearAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("/current")
    public EntityModel<?> getCurrentYear() {
        YearDto yearDto = service.getYear(LocalDate.now().withDayOfMonth(1).withMonth(1));
        return assembler.toModel(yearDto);
    }

    @GetMapping("/{year}")
    public EntityModel<?> getYearByDate(@PathVariable("year") Integer year) {
        YearDto yearDto = service.getYear(LocalDate.of(year, 1, 1));
        return assembler.toModel(yearDto);
    }
}
