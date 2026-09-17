package com.hostel.management.controller;

import com.hostel.management.dto.ComplaintReport;
import com.hostel.management.dto.OccupancyReport;
import com.hostel.management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Access to this whole controller is already restricted to ADMIN at the
 * SecurityConfig level ("/api/reports/**" -> hasRole("ADMIN")), so no
 * per-method @PreAuthorize is needed here.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/occupancy")
    public OccupancyReport getOccupancyReport() {
        return reportService.getOccupancyReport();
    }

    @GetMapping("/complaints")
    public ComplaintReport getComplaintReport() {
        return reportService.getComplaintReport();
    }
}
