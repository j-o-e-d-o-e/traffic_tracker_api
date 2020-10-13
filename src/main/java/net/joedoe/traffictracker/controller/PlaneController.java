package net.joedoe.traffictracker.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.PlaneDto;
import net.joedoe.traffictracker.mapper.PlaneMapper;
import net.joedoe.traffictracker.model.MapData;
import net.joedoe.traffictracker.model.Plane;
import net.joedoe.traffictracker.service.PlaneService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("deprecation")
@Slf4j
@Api(value = "Plane Controller", description = "Only planes for last 30 days stored")
@RestController
@RequestMapping("/planes")
public class PlaneController {
    private final PlaneService service;
    private final PlaneMapper mapper;

    public PlaneController(PlaneService service, PlaneMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public PagedResources<PlaneDto> getPlanesForCurrentDay(
            Pageable pageable, PagedResourcesAssembler<Plane> pagedAssembler) {
        return pagedAssembler.toResource(service.getPlanesByDate(LocalDate.now(), pageable), mapper);
    }

    @GetMapping(value = "/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResources<PlaneDto> getPlanesByDate(
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            Pageable pageable, PagedResourcesAssembler<Plane> pagedAssembler) {
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

    @GetMapping(value = "/icao24/{icao_24}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResources<PlaneDto> getPlanesByIcao24(
            @PathVariable("icao_24") String icao, Pageable pageable,
            PagedResourcesAssembler<Plane> pagedAssembler) {
        if (icao == null)
            return null;
        return pagedAssembler.toResource(service.getPlanesByIcao(icao, pageable), mapper);
    }

    @GetMapping("/one/day/{date}")
    public List<PlaneDto> getPlanesListByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return mapper.toResources(service.getPlanesListByDate(date));
    }

    @GetMapping("/current")
    public MapData getCurrentPlanes() {
        return service.getCurrentPlanes();
    }
}
