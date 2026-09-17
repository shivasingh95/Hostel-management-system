package com.hostel.management.dto;

import com.hostel.management.entity.ComplaintCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintRequest {

    @NotNull
    private Long studentId;

    @NotNull
    private Long roomId;

    @NotNull
    private ComplaintCategory category;

    @NotBlank
    private String description;
}
