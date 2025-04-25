package org.lalit.ecommercebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.lalit.ecommercebackend.model.Order;
import org.lalit.ecommercebackend.model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;

    @NotBlank
    private String shippingAddress;

    @NotNull
    private Long userId;

    @NotEmpty
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long id;

        @NotNull
        private Long productId;

        private String productName;

        @NotNull
        @Positive
        private Integer quantity;

        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}