package com.safo.icos.modules.user.application.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDto {
    private String phoneNumber;
    private String address;
    private LocalDate hireDate;
    private LocalDate birthDate;
}
