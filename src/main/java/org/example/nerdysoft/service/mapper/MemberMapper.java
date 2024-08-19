package org.example.nerdysoft.service.mapper;

import org.example.nerdysoft.model.dto.MemberDetailedDto;
import org.example.nerdysoft.model.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberDetailedDto toDto(Member member);

    Member toEntity(MemberDetailedDto memberDetailedDto);
}
