package it.korea.app_bookstore.book.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;


import it.korea.app_bookstore.book.entity.BookEntity;
import it.korea.app_bookstore.book.entity.CategoryEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookDTO {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Response {
        private int bookId;
        private String title;
        private String category;
        private String author;
        private String publisher;
        private int price;
        private int stock;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate releaseDate;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createDate;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateDate;
        private String fileName;
        private String storedName;
        private String filePath;
        private String fileThumbName;
        private String delYn;

        public static Response of(BookEntity entity) {

            // 파일 엔티티를 파일 DTO 로 객체 변환
            // 바로 이때 파일 리스트가 SELECT 된다.
            List<BookFileDTO> fileList = 
                entity.getFileList().stream().map(BookFileDTO::of).filter((file)-> 
                    file.getMainYn().equals("Y")).toList();
            
            BookFileDTO mainImage = null;

            if (fileList != null && fileList.size() > 0) {
                mainImage = fileList.get(0);
            }

            return Response.builder()
                .bookId(entity.getBookId())
                .title(entity.getTitle())
                .category(entity.getCategory().getCaName())
                .author(entity.getAuthor())
                .publisher(entity.getPublisher())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .releaseDate(entity.getReleaseDate())
                .createDate(entity.getCreateDate())
                .updateDate(entity.getUpdateDate())
                .fileName(mainImage != null ? mainImage.getFileName() : null)
                .storedName(mainImage != null ? mainImage.getStoredName() : null)
                .filePath(mainImage != null ? mainImage.getFilePath() : null)
                .fileThumbName(mainImage != null ? mainImage.getFileThumbName() : null)
                .delYn(entity.getDelYn())
                .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Detail {
        private int bookId;
        private String title;
        private String author;
        private String publisher;
        private int price;
        private String description;
        private int stock;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate releaseDate;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createDate;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateDate;
        private String category;
        private int categoryId;
        private List<BookFileDTO> fileList;
        private String delYn;

        public static Detail of(BookEntity entity) {

            // 파일 엔티티를 파일 DTO 로 객체 변환
            // 바로 이때 파일 리스트가 SELECT 된다.
            List<BookFileDTO> fileList = 
                entity.getFileList().stream().map(BookFileDTO::of)
                .sorted((o1, o2)-> o2.getMainYn().equals("Y") ? 1 : -1).toList();
            
            CategoryEntity categoryEntity = entity.getCategory();

            return Detail.builder()
                .bookId(entity.getBookId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .publisher(entity.getPublisher())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .stock(entity.getStock())
                .releaseDate(entity.getReleaseDate())
                .createDate(entity.getCreateDate())
                .updateDate(entity.getUpdateDate())
                .category(categoryEntity.getCaName())
                .categoryId(categoryEntity.getCaId())
                .fileList(fileList)
                .delYn(entity.getDelYn())
                .build();
        }
    }

    @Data
	public static class Request {
		private int bookId;

        @NotNull(message = "카테고리는 필수 항목입니다.")
        private int caId;
        @NotBlank(message = "제목은 필수 항목입니다.")
        private String title;
        @NotBlank(message = "저자는 필수 항목입니다.")
        private String author;
        @NotBlank(message = "출판사는 필수 항목입니다.")
        private String publisher;
        @NotNull(message = "가격은 필수 항목입니다.")
        private int price;
        @NotBlank(message = "설명은 필수 항목입니다.")
        private String description;
        @NotNull(message = "발행일은 필수 항목입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate releaseDate;
        private int stock;
	
		// 메인 이미지
		private MultipartFile mainImage;

        // 이미지
        private List<MultipartFile> image;
	}
}
