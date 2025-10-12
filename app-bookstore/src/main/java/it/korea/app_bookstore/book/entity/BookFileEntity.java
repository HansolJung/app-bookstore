package it.korea.app_bookstore.book.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kb_book_files")
public class BookFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bfId;
    private String fileName;
    private String storedName;
    private String filePath;
    private Long fileSize;
    private String fileThumbName;

    @Column(columnDefinition = "CHAR(1)")
    private String mainYn;

    @Column(updatable = false)
    private LocalDateTime createDate;

    // 도서 매핑
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private BookEntity book;
}
