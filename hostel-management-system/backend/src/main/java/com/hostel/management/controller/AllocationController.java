package com.hostel.management.controller;

import com.hostel.management.dto.AllocationRequest;
import com.hostel.management.dto.AllocationResponse;
import com.hostel.management.service.AllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService allocationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AllocationResponse> allocateRoom(@Valid @RequestBody AllocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(allocationService.allocateRoom(request));
    }

    @PutMapping("/{id}/vacate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AllocationResponse> vacateRoom(@PathVariable Long id) {
        return ResponseEntity.ok(allocationService.vacateRoom(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AllocationResponse>> getForStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(allocationService.getAllocationsForStudent(studentId));
    }
}
