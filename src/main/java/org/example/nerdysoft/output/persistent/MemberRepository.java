package org.example.nerdysoft.output.persistent;

import java.util.List;
import java.util.Optional;

import org.example.nerdysoft.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByName(String name);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.borrowedBooks")
    List<Member> findAllWithBorrowedBooks();

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.borrowedBooks WHERE m.id = :id")
    Optional<Member> findByIdWithBorrowedBooks(@Param("id") Long id);
}
