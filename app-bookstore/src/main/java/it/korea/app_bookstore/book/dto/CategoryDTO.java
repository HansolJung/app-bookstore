package it.korea.app_bookstore.book.dto;

import it.korea.app_bookstore.book.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CategoryDTO {

    private int caId;
    private String caName;

    public static CategoryDTO of(CategoryEntity entity) {
        return CategoryDTO.builder()
            .caId(entity.getCaId())
            .caName(entity.getCaName())
            .build();
    }
}
