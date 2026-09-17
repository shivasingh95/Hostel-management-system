package com.hostel.management.repository;

import com.hostel.management.entity.Complaint;
import com.hostel.management.entity.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByRoomId(Long roomId);

    List<Complaint> findByStudentId(Long studentId);

    long countByStatus(ComplaintStatus status);

    long countByCategory(com.hostel.management.entity.ComplaintCategory category);
}
