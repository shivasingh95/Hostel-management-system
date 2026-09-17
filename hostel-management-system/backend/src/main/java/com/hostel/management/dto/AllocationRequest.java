package com.hostel.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllocationRequest {

    @NotNull
    private Long studentId;

    @NotNull
    private Long roomId;
}
