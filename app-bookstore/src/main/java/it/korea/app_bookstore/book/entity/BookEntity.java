package it.korea.app_bookstore.book.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import it.korea.app_bookstore.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kb_books")
public class BookEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookId;
    private String title;
    private String author;
    private String publisher;
    private int price;
    private String description;
    private int stock;
    private LocalDate releaseDate;

    @Column(columnDefinition = "CHAR(1)")
    private String delYn;

    // 카테고리 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ca_id")
    private CategoryEntity category;

    // 파일(이미지) 매핑
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true) // 기본적으로 fetch = FetchType.LAZY
    private Set<BookFileEntity> fileList = new HashSet<>();    // NPE 방어 코드

    // 파일(이미지) 추가
    public void addFiles(BookFileEntity entity, boolean isUpdate) {
        if (fileList == null) {
            this.fileList = new HashSet<>();
        }

        entity.setBook(this);
        fileList.add(entity);

        if (isUpdate) { // 만약 파일을 업데이트 했다면...
            this.preUpdate();  // book 의 updateDate 갱신
        }
    }
}
