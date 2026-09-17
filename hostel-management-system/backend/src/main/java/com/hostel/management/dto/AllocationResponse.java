package com.hostel.management.dto;

import com.hostel.management.entity.AllocationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AllocationResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long roomId;
    private String roomNumber;
    private LocalDate allocatedDate;
    private LocalDate vacatedDate;
    private AllocationStatus status;
}
