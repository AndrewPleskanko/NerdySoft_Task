package org.example.nerdysoft.model.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookMainInfoDto {

    private Long id;

    @NotBlank(message = "Title is required")
    @Pattern(regexp = "^[A-Z][a-zA-Z\\s]{2,}$", message =
            "Title should start with a capital letter and have at least 3 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Pattern(regexp = "^[A-Z][a-z]+\\s[A-Z][a-z]+$", message =
            "Author should contain two capital words (Name and Surname)")
    private String author;

    @Min(value = 0, message = "Amount should be at least 0")
    private int amount;
}