package net.joedoe.traffictracker.controller;

import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.hateoas.FlightAssembler;
import net.joedoe.traffictracker.service.FlightService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/planes")
public class PlaneController {
    private final FlightService service;
    private final FlightAssembler assembler;

    public PlaneController(FlightService service, FlightAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(value = "/{icao}/flights", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFlightsByPlaneIcao(@PathVariable("icao") String icao, Pageable pageable, PagedResourcesAssembler<FlightDto> pagedAssembler) {
        Page<FlightDto> page = service.getByPlaneIcao(icao, pageable);
        PagedModel<?> model = pagedAssembler.toModel(page, assembler);
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS)).body(model);
    }
}
