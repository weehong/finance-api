package com.mattemat.finance.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mattemat.finance.dto.request.SubscriptionRequestDto;
import com.mattemat.finance.dto.response.SubscriptionResponseDto;
import com.mattemat.finance.entity.Subscription;
import com.mattemat.finance.mapper.SubscriptionMapper;
import com.mattemat.finance.repository.CurrencyRepository;
import com.mattemat.finance.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private SubscriptionMapper subscriptionMapper;
    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private List<Subscription> testSubscriptions;
    private List<SubscriptionResponseDto> testResponseDtos;
    private SubscriptionRequestDto testRequestDto;

    private static Stream<Arguments> provideEdgeCases() {
        return Stream.of(
                Arguments.of(Collections.singletonList(new Subscription()), 1),
                Arguments.of(Collections.emptyList(), 0),
                Arguments.of(List.of(new Subscription(), new Subscription(), new Subscription()), 3)
        );
    }

    @BeforeEach
    void setUp() throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        testSubscriptions = readValue("data/entity/subscriptions.json", new TypeReference<>() {
        });
        testResponseDtos = readValue("data/response/subscriptions.json", new TypeReference<>() {
        });
        testRequestDto = readValue("data/request/subscriptions.json", new TypeReference<>() {
        });
    }

    @Test
    void readAll_shouldReturnListOfSubscriptionResponseDtos() {
        when(subscriptionRepository.findAll()).thenReturn(testSubscriptions);

        IntStream.range(0, testSubscriptions.size())
                .forEach(i -> when(subscriptionMapper.toDto(testSubscriptions.get(i)))
                        .thenReturn(testResponseDtos.get(i)));

        List<SubscriptionResponseDto> actualDtos = subscriptionService.readAll();

        assertAll(
                "Subscription DTOs",
                () -> assertNotNull(actualDtos, "DTOs list should not be null"),
                () -> assertEquals(testResponseDtos.size(), actualDtos.size(), "Should return correct number of DTOs"),
                () -> IntStream.range(0, testResponseDtos.size()).forEach(i ->
                        assertSubscriptionDto(actualDtos.get(i), testResponseDtos.get(i)))
        );
        verify(subscriptionRepository).findAll();
        verify(subscriptionMapper, times(testSubscriptions.size())).toDto(any(Subscription.class));
    }

    @Test
    void readAll_shouldReturnEmptyList_whenNoSubscriptions() {
        when(subscriptionRepository.findAll()).thenReturn(Collections.emptyList());

        List<SubscriptionResponseDto> dtos = subscriptionService.readAll();

        assertAll(
                "Empty DTO list",
                () -> assertNotNull(dtos, "DTOs list should not be null"),
                () -> assertTrue(dtos.isEmpty(), "DTOs list should be empty")
        );
        verify(subscriptionRepository).findAll();
        verify(subscriptionMapper, never()).toDto(any(Subscription.class));
    }

    @ParameterizedTest
    @MethodSource("provideEdgeCases")
    void readAll_shouldHandleEdgeCases(List<Subscription> input, int expectedSize) {
        when(subscriptionRepository.findAll()).thenReturn(input);
        input.forEach(subscription -> when(subscriptionMapper.toDto(subscription)).thenReturn(new SubscriptionResponseDto()));

        List<SubscriptionResponseDto> dtos = subscriptionService.readAll();

        assertAll(
                "Edge case handling",
                () -> assertNotNull(dtos, "DTOs list should not be null"),
                () -> assertEquals(expectedSize, dtos.size(), "DTOs list size should match expected")
        );
        verify(subscriptionRepository).findAll();
        verify(subscriptionMapper, times(expectedSize)).toDto(any(Subscription.class));
    }

    @Test
    void create_shouldReturnSubscriptionResponseDto() {
        when(subscriptionMapper.toEntity(any(SubscriptionRequestDto.class))).thenReturn(testSubscriptions.getFirst());
        when(currencyRepository.findByCurrency(anyString())).thenReturn(BigDecimal.TEN);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscriptions.getFirst());
        when(subscriptionMapper.toDto(any(Subscription.class))).thenReturn(testResponseDtos.getFirst());

        SubscriptionResponseDto actualResponseDto = subscriptionService.create(testRequestDto);

        assertSubscriptionDto(actualResponseDto, testResponseDtos.getFirst());
        verify(subscriptionRepository).save(any(Subscription.class));
        verify(subscriptionMapper).toDto(testSubscriptions.getFirst());
    }

    @Test
    void read_shouldReturnSubscriptionResponseDto_whenSubscriptionExists() {
        Long id = 1L;
        when(subscriptionRepository.findById(anyLong())).thenReturn(Optional.of(testSubscriptions.getFirst()));
        when(subscriptionMapper.toDto(testSubscriptions.getFirst())).thenReturn(testResponseDtos.getFirst());

        SubscriptionResponseDto actualResponseDto = subscriptionService.read(id);

        assertSubscriptionDto(actualResponseDto, testResponseDtos.getFirst());
        verify(subscriptionRepository).findById(id);
        verify(subscriptionMapper).toDto(testSubscriptions.getFirst());
    }

    @Test
    void read_shouldReturnNull_whenSubscriptionDoesNotExist() {
        Long id = 1L;
        when(subscriptionRepository.findById(anyLong())).thenReturn(Optional.empty());

        SubscriptionResponseDto responseDto = subscriptionService.read(id);

        assertNull(responseDto, "Response DTO should be null for non-existent subscription");
        verify(subscriptionRepository).findById(id);
        verify(subscriptionMapper, never()).toDto(any(Subscription.class));
    }

    @Test
    void update_shouldUpdateSubscription_whenSubscriptionExists() {
        Long id = 1L;
        when(subscriptionRepository.findById(anyLong())).thenReturn(Optional.of(testSubscriptions.getFirst()));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscriptions.getFirst());
        doNothing().when(subscriptionMapper).updateEntityFromDto(any(SubscriptionRequestDto.class), any(Subscription.class));

        subscriptionService.update(id, testRequestDto);

        verify(subscriptionRepository).findById(id);
        verify(subscriptionRepository).save(argThat(subscription -> {
            assertEquals(id, subscription.getId(), "ID should not change");
            assertEquals(testRequestDto.getName(), subscription.getName(), "Name should be updated");
            assertEquals(testRequestDto.getFromCurrency(), subscription.getCurrency(), "Currency should be updated");
            assertEquals(testRequestDto.getAmount(), subscription.getAmount(), "Amount should be updated");
            assertEquals(testRequestDto.getBillingCycle(), subscription.getBillingCycle(), "Billing cycle should be updated");
            assertEquals(testRequestDto.getSubscriptionDate(), subscription.getSubscriptionDate(), "Subscription date should be updated");
            return true;
        }));
        verify(subscriptionMapper, never()).toDto(any(Subscription.class));
    }

    @Test
    void update_shouldUpdateSubscriptionAndConvertAmount_whenCurrencyChanges() {
        Long id = 1L;
        Subscription existingSubscription = testSubscriptions.getFirst();
        existingSubscription.setCurrency("USD");

        SubscriptionRequestDto updatedRequest = new SubscriptionRequestDto();
        updatedRequest.setFromCurrency("EUR");
        updatedRequest.setToCurrency("USD");
        updatedRequest.setAmount(new BigDecimal("100.00"));

        BigDecimal convertedAmount = new BigDecimal("100.00");

        when(subscriptionRepository.findById(anyLong())).thenReturn(Optional.of(existingSubscription));
        when(currencyRepository.findByCurrency(anyString())).thenReturn(BigDecimal.TEN);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(existingSubscription);
        doNothing().when(subscriptionMapper).updateEntityFromDto(any(SubscriptionRequestDto.class), any(Subscription.class));

        subscriptionService.update(id, updatedRequest);

        verify(subscriptionRepository).findById(id);
        verify(subscriptionRepository).save(argThat(subscription -> {
            assertEquals(id, subscription.getId(), "ID should not change");
            assertEquals("USD", subscription.getCurrency(), "Currency should be updated");
            assertEquals(convertedAmount, subscription.getConvertedAmount(), "Converted amount should be set");
            return true;
        }));
        verify(subscriptionMapper).updateEntityFromDto(eq(updatedRequest), any(Subscription.class));
    }

    @Test
    void update_shouldNotUpdateSubscription_whenSubscriptionDoesNotExist() {
        Long id = 1L;
        when(subscriptionRepository.findById(anyLong())).thenReturn(Optional.empty());

        subscriptionService.update(id, testRequestDto);

        verify(subscriptionRepository).findById(id);
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void delete_shouldDeleteSubscription() {
        Long id = 1L;

        subscriptionService.delete(id);

        verify(subscriptionRepository).deleteById(id);
    }

    private void assertSubscriptionDto(SubscriptionResponseDto actual, SubscriptionResponseDto expected) {
        assertAll(
                "Subscription DTO",
                () -> assertEquals(expected.getName(), actual.getName(), "Name should match"),
                () -> assertEquals(expected.getCurrency(), actual.getCurrency(), "Currency should match"),
                () -> assertEquals(expected.getAmount(), actual.getAmount(), "Amount should match"),
                () -> assertEquals(expected.getConvertedAmount(), actual.getConvertedAmount(), "Converted amount should match"),
                () -> assertEquals(testRequestDto.getBillingCycle(), actual.getBillingCycle(), "Billing cycle should be updated"),
                () -> assertEquals(testRequestDto.getSubscriptionDate(), actual.getSubscriptionDate(), "Subscription date should be updated"),
                () -> assertEquals(expected.getCreatedAt(), actual.getCreatedAt(), "Start date should match"),
                () -> assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt(), "End date should match")
        );
    }

    private <T> T readValue(String path, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(new ClassPathResource(path).getInputStream(), typeReference);
    }
}