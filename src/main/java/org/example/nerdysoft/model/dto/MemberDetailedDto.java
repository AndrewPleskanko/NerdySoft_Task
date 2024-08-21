package org.example.nerdysoft.model.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetailedDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private LocalDate membershipDate;

    private List<BookMainInfoDto> borrowedBooks;
}
