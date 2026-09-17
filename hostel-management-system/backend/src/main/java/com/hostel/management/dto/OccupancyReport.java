package com.hostel.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OccupancyReport {
    private long totalRooms;
    private long occupiedRooms;
    private long vacantRooms;
    private double occupancyPercentage;
}
