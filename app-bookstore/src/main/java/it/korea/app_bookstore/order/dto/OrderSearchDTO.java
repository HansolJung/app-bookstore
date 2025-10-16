package it.korea.app_bookstore.order.dto;

import lombok.Data;

@Data
public class OrderSearchDTO {
    
    private String searchText;
    private String status;
    private String userId;
}

