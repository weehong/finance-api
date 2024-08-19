package com.mattemat.finance.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SubscriptionRequestDto {
    private String name;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal amount;
    private Integer billingCycle;
    private LocalDate subscriptionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
