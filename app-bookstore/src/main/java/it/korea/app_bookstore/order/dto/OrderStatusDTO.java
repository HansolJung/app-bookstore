package it.korea.app_bookstore.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusDTO {
    
    @NotNull(message = "주문 아이디는 필수 항목입니다.")
    private int orderId;
    @NotBlank(message = "주문 상태는 필수 항목입니다.")
    private String newStatus;
}
