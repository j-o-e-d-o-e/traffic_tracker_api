package net.joedoe.traffictracker.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.PlaneDto;
import net.joedoe.traffictracker.mapper.PlaneMapper;
import net.joedoe.traffictracker.model.MapData;
import net.joedoe.traffictracker.service.PlaneService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Api(value="Plane Controller", description = "Only planes for last 30 days stored")
@RestController
@RequestMapping("/planes")
public class PlaneController {
    private final PlaneService service;
    private PlaneMapper mapper;

    public PlaneController(PlaneService service, PlaneMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public PagedResources<PlaneDto> getPlanesForCurrentDay(
            Pageable pageable, PagedResourcesAssembler<net.joedoe.traffictracker.model.Plane> pagedAssembler) {
        return pagedAssembler.toResource(service.getPlanesByDate(LocalDate.now(), pageable), mapper);
    }

    @GetMapping("/{date}")
    public PagedResources<PlaneDto> getPlanesByDate(
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            Pageable pageable, PagedResourcesAssembler<net.joedoe.traffictracker.model.Plane> pagedAssembler) {
        if (date == null)
            return null;
        return pagedAssembler.toResource(service.getPlanesByDate(date, pageable), mapper);
    }

    @GetMapping("/id/{id}")
    public PlaneDto getPlaneById(@PathVariable("id") Long id) {
        if (id == null)
            return null;
        return mapper.toResource(service.getPlaneById(id));
    }

    @GetMapping("/icao24/{icao_24}")
    public PagedResources<PlaneDto> getPlanesByIcao24(
            @PathVariable("icao_24") String icao, Pageable pageable,
            PagedResourcesAssembler<net.joedoe.traffictracker.model.Plane> pagedAssembler) {
        if (icao == null)
            return null;
        return pagedAssembler.toResource(service.getPlanesByIcao(icao, pageable), mapper);
    }

    @GetMapping("/current")
    public MapData getCurrentPlanes() {
        return service.getCurrentPlanes();
    }

    @GetMapping("/max/altitude")
    public List<PlaneDto> getPlanesWithMaxAltitude() {
        return mapper.toResources(service.getPlanesWithMaxAltitude());
    }

    @GetMapping("/max/speed")
    public List<PlaneDto> getPlanesWithMaxSpeed() {
        return mapper.toResources(service.getPlanesWithMaxSpeed());
    }

    @GetMapping("/min/altitude")
    public List<PlaneDto> getPlanesWithMinAltitude() {
        return mapper.toResources(service.getPlanesWithMinAltitude());
    }

    @GetMapping("/min/speed")
    public List<PlaneDto> getPlanesWithMinSpeed() {
        return mapper.toResources(service.getPlanesWithMinSpeed());
    }
}
