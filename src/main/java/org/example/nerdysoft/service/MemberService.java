package org.example.nerdysoft.service;

import java.util.List;

import org.example.nerdysoft.model.dto.MemberDto;

public interface MemberService {
    List<MemberDto> getAllMembers();

    MemberDto getMemberById(Long id);

    MemberDto saveMember(MemberDto memberDto);

    void deleteMember(Long id);
}
