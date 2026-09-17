package com.hostel.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private int floor;
    private int capacity;
    private int occupiedCount;
    private boolean full;
    private String hostelName;
}
