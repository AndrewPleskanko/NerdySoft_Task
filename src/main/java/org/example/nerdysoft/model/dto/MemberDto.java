package org.example.nerdysoft.model.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.Data;

@Data
public class MemberDto {
    private Long id;
    private String name;
    private LocalDate membershipDate;
    private Set<BookDto> borrowedBooks;
}
