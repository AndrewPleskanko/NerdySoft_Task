package org.example.nerdysoft.output.persistent;

import java.util.List;
import java.util.Optional;

import org.example.nerdysoft.model.entity.Book;
import org.example.nerdysoft.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.borrowedBooks WHERE m.name = :name")
    Optional<Member> findByNameWithBorrowedBooks(@Param("name") String name);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.borrowedBooks")
    List<Member> findAllWithBorrowedBooks();

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.borrowedBooks WHERE m.id = :id")
    Optional<Member> findByIdWithBorrowedBooks(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Member m SET m.borrowedBooks = :book WHERE m.id = :memberId")
    void updateBorrowedBooks(@Param("memberId") Long memberId, @Param("book") Book book);
}
