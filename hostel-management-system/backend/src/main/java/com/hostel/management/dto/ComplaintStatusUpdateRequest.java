package com.hostel.management.dto;

import com.hostel.management.entity.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintStatusUpdateRequest {

    @NotNull
    private ComplaintStatus status;
}
