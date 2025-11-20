package com.example.localhub.service;

import com.example.localhub.domain.place.Place;
import com.example.localhub.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepo;

    public Place create(Place p){ return placeRepo.save(p); }

    public Page<Place> list(int page, int size){
        return placeRepo.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
    }

    public Place get(Long id){
        return placeRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("place not found"));
    }

    public void delete(Long id){ placeRepo.delete(get(id)); }
}

