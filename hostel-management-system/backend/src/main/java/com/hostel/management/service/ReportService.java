package com.hostel.management.service;

import com.hostel.management.dto.ComplaintReport;
import com.hostel.management.dto.OccupancyReport;
import com.hostel.management.entity.ComplaintCategory;
import com.hostel.management.entity.ComplaintStatus;
import com.hostel.management.repository.ComplaintRepository;
import com.hostel.management.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final RoomRepository roomRepository;
    private final ComplaintRepository complaintRepository;

    public OccupancyReport getOccupancyReport() {
        long total = roomRepository.count();
        long occupied = roomRepository.findAll().stream().filter(r -> r.getOccupiedCount() > 0).count();
        long vacant = total - occupied;
        double percentage = total == 0 ? 0.0 : (occupied * 100.0) / total;

        return new OccupancyReport(total, occupied, vacant, Math.round(percentage * 100.0) / 100.0);
    }

    public ComplaintReport getComplaintReport() {
        long total = complaintRepository.count();
        long open = complaintRepository.countByStatus(ComplaintStatus.OPEN);
        long inProgress = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS);
        long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);

        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (ComplaintCategory category : ComplaintCategory.values()) {
            byCategory.put(category.name(), complaintRepository.countByCategory(category));
        }

        return new ComplaintReport(total, open, inProgress, resolved, byCategory);
    }
}
