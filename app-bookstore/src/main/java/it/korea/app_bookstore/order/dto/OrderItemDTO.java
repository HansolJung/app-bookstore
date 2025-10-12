package it.korea.app_bookstore.order.dto;

import it.korea.app_bookstore.book.dto.BookDTO;
import it.korea.app_bookstore.order.entity.OrderItemEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderItemDTO {

    private int itemId;
    private int quantity;
    private int totalPrice;
    private BookDTO.Detail book;

    public static OrderItemDTO of(OrderItemEntity entity) {

        BookDTO.Detail book = BookDTO.Detail.of(entity.getBook());

        return OrderItemDTO.builder()
            .itemId(entity.getItemId())
            .quantity(entity.getQuantity())
            .totalPrice(entity.getTotalPrice())
            .book(book)
            .build();
    }
}
