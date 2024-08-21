package org.example.nerdysoft.model.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "books")
public class Book extends AbstractEntity {

    @NotBlank(message = "Title is required")
    @Pattern(regexp = "^[A-Z][a-zA-Z ]{2,}$", message = "Title should start with a capital letter,"
            + " be at least 3 characters long, and contain only letters and spaces")
    private String title;

    @NotBlank(message = "Author is required")
    @Pattern(regexp = "^[A-Z][a-zA-Z]+\\s[A-Z][a-zA-Z]+$", message =
            "Author should contain two capital words with name and surname")
    private String author;

    @Min(value = 0, message = "Amount should be at least 0")
    private int amount;

    @JsonManagedReference
    @ManyToMany(mappedBy = "borrowedBooks")
    private Set<Member> borrowers;
}