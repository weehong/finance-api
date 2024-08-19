package com.mattemat.finance.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SubscriptionResponseDto {
    private Long id;
    private String name;
    private String currency;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
    private Integer billingCycle;
    private LocalDate subscriptionDate;
    private LocalDate nextSubscriptionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
