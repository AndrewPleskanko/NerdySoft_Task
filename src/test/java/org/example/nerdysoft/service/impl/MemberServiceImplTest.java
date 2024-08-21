package org.example.nerdysoft.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import org.example.nerdysoft.constant.MessageConstants;
import org.example.nerdysoft.model.dto.MemberDetailedDto;
import org.example.nerdysoft.model.entity.Book;
import org.example.nerdysoft.model.entity.Member;
import org.example.nerdysoft.model.exception.MemberHasBorrowedBooksException;
import org.example.nerdysoft.model.exception.MemberNotFoundException;
import org.example.nerdysoft.output.persistent.MemberRepository;
import org.example.nerdysoft.service.mapper.MemberMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private MemberDetailedDto memberDetailedDto;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setName("Test Member");
        member.setBorrowedBooks(new HashSet<>());
        memberDetailedDto = new MemberDetailedDto();
        memberDetailedDto.setId(1L);
        memberDetailedDto.setName("Test Member");
    }

    @Test
    void getAllMembers() {
        when(memberRepository.findAllWithBorrowedBooks()).thenReturn(Collections.singletonList(member));
        when(memberMapper.toDto(member)).thenReturn(memberDetailedDto);

        var members = memberService.getAllMembers();

        assertNotNull(members);
        assertEquals(1, members.size());
        verify(memberRepository, times(1)).findAllWithBorrowedBooks();
        verify(memberMapper, times(1)).toDto(member);
    }

    @Test
    void getMemberById() {
        when(memberRepository.findByIdWithBorrowedBooks(1L)).thenReturn(Optional.of(member));
        when(memberMapper.toDto(member)).thenReturn(memberDetailedDto);

        var result = memberService.getMemberById(1L);

        assertNotNull(result);
        assertEquals(memberDetailedDto, result);
    }

    @Test
    void getMemberById_ThrowsMemberNotFoundException() {
        when(memberRepository.findByIdWithBorrowedBooks(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(1L));

        assertEquals(MessageConstants.MEMBER_NOT_FOUND_MESSAGE + 1L, exception.getMessage());
        verify(memberRepository, times(1)).findByIdWithBorrowedBooks(1L);
    }

    @Test
    void saveMember() {
        when(memberMapper.toEntity(memberDetailedDto)).thenReturn(member);
        when(memberRepository.save(member)).thenReturn(member);
        when(memberMapper.toDto(member)).thenReturn(memberDetailedDto);
        var result = memberService.saveMember(memberDetailedDto);

        assertNotNull(result);
        assertEquals(memberDetailedDto, result);
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void deleteMember() {
        when(memberRepository.findById(1L)).thenReturn(Optional.ofNullable(member));

        memberService.deleteMember(1L);

        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMember_ThrowsMemberNotFoundException() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(MemberNotFoundException.class, () -> memberService.deleteMember(1L));

        assertEquals(MessageConstants.MEMBER_NOT_FOUND_MESSAGE + 1L, exception.getMessage());
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void deleteMember_ThrowsMemberHasBorrowedBooksException() {
        member.setBorrowedBooks(Collections.singleton(new Book()));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        var exception = assertThrows(MemberHasBorrowedBooksException.class, () -> memberService.deleteMember(1L));

        assertEquals(MessageConstants.MEMBER_HAS_BORROWED_BOOKS_MESSAGE, exception.getMessage());
        verify(memberRepository, times(1)).findById(1L);
    }
}