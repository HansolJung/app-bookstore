package it.korea.app_bookstore.book.dto;

import lombok.Data;

@Data
public class BookSearchDTO {
    //private String searchType;
    private String searchText;
    private int caId;
}
