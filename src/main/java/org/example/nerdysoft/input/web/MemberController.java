package org.example.nerdysoft.input.web;

import java.util.List;

import org.example.nerdysoft.model.dto.MemberDetailedDto;
import org.example.nerdysoft.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public List<MemberDetailedDto> getAllMembers() {
        log.info("GET /api/members - Fetching all members");
        return memberService.getAllMembers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDetailedDto> getMemberById(@PathVariable Long id) {
        log.info("GET /api/members/{} - Fetching member by id", id);
        MemberDetailedDto member = memberService.getMemberById(id);
        if (member == null) {
            log.error("Member not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(member);
    }

    @PostMapping
    public MemberDetailedDto createMember(@Valid @RequestBody MemberDetailedDto member) {
        log.info("POST /api/members - Creating member: {}", member);
        return memberService.saveMember(member);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberDetailedDto> updateMember(@PathVariable Long id,
                                                          @Valid @RequestBody MemberDetailedDto memberDetails) {
        log.info("PUT /api/members/{} - Updating member: {}", id, memberDetails);
        MemberDetailedDto member = memberService.getMemberById(id);
        if (member == null) {
            log.error("Member not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
        member.setName(memberDetails.getName());
        return ResponseEntity.ok(memberService.saveMember(member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        log.info("DELETE /api/members/{} - Deleting member", id);
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}