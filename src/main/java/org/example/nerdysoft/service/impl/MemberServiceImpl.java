package org.example.nerdysoft.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.example.nerdysoft.model.dto.MemberDto;
import org.example.nerdysoft.model.entity.Member;
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
    public List<MemberDto> getAllMembers() {
        log.info("Fetching all members");
        return memberRepository.findAll().stream().map(memberMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public MemberDto getMemberById(Long id) {
        log.info("Fetching member with id: {}", id);
        return memberRepository.findById(id).map(memberMapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    public MemberDto saveMember(MemberDto memberDto) {
        log.info("Saving member: {}", memberDto);
        Member member = memberMapper.toEntity(memberDto);
        member.setMembershipDate(LocalDate.now());
        return memberMapper.toDto(memberRepository.save(member));
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        log.info("Deleting member with id: {}", id);
        Member member = memberRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Member not found"));
        if (!member.getBorrowedBooks().isEmpty()) {
            throw new IllegalStateException("Member has borrowed books and cannot be deleted");
        }
        memberRepository.deleteById(id);
    }
}