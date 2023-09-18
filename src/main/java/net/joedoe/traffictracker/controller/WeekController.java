package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.dto.WeekDto;
import net.joedoe.traffictracker.hateoas.WeekAssembler;
import net.joedoe.traffictracker.service.WeekService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

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
    public EntityModel<?> getWeekLatest() {
        WeekDto week = service.getWeekLatest();
        return assembler.toModel(week);
    }

    @GetMapping("/{date}")
    public ResponseEntity<?> getWeekByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        date = date.with(DayOfWeek.MONDAY);
        WeekDto week = service.getWeekByDate(date);
        EntityModel<WeekDto> model = assembler.toModel(week);
        if (date.isBefore(LocalDate.now().with(DayOfWeek.MONDAY))) {
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS)).body(model);
        }
        return ResponseEntity.ok().body(model);
    }
}
