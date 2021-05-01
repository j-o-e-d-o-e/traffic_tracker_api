package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.hateoas.WeekAssembler;
import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.service.WeekService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/weeks")
public class WeekController {
    private final WeekService service;
    private final WeekAssembler assembler;

    public WeekController(WeekService service, WeekAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("/current")
    public EntityModel<?> getCurrentWeek() {
        WeekDto week = service.getWeek(LocalDate.now().with(DayOfWeek.MONDAY));
        return assembler.toModel(week);
    }

    @GetMapping("/{date}")
    public EntityModel<?> getWeekByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        WeekDto week = service.getWeek(date.with(DayOfWeek.MONDAY));
        return assembler.toModel(week);
    }
}
