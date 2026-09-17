package com.hostel.management.controller;

import com.hostel.management.dto.ComplaintRequest;
import com.hostel.management.dto.ComplaintResponse;
import com.hostel.management.dto.ComplaintStatusUpdateRequest;
import com.hostel.management.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ComplaintResponse> raiseComplaint(@Valid @RequestBody ComplaintRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(complaintService.raiseComplaint(request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplaintResponse> updateStatus(@PathVariable Long id,
                                                           @Valid @RequestBody ComplaintStatusUpdateRequest request) {
        return ResponseEntity.ok(complaintService.updateStatus(id, request));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ComplaintResponse>> getForRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(complaintService.getComplaintsForRoom(roomId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ComplaintResponse>> getForStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(complaintService.getComplaintsForStudent(studentId));
    }
}
