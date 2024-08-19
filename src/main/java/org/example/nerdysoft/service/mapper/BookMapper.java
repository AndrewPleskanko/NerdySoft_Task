package org.example.nerdysoft.service.mapper;

import org.example.nerdysoft.model.dto.BookDetailedDto;
import org.example.nerdysoft.model.dto.BookMainInfoDto;
import org.example.nerdysoft.model.entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDetailedDto toDto(Book book);

    Book toEntity(BookDetailedDto bookDetailedDto);
}

