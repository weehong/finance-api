package com.mattemat.finance.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SubscriptionTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void testOnCreate() {
        // Given
        Subscription subscription = createSubscription();

        // When
        Subscription savedSubscription = testEntityManager.persistAndFlush(subscription);

        // Then
        assertNotNull(savedSubscription.getId());
        assertNotNull(savedSubscription.getCreatedAt());
        assertNotNull(savedSubscription.getUpdatedAt());
        assertTrue(isWithinMillisecond(savedSubscription.getCreatedAt(), savedSubscription.getUpdatedAt()));
        assertTrue(isWithinLastSecond(savedSubscription.getCreatedAt()));
    }

    @Test
    void testOnUpdate() {
        // Given
        Subscription subscription = createSubscription();
        Subscription savedSubscription = testEntityManager.persistAndFlush(subscription);
        testEntityManager.detach(savedSubscription);

        LocalDateTime originalUpdatedAt = savedSubscription.getUpdatedAt();

        // When
        savedSubscription.setAmount(new BigDecimal("10.99"));
        Subscription updatedSubscription = testEntityManager.merge(savedSubscription);
        testEntityManager.flush();

        // Then
        assertNotNull(updatedSubscription.getId());
        assertTrue(updatedSubscription.getUpdatedAt().isAfter(originalUpdatedAt));
        assertTrue(isWithinLastSecond(updatedSubscription.getUpdatedAt()));
    }

    private Subscription createSubscription() {
        Subscription subscription = new Subscription();
        subscription.setName("Netflix");
        subscription.setCurrency("USD");
        subscription.setAmount(new BigDecimal("9.99"));
        subscription.setConvertedAmount(new BigDecimal("9.99"));
        subscription.setBillingCycle(12);
        subscription.setSubscriptionDate(LocalDate.now());
        subscription.setNextSubscriptionDate(LocalDate.now().plusMonths(12));
        return subscription;
    }

    private boolean isWithinLastSecond(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long diff = ChronoUnit.MILLIS.between(dateTime, now);
        return diff >= 0 && diff < 1000;
    }

    private boolean isWithinMillisecond(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        long diff = ChronoUnit.MILLIS.between(dateTime1, dateTime2);
        return Math.abs(diff) < 1;
    }
}