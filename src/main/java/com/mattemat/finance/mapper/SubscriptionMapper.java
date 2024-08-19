package com.mattemat.finance.mapper;

import com.mattemat.finance.dto.request.SubscriptionRequestDto;
import com.mattemat.finance.dto.response.SubscriptionResponseDto;
import com.mattemat.finance.entity.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SubscriptionMapper {

    public Subscription toEntity(SubscriptionRequestDto dto) {
        log.debug("Converting SubscriptionRequestDto to Subscription entity");
        Subscription subscription = new Subscription();
        updateEntityFromDto(dto, subscription);
        log.debug("SubscriptionRequestDto converted to Subscription entity");
        return subscription;
    }

    public void updateEntityFromDto(SubscriptionRequestDto dto, Subscription subscription) {
        log.debug("Updating Subscription entity from SubscriptionRequestDto");
        subscription.setName(dto.getName());
        subscription.setCurrency(dto.getFromCurrency());
        subscription.setAmount(dto.getAmount());
        subscription.setBillingCycle(dto.getBillingCycle());
        subscription.setSubscriptionDate(dto.getSubscriptionDate());
        subscription.setCreatedAt(dto.getCreatedAt());
        subscription.setUpdatedAt(dto.getUpdatedAt());
        log.debug("Subscription entity updated from SubscriptionRequestDto");
    }

    public SubscriptionResponseDto toDto(Subscription subscription) {
        log.debug("Converting Subscription entity to SubscriptionResponseDto");
        SubscriptionResponseDto dto = new SubscriptionResponseDto();
        dto.setId(subscription.getId());
        dto.setName(subscription.getName());
        dto.setCurrency(subscription.getCurrency());
        dto.setAmount(subscription.getAmount());
        dto.setConvertedAmount(subscription.getConvertedAmount());
        dto.setBillingCycle(subscription.getBillingCycle());
        dto.setSubscriptionDate(subscription.getSubscriptionDate());
        dto.setNextSubscriptionDate(subscription.getNextSubscriptionDate());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());
        log.debug("Subscription entity converted to SubscriptionResponseDto");
        return dto;
    }
}