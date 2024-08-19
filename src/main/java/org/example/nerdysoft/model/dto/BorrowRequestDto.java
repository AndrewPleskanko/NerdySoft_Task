package org.example.nerdysoft.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowRequestDto {
    private Long memberId;
    private Long bookId;
}
