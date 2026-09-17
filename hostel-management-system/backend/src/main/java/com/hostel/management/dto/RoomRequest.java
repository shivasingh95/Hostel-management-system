package com.hostel.management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequest {

    @NotBlank
    private String roomNumber;

    private int floor;

    @Min(1)
    private int capacity;

    @NotNull
    private Long hostelId;
}
