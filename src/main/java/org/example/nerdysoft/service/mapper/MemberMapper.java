package org.example.nerdysoft.service.mapper;

import org.example.nerdysoft.model.dto.MemberDto;
import org.example.nerdysoft.model.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    MemberDto toDto(Member member);

    Member toEntity(MemberDto memberDto);
}
