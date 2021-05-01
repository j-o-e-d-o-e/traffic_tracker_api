package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.hateoas.DayAssembler;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.model.Day;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/days")
public class DayController {
    private final DayService service;
    private final DayAssembler assembler;

    public DayController(DayService service, DayAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("/current")
    public EntityModel<DayDto> getCurrentDay() {
        DayDto day = service.getDayByDate(LocalDate.now());
        return assembler.toModel(day);
    }

    @GetMapping("/{date}")
    public EntityModel<DayDto> getDayByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        DayDto day = service.getDayByDate(date);
        return assembler.toModel(day);

    }

    @ApiIgnore
    @GetMapping("/all")
    public List<Day> findAll() {
        return service.findAll();
    }
}
