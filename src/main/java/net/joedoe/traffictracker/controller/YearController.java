package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.YearDto;
import net.joedoe.traffictracker.hateoas.YearAssembler;
import net.joedoe.traffictracker.service.YearService;
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
    public ResponseEntity<?> getYearByDate(@PathVariable("year") Integer year) {
        YearDto yearDto = service.getYear(LocalDate.of(year, 1, 1));
        EntityModel<YearDto> model = assembler.toModel(yearDto);
        if (year < LocalDate.now().getYear()) {
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS)).body(model);
        }
        return ResponseEntity.ok().body(model);
    }
}
