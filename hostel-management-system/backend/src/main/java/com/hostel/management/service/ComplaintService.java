package com.hostel.management.service;

import com.hostel.management.dto.ComplaintRequest;
import com.hostel.management.dto.ComplaintResponse;
import com.hostel.management.dto.ComplaintStatusUpdateRequest;
import com.hostel.management.entity.Complaint;
import com.hostel.management.entity.ComplaintStatus;
import com.hostel.management.entity.Room;
import com.hostel.management.entity.Student;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.repository.ComplaintRepository;
import com.hostel.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;
    private final RoomService roomService;
    private final NotificationService notificationService;

    public ComplaintResponse raiseComplaint(ComplaintRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + request.getStudentId()));
        Room room = roomService.findRoomOrThrow(request.getRoomId());

        Complaint complaint = new Complaint();
        complaint.setStudent(student);
        complaint.setRoom(room);
        complaint.setCategory(request.getCategory());
        complaint.setDescription(request.getDescription());
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint.setRaisedDate(LocalDate.now());

        return toResponse(complaintRepository.save(complaint));
    }

    public ComplaintResponse updateStatus(Long complaintId, ComplaintStatusUpdateRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + complaintId));

        complaint.setStatus(request.getStatus());
        if (request.getStatus() == ComplaintStatus.RESOLVED) {
            complaint.setResolvedDate(LocalDate.now());
        }

        Complaint saved = complaintRepository.save(complaint);

        // Fire-and-forget: doesn't block this request/response cycle.
        notificationService.notifyComplaintStatusChange(
                saved.getStudent().getEmail(), saved.getRoom().getRoomNumber(), saved.getStatus().name());

        return toResponse(saved);
    }

    public List<ComplaintResponse> getComplaintsForRoom(Long roomId) {
        return complaintRepository.findByRoomId(roomId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ComplaintResponse> getComplaintsForStudent(Long studentId) {
        return complaintRepository.findByStudentId(studentId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ComplaintResponse toResponse(Complaint c) {
        return new ComplaintResponse(
                c.getId(),
                c.getStudent().getName(),
                c.getRoom().getRoomNumber(),
                c.getCategory(),
                c.getDescription(),
                c.getStatus(),
                c.getRaisedDate(),
                c.getResolvedDate()
        );
    }
}
