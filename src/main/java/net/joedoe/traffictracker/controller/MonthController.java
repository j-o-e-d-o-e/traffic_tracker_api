package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.MonthDto;
import net.joedoe.traffictracker.hateoas.MonthAssembler;
import net.joedoe.traffictracker.service.MonthService;
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
@RequestMapping("/api/months")
public class MonthController {
    private final MonthService service;
    private final MonthAssembler assembler;

    public MonthController(MonthService service, MonthAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("/current")
    public EntityModel<?> getCurrentMonth() {
        MonthDto monthDto = service.getMonth(LocalDate.now().withDayOfMonth(1));
        return assembler.toModel(monthDto);
    }

    @GetMapping("/{year}/{month}")
    public ResponseEntity<?> getMonthByDate(@PathVariable("year") Integer year, @PathVariable("month") Integer month) {
        MonthDto monthDto = service.getMonth(LocalDate.of(year, month, 1));
        EntityModel<MonthDto> model = assembler.toModel(monthDto);
        if (year < LocalDate.now().getYear() || month < LocalDate.now().getMonthValue()) {
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS)).body(model);
        }
        return ResponseEntity.ok().body(model);
    }
}
