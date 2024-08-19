package com.mattemat.finance.service;

import com.mattemat.finance.dto.request.SubscriptionRequestDto;
import com.mattemat.finance.dto.response.SubscriptionResponseDto;
import com.mattemat.finance.entity.Subscription;
import com.mattemat.finance.mapper.SubscriptionMapper;
import com.mattemat.finance.repository.CurrencyRepository;
import com.mattemat.finance.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SubscriptionServiceImpl
        implements GenericCrudService<SubscriptionRequestDto, SubscriptionResponseDto> {

    private final CurrencyRepository currencyRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Autowired
    public SubscriptionServiceImpl(
            CurrencyRepository currencyRepository,
            SubscriptionRepository subscriptionRepository,
            SubscriptionMapper subscriptionMapper) {
        this.currencyRepository = currencyRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionMapper = subscriptionMapper;
        log.info("SubscriptionService - SubscriptionServiceImpl initialized");
    }

    @Override
    public SubscriptionResponseDto create(SubscriptionRequestDto subscriptionRequestDto) {
        log.info("SubscriptionService - Creating new subscription");
        Subscription subscription = subscriptionMapper.toEntity(subscriptionRequestDto);
        subscription.setConvertedAmount(convertedAmount(subscriptionRequestDto.getAmount(), subscriptionRequestDto.getFromCurrency(), subscriptionRequestDto.getToCurrency()));
        subscription.setNextSubscriptionDate(
                subscriptionRequestDto.getSubscriptionDate().plusMonths(subscriptionRequestDto.getBillingCycle()));
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("SubscriptionService - Subscription created with ID: {}", savedSubscription.getId());
        return subscriptionMapper.toDto(savedSubscription);
    }

    @Override
    public SubscriptionResponseDto read(Long id) {
        log.info("SubscriptionService - Reading subscription with ID: {}", id);
        return subscriptionRepository.findById(id)
                .map(subscription -> {
                    log.info("SubscriptionService - Subscription found with ID: {}", id);
                    return subscriptionMapper.toDto(subscription);
                })
                .orElseGet(() -> {
                    log.warn("Subscription not found with ID: {}", id);
                    return null;
                });
    }

    @Override
    public List<SubscriptionResponseDto> readAll() {
        log.info("SubscriptionService - Reading all subscriptions");
        List<SubscriptionResponseDto> subscriptions = subscriptionRepository.findAll()
                .stream()
                .map(subscriptionMapper::toDto)
                .toList();
        log.info("SubscriptionService - Found {} subscriptions", subscriptions.size());
        return subscriptions;
    }

    @Override
    public void update(Long id, SubscriptionRequestDto request) {
        log.info("SubscriptionService - Updating subscription with ID: {}", id);
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(id);
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();

            if (!request.getFromCurrency().equalsIgnoreCase(subscriptionOpt.get().getCurrency())) {
                BigDecimal convertedAmount = convertedAmount(
                        request.getAmount(),
                        request.getFromCurrency(),
                        request.getToCurrency());
                log.info("SubscriptionService - Updating converted amount for ID: {}", id);
                subscription.setConvertedAmount(convertedAmount);
            }

            subscriptionMapper.updateEntityFromDto(request, subscription);
            subscriptionRepository.save(subscription);
            log.info("SubscriptionService - Subscription updated successfully with ID: {}", id);
        } else {
            log.warn("Subscription not found for update with ID: {}", id);
        }
    }

    @Override
    public void delete(Long id) {
        log.info("SubscriptionService - Deleting subscription with ID: {}", id);
        subscriptionRepository.deleteById(id);
        log.info("SubscriptionService - Subscription deleted with ID: {}", id);
    }

    private BigDecimal convertedAmount(BigDecimal amount, String fromCurrency, String toCurrency) {
        BigDecimal fromCurrencyRate = currencyRepository.findByCurrency(fromCurrency);
        BigDecimal toCurrencyRate = currencyRepository.findByCurrency(toCurrency);
        BigDecimal amountInBaseCurrency = amount.divide(fromCurrencyRate, RoundingMode.HALF_UP);
        return amountInBaseCurrency.multiply(toCurrencyRate);
    }
}