package net.joedoe.traffictracker.controller;

import io.swagger.v3.oas.annotations.Hidden;
import net.joedoe.traffictracker.dto.FlightDto;
import net.joedoe.traffictracker.hateoas.FlightAssembler;
import net.joedoe.traffictracker.repo.DeviceRepository;
import net.joedoe.traffictracker.service.FlightService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private final FlightService service;
    private final FlightAssembler assembler;
    private final DeviceRepository deviceRepository;

    public FlightController(FlightService service, FlightAssembler assembler, DeviceRepository deviceRepository) {
        this.service = service;
        this.assembler = assembler;
        this.deviceRepository = deviceRepository;
    }

    @GetMapping("/current")
    public PagedModel<?> getFlightsForLatestDay(Pageable pageable, PagedResourcesAssembler<FlightDto> pagedAssembler) {
        Page<FlightDto> page = service.getFlightsForLatestDay(pageable);
        return pagedAssembler.toModel(page, assembler);
    }

    @GetMapping(value = "/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFlightsByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Pageable pageable, PagedResourcesAssembler<FlightDto> pagedAssembler) {
        Page<FlightDto> page = service.getFlightsByDate(date, pageable);
        PagedModel<?> model = pagedAssembler.toModel(page, assembler);
        if (date.isBefore(LocalDate.now())) {
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS)).body(model);
        }
        return ResponseEntity.ok().body(model);
    }


    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        byte[] media = service.loadPhoto(id);
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS)).body(media);
    }

    @Hidden
    @PostMapping("/{id}/image")
    public ResponseEntity<?> postPhotoById(@PathVariable Long id, @RequestHeader("Authorization") String auth, @RequestBody() MultipartFile img) {
        if (!auth.equals(deviceRepository.findByName("raspi").getPw()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            service.savePhoto(id, img);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
}
