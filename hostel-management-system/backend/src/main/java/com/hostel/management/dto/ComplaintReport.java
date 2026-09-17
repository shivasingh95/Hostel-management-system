package com.hostel.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ComplaintReport {
    private long totalComplaints;
    private long openCount;
    private long inProgressCount;
    private long resolvedCount;
    private Map<String, Long> byCategory;
}
