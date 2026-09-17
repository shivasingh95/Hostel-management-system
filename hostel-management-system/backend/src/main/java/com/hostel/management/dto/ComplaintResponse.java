package com.hostel.management.dto;

import com.hostel.management.entity.ComplaintCategory;
import com.hostel.management.entity.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ComplaintResponse {
    private Long id;
    private String studentName;
    private String roomNumber;
    private ComplaintCategory category;
    private String description;
    private ComplaintStatus status;
    private LocalDate raisedDate;
    private LocalDate resolvedDate;
}
