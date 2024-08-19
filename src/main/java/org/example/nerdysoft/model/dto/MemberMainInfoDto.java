package org.example.nerdysoft.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberMainInfoDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private LocalDate membershipDate;
}
