package com.hostel.management.service;

import com.hostel.management.dto.AllocationRequest;
import com.hostel.management.dto.AllocationResponse;
import com.hostel.management.entity.Allocation;
import com.hostel.management.entity.AllocationStatus;
import com.hostel.management.entity.Room;
import com.hostel.management.entity.Student;
import com.hostel.management.exception.DuplicateAllocationException;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.exception.RoomFullException;
import com.hostel.management.repository.AllocationRepository;
import com.hostel.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final StudentRepository studentRepository;
    private final RoomService roomService;

    @Transactional
    public AllocationResponse allocateRoom(AllocationRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + request.getStudentId()));

        // Business rule: a student cannot hold two active allocations at once.
        allocationRepository.findByStudentIdAndStatus(student.getId(), AllocationStatus.ACTIVE)
                .ifPresent(a -> {
                    throw new DuplicateAllocationException(
                            "Student already has an active room allocation (Room " + a.getRoom().getRoomNumber() + ")");
                });

        Room room = roomService.findRoomOrThrow(request.getRoomId());

        // Business rule: cannot allocate beyond room capacity.
        if (room.isFull()) {
            throw new RoomFullException("Room " + room.getRoomNumber() + " is already at full capacity");
        }

        room.setOccupiedCount(room.getOccupiedCount() + 1);
        roomService.syncCache(room);

        Allocation allocation = new Allocation();
        allocation.setStudent(student);
        allocation.setRoom(room);
        allocation.setAllocatedDate(LocalDate.now());
        allocation.setStatus(AllocationStatus.ACTIVE);

        Allocation saved = allocationRepository.save(allocation);
        return toResponse(saved);
    }

    @Transactional
    public AllocationResponse vacateRoom(Long allocationId) {
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found: " + allocationId));

        allocation.setStatus(AllocationStatus.VACATED);
        allocation.setVacatedDate(LocalDate.now());

        Room room = allocation.getRoom();
        room.setOccupiedCount(Math.max(0, room.getOccupiedCount() - 1));
        roomService.syncCache(room);

        return toResponse(allocationRepository.save(allocation));
    }

    public List<AllocationResponse> getAllocationsForStudent(Long studentId) {
        return allocationRepository.findByStudentId(studentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AllocationResponse toResponse(Allocation a) {
        return new AllocationResponse(
                a.getId(),
                a.getStudent().getId(),
                a.getStudent().getName(),
                a.getRoom().getId(),
                a.getRoom().getRoomNumber(),
                a.getAllocatedDate(),
                a.getVacatedDate(),
                a.getStatus()
        );
    }
}
