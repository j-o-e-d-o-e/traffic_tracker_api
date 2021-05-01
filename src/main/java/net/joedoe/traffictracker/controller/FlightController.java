package net.joedoe.traffictracker.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.config.SwaggerConfig;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.hateoas.FlightAssembler;
import net.joedoe.traffictracker.model.MapData;
import net.joedoe.traffictracker.service.FlightService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = { SwaggerConfig.FlightControllerTag})
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
        Page<FlightDto> page = service.getFlightsByDate(LocalDate.now(), pageable);
        return pagedAssembler.toModel(page, assembler);
    }

    @GetMapping(value = "/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedModel<?> getFlightsByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                            Pageable pageable, PagedResourcesAssembler<FlightDto> pagedAssembler) {
        Page<FlightDto> page = service.getFlightsByDate(date, pageable);
        return pagedAssembler.toModel(page, assembler);
    }

    @GetMapping(value = "/icao24/{icao_24}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedModel<?> getFlightsByIcao24(@PathVariable("icao_24") String icao, Pageable pageable, PagedResourcesAssembler<FlightDto> pagedAssembler) {
        Page<FlightDto> page = service.getFlightsByIcao(icao, pageable);
        return pagedAssembler.toModel(page, assembler);
    }

    @GetMapping("/id/{id}")
    public EntityModel<?> getFlightById(@PathVariable("id") Long id) {
        FlightDto flight = service.getFlightById(id);
        return assembler.toModel(flight);
    }

    @ApiIgnore
    @GetMapping("/one/day/{date}")
    public List<FlightDto> getFlightsListByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return service.getFlightsDtoListByDate(date);
    }

    @ApiIgnore
    @GetMapping("/live")
    public MapData getCurrentFlights() {
        return service.getCurrentFlights();
    }
}
