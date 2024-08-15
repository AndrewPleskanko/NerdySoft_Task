package org.example.nerdysoft.output.persistent;

import java.util.Map;
import java.util.Set;

import org.example.nerdysoft.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.title = :title AND b.author = :author")
    Book findByNameAndAuthor(@Param("title") String name, @Param("author") String author);

    @Query("SELECT DISTINCT b.title FROM Book b JOIN b.borrowers")
    Set<String> findDistinctBorrowedBookNames();

    @Query("SELECT b.title, COUNT(b) FROM Book b JOIN b.borrowers GROUP BY b.title")
    Map<String, Long> findDistinctBorrowedBookNamesWithAmount();
}