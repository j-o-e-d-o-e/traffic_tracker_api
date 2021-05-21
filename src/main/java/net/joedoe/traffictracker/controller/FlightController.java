package net.joedoe.traffictracker.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.config.SwaggerConfig;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.hateoas.FlightAssembler;
import net.joedoe.traffictracker.service.FlightService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = {SwaggerConfig.FlightControllerTag})
@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private final FlightService service;
    private final FlightAssembler assembler;

    public FlightController(FlightService service, FlightAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("/current")
    public PagedModel<?> getFlightsForCurrentDay(Pageable pageable, PagedResourcesAssembler<FlightDto> pagedAssembler) {
        Page<FlightDto> page = service.getByDate(LocalDate.now(), pageable);
        return pagedAssembler.toModel(page, assembler);
    }

    @GetMapping(value = "/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFlightsByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                              Pageable pageable, PagedResourcesAssembler<FlightDto> pagedAssembler) {
        Page<FlightDto> page = service.getByDate(date, pageable);
        PagedModel<?> model = pagedAssembler.toModel(page, assembler);
        if (date.isBefore(LocalDate.now())) {
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS)).body(model);
        }
        return ResponseEntity.ok().body(model);
    }
}
