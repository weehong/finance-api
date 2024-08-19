package com.mattemat.finance.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mattemat.finance.dto.request.SubscriptionRequestDto;
import com.mattemat.finance.dto.response.SubscriptionResponseDto;
import com.mattemat.finance.entity.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubscriptionMapperTest {

    private SubscriptionMapper subscriptionMapper;
    private ObjectMapper objectMapper;
    private List<Subscription> testSubscriptions;
    private List<SubscriptionResponseDto> testResponseDtos;
    private List<SubscriptionRequestDto> testRequestDtos;

    @BeforeEach
    void setUp() throws IOException {
        subscriptionMapper = new SubscriptionMapper();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        testSubscriptions = readValueAsList("data/entity/subscriptions.json", Subscription.class);
        testResponseDtos = readValueAsList("data/response/subscriptions.json", SubscriptionResponseDto.class);
        testRequestDtos = readValueAsList("data/request/subscriptions.json", SubscriptionRequestDto.class);
    }

    @Test
    void testToEntity() {
        for (int i = 0; i < testRequestDtos.size(); i++) {
            SubscriptionRequestDto dto = testRequestDtos.get(i);
            Subscription expectedEntity = testSubscriptions.get(i);

            Subscription actualEntity = subscriptionMapper.toEntity(dto);

            assertNotNull(actualEntity);
            assertEquals(expectedEntity.getName(), actualEntity.getName());
            assertEquals(expectedEntity.getCurrency(), actualEntity.getCurrency());
            assertEquals(expectedEntity.getAmount(), actualEntity.getAmount());
            assertEquals(expectedEntity.getSubscriptionDate(), actualEntity.getSubscriptionDate());
            assertEquals(expectedEntity.getBillingCycle(), actualEntity.getBillingCycle());
            assertEquals(expectedEntity.getCreatedAt(), actualEntity.getCreatedAt());
            assertEquals(expectedEntity.getUpdatedAt(), actualEntity.getUpdatedAt());
        }
    }

    @Test
    void testUpdateEntityFromDto() {
        for (SubscriptionRequestDto dto : testRequestDtos) {
            Subscription subscription = new Subscription();

            subscriptionMapper.updateEntityFromDto(dto, subscription);

            assertEquals(dto.getName(), subscription.getName());
            assertEquals(dto.getFromCurrency(), subscription.getCurrency());
            assertEquals(dto.getAmount(), subscription.getAmount());
            assertEquals(dto.getBillingCycle(), subscription.getBillingCycle());
            assertEquals(dto.getSubscriptionDate(), subscription.getSubscriptionDate());
            assertEquals(dto.getCreatedAt(), subscription.getCreatedAt());
            assertEquals(dto.getUpdatedAt(), subscription.getUpdatedAt());
        }
    }

    @Test
    void testToDto() {
        for (int i = 0; i < testSubscriptions.size(); i++) {
            Subscription subscription = testSubscriptions.get(i);
            SubscriptionResponseDto expectedDto = testResponseDtos.get(i);

            SubscriptionResponseDto actualDto = subscriptionMapper.toDto(subscription);

            assertNotNull(actualDto);
            assertEquals(expectedDto.getId(), actualDto.getId());
            assertEquals(expectedDto.getName(), actualDto.getName());
            assertEquals(expectedDto.getCurrency(), actualDto.getCurrency());
            assertEquals(expectedDto.getAmount(), actualDto.getAmount());
            assertEquals(expectedDto.getBillingCycle(), actualDto.getBillingCycle());
            assertEquals(expectedDto.getSubscriptionDate(), actualDto.getSubscriptionDate());
            assertEquals(expectedDto.getCreatedAt(), actualDto.getCreatedAt());
            assertEquals(expectedDto.getUpdatedAt(), actualDto.getUpdatedAt());
        }
    }

    private <T> List<T> readValueAsList(String filename, Class<T> clazz) throws IOException {
        try {
            return objectMapper.readValue(
                    getClass().getClassLoader().getResourceAsStream(filename),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (com.fasterxml.jackson.databind.exc.MismatchedInputException e) {
            // If it's not a list, try reading as a single object
            T singleObject = objectMapper.readValue(
                    getClass().getClassLoader().getResourceAsStream(filename),
                    clazz
            );
            return Collections.singletonList(singleObject);
        }
    }
}