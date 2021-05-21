package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.hateoas.DayAssembler;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

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
    public ResponseEntity<?> getCurrentDay() {
        DayDto day = service.getDayByDate(LocalDate.now());
        EntityModel<DayDto> model = assembler.toModel(day);
        return ResponseEntity.ok().body(model);
    }

    @GetMapping("/{date}")
    public ResponseEntity<?> getDayByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        DayDto day = service.getDayByDate(date);
        EntityModel<DayDto> model = assembler.toModel(day);
        if (date.isBefore(LocalDate.now())) {
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS)).body(model);
        }
        return ResponseEntity.ok().body(model);
    }
}
