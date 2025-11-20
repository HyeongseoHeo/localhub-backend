package com.example.localhub.controller;

import com.example.localhub.domain.place.Place;
import com.example.localhub.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @PostMapping
    public Place create(@Valid @RequestBody Place req){ // 간단히 엔티티 바인딩
        return placeService.create(req);
    }

    @GetMapping
    public Page<Place> list(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size){
        return placeService.list(page, size);
    }

    @GetMapping("/{id}")
    public Place get(@PathVariable Long id){ return placeService.get(id); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){ placeService.delete(id); }
}
