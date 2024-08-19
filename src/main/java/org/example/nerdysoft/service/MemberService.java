package org.example.nerdysoft.service;

import java.util.List;

import org.example.nerdysoft.model.dto.MemberDetailedDto;

public interface MemberService {
    List<MemberDetailedDto> getAllMembers();

    MemberDetailedDto getMemberById(Long id);

    MemberDetailedDto saveMember(MemberDetailedDto memberDetailedDto);

    void deleteMember(Long id);
}
