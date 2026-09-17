package com.hostel.management.service;

import com.hostel.management.dto.AllocationRequest;
import com.hostel.management.entity.*;
import com.hostel.management.exception.DuplicateAllocationException;
import com.hostel.management.exception.RoomFullException;
import com.hostel.management.repository.AllocationRepository;
import com.hostel.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllocationServiceTest {

    @Mock
    private AllocationRepository allocationRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private AllocationService allocationService;

    private Student student;
    private Room room;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setName("Test Student");
        student.setEmail("test@example.com");

        Hostel hostel = new Hostel();
        hostel.setId(1L);
        hostel.setName("Block A");

        room = new Room();
        room.setId(1L);
        room.setRoomNumber("A-101");
        room.setCapacity(2);
        room.setOccupiedCount(0);
        room.setHostel(hostel);
    }

    @Test
    void allocateRoom_succeeds_whenRoomHasSpace() {
        AllocationRequest request = new AllocationRequest();
        request.setStudentId(1L);
        request.setRoomId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(allocationRepository.findByStudentIdAndStatus(1L, AllocationStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(roomService.findRoomOrThrow(1L)).thenReturn(room);
        when(allocationRepository.save(any(Allocation.class))).thenAnswer(inv -> {
            Allocation a = inv.getArgument(0);
            a.setId(99L);
            return a;
        });

        var response = allocationService.allocateRoom(request);

        assertEquals("A-101", response.getRoomNumber());
        assertEquals(1, room.getOccupiedCount());
        verify(roomService).syncCache(room);
    }

    @Test
    void allocateRoom_throwsRoomFullException_whenRoomAtCapacity() {
        room.setOccupiedCount(2); // capacity is 2 -> full

        AllocationRequest request = new AllocationRequest();
        request.setStudentId(1L);
        request.setRoomId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(allocationRepository.findByStudentIdAndStatus(1L, AllocationStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(roomService.findRoomOrThrow(1L)).thenReturn(room);

        assertThrows(RoomFullException.class, () -> allocationService.allocateRoom(request));
    }

    @Test
    void allocateRoom_throwsDuplicateAllocationException_whenStudentAlreadyHasActiveRoom() {
        Allocation existing = new Allocation();
        existing.setRoom(room);
        existing.setStatus(AllocationStatus.ACTIVE);

        AllocationRequest request = new AllocationRequest();
        request.setStudentId(1L);
        request.setRoomId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(allocationRepository.findByStudentIdAndStatus(1L, AllocationStatus.ACTIVE))
                .thenReturn(Optional.of(existing));

        assertThrows(DuplicateAllocationException.class, () -> allocationService.allocateRoom(request));
    }
}
