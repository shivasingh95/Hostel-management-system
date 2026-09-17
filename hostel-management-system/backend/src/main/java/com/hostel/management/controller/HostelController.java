package com.hostel.management.controller;

import com.hostel.management.entity.Hostel;
import com.hostel.management.repository.HostelRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hostels")
@RequiredArgsConstructor
public class HostelController {

    private final HostelRepository hostelRepository;

    @GetMapping
    public ResponseEntity<List<Hostel>> getAll() {
        return ResponseEntity.ok(hostelRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hostel> create(@Valid @RequestBody Hostel hostel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hostelRepository.save(hostel));
    }
}
