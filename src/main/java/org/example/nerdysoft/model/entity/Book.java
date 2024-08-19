package org.example.nerdysoft.model.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "books")
public class Book extends AbstractEntity {

    @NotBlank(message = "Title is required")
    @Pattern(regexp = "^[A-Z][a-zA-Z]{2,}$", message = "Title should start with a capital letter "
            + "and be at least 3 characters long")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        if (!super.equals(o)) return false;
        return getAmount() == book.getAmount() && Objects.equals(getTitle(), book.getTitle()) && Objects.equals(getAuthor(), book.getAuthor()) && Objects.equals(getBorrowers(), book.getBorrowers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTitle(), getAuthor(), getAmount(), getBorrowers());
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", amount=" + amount +
                '}';
    }
}