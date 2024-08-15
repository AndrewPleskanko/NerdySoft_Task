package org.example.nerdysoft.model.dto;

import java.util.Set;

import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private int amount;
    private Set<MemberDto> borrowers;
}
