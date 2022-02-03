package net.joedoe.traffictracker.controller;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.DayDto;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.hateoas.DayAssembler;
import net.joedoe.traffictracker.repo.DeviceRepository;
import net.joedoe.traffictracker.service.DayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/days")
public class DayController {
    private final DayService service;
    private final DayAssembler assembler;
    private final DeviceRepository deviceRepository;

    public DayController(DayService service, DayAssembler assembler, DeviceRepository deviceRepository) {
        this.service = service;
        this.assembler = assembler;
        this.deviceRepository = deviceRepository;
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentDay() {
        DayDto day = service.getDayLatest();
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

    @GetMapping("")
    public ResponseEntity<?> getDates() {
        List<LocalDate> dates = service.getDates();
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(3600 * 24, TimeUnit.SECONDS)).body(dates);
    }

    @ApiIgnore
    @PostMapping("/{date}")
    public ResponseEntity<?> postFlights(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                         @RequestHeader("Authorization") String auth, @RequestBody List<FlightDto> flights) {
        if (!auth.equals(deviceRepository.findByName("").getPw())) // enter device name
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<FlightDto> savedFlights = service.addFlights(date, flights);
        return ResponseEntity.ok().body(savedFlights);
    }
}
