package com.hostel.management.repository;

import com.hostel.management.entity.Allocation;
import com.hostel.management.entity.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    List<Allocation> findByStudentId(Long studentId);

    Optional<Allocation> findByStudentIdAndStatus(Long studentId, AllocationStatus status);

    List<Allocation> findByRoomIdAndStatus(Long roomId, AllocationStatus status);

    long countByStatus(AllocationStatus status);
}
