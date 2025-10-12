package it.korea.app_bookstore.basket.dto;

import it.korea.app_bookstore.basket.entity.BasketEntity;
import it.korea.app_bookstore.book.dto.BookDTO;
import it.korea.app_bookstore.user.dto.UserSecureDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BasketDTO {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Detail {
        private int basketId;
        private int quantity;
        private int totalPrice;
        private String userName;
        private String userId;
        private BookDTO.Response book;
     
        public static Detail of(BasketEntity entity) {
            
            UserSecureDTO user = new UserSecureDTO(entity.getUser());
            BookDTO.Response book = BookDTO.Response.of(entity.getBook());

            return Detail.builder()
                .basketId(entity.getBasketId())
                .quantity(entity.getQuantity())
                .totalPrice(entity.getTotalPrice())
                .userName(user.getUserName())
                .userId(user.getUserId())
                .book(book)
                .build();
        }
    }

    @Data
	public static class Request {

        private String userId;

        @Valid
        private InnerRequest book;
	}

    @Data
    public static class InnerRequest {
        private int bookId;
        private int quantity;
    }
}
