package org.example.nerdysoft.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.example.nerdysoft.constant.MessageConstants;
import org.example.nerdysoft.model.dto.MemberDetailedDto;
import org.example.nerdysoft.model.entity.Member;
import org.example.nerdysoft.model.exception.MemberHasBorrowedBooksException;
import org.example.nerdysoft.model.exception.MemberNotFoundException;
import org.example.nerdysoft.output.persistent.MemberRepository;
import org.example.nerdysoft.service.MemberService;
import org.example.nerdysoft.service.mapper.MemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Override
    public List<MemberDetailedDto> getAllMembers() {
        log.info("Fetching all members with borrowed books");
        List<Member> members = memberRepository.findAllWithBorrowedBooks();
        return members.stream()
                .map(memberMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MemberDetailedDto getMemberById(Long id) {
        log.info("Fetching member with id: {}", id);
        return memberRepository.findByIdWithBorrowedBooks(id)
                .map(memberMapper::toDto)
                .orElseThrow(() -> new MemberNotFoundException(MessageConstants.MEMBER_NOT_FOUND_MESSAGE + id));
    }

    @Override
    @Transactional
    public MemberDetailedDto saveMember(MemberDetailedDto memberDetailedDto) {
        log.info("Saving member: {}", memberDetailedDto);
        Member member = memberMapper.toEntity(memberDetailedDto);
        member.setMembershipDate(LocalDate.now());
        return memberMapper.toDto(memberRepository.save(member));
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        log.info("Deleting member with id: {}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(MessageConstants.MEMBER_NOT_FOUND_MESSAGE + id));
        if (!member.getBorrowedBooks().isEmpty()) {
            throw new MemberHasBorrowedBooksException(MessageConstants.MEMBER_HAS_BORROWED_BOOKS_MESSAGE);
        }
        memberRepository.deleteById(id);
        log.debug("Member with id {} has been deleted.", id);
    }
}
