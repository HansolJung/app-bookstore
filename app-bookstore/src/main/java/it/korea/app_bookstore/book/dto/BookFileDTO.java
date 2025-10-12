package it.korea.app_bookstore.book.dto;

import java.time.LocalDateTime;

import it.korea.app_bookstore.book.entity.BookFileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookFileDTO {

    private int bfId;
    private String fileName;
    private String storedName;
    private String filePath;
    private String fileThumbName;
    private Long fileSize;
    private String mainYn;
    private LocalDateTime createDate;

    public static BookFileDTO of(BookFileEntity entity) {
        return BookFileDTO.builder()
            .bfId(entity.getBfId())
            .fileName(entity.getFileName())
            .storedName(entity.getStoredName())
            .filePath(entity.getFilePath())
            .fileThumbName(entity.getFileThumbName())
            .fileSize(entity.getFileSize())
            .createDate(entity.getCreateDate())
            .mainYn(entity.getMainYn())
            .build();
    }
}
