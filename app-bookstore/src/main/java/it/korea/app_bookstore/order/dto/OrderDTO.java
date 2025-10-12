package it.korea.app_bookstore.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import it.korea.app_bookstore.order.entity.OrderEntity;
import it.korea.app_bookstore.user.dto.UserSecureDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OrderDTO {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Response {
        private int orderId;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime orderDate;;
        private int totalPrice;
        private String status;
        private String userName;
        private String userId;
        //private List<OrderItemDTO> itemList;
     
        public static Response of(OrderEntity entity) {
            
            UserSecureDTO user = new UserSecureDTO(entity.getUser());
            //List<OrderItemDTO> itemList = entity.getItemList().stream().map(OrderItemDTO::of).toList();

            return Response.builder()
                .orderId(entity.getOrderId())
                .orderDate(entity.getOrderDate())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .userName(user.getUserName())
                .userId(user.getUserId())
                //.itemList(itemList)
                .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Detail {
        private int orderId;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime orderDate;;
        private int totalPrice;
        private String status;
        private String userName;
        private String userId;
        private List<OrderItemDTO> itemList;
     
        public static Detail of(OrderEntity entity) {
            
            UserSecureDTO user = new UserSecureDTO(entity.getUser());
            List<OrderItemDTO> itemList = entity.getItemList()
                .stream().map(OrderItemDTO::of).toList();

            return Detail.builder()
                .orderId(entity.getOrderId())
                .orderDate(entity.getOrderDate())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .userName(user.getUserName())
                .userId(user.getUserId())
                .itemList(itemList)
                .build();
        }
    }

    @Data
	public static class Request {

        private String userId;

        @Valid
        private List<InnerRequest> bookList;
	}

    @Data
    public static class InnerRequest {
        private int bookId;
        private int quantity;

    }
}
