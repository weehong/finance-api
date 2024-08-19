package com.mattemat.finance.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mattemat.finance.dto.request.SubscriptionRequestDto;
import com.mattemat.finance.dto.response.SubscriptionResponseDto;
import com.mattemat.finance.service.SubscriptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubscriptionServiceImpl subscriptionService;

    private List<SubscriptionResponseDto> subscriptionResponseDtos;
    private SubscriptionRequestDto subscriptionRequestDto;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        subscriptionResponseDtos = readValue("data/response/subscriptions.json", new TypeReference<>() {});
        subscriptionRequestDto = readValue("data/request/subscriptions.json", new TypeReference<>() {});
    }

    @Test
    void getSubscriptions_shouldReturnListOfSubscriptionResponseDtos() throws Exception {
        when(subscriptionService.readAll()).thenReturn(subscriptionResponseDtos);

        mockMvc.perform(get("/api/v1/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(subscriptionResponseDtos.size()))
                .andExpect(jsonPath("$[0].id").value(subscriptionResponseDtos.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(subscriptionResponseDtos.get(0).getName()));
    }

    @Test
    void getSubscription_withValidId_shouldReturnSubscriptionResponseDto() throws Exception {
        Long id = 1L;
        SubscriptionResponseDto expectedDto = subscriptionResponseDtos.get(0);
        when(subscriptionService.read(id)).thenReturn(expectedDto);

        mockMvc.perform(get("/api/v1/subscriptions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedDto.getName()));
    }

    @Test
    void getSubscription_withInvalidId_shouldReturnNotFound() throws Exception {
        Long id = 999L;
        when(subscriptionService.read(id)).thenReturn(null);

        mockMvc.perform(get("/api/v1/subscriptions/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSubscription_shouldReturnCreatedSubscriptionResponseDto() throws Exception {
        SubscriptionResponseDto createdDto = subscriptionResponseDtos.get(0);
        when(subscriptionService.create(any(SubscriptionRequestDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/v1/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createdDto.getId()))
                .andExpect(jsonPath("$.name").value(createdDto.getName()))
                .andExpect(header().string("Location", "http://localhost/api/v1/subscriptions/" + createdDto.getId()));
    }

    @Test
    void updateSubscription_withValidId_shouldReturnUpdatedSubscriptionResponseDto() throws Exception {
        Long id = 1L;
        SubscriptionResponseDto updatedDto = subscriptionResponseDtos.get(0);
        when(subscriptionService.read(id)).thenReturn(updatedDto);

        mockMvc.perform(put("/api/v1/subscriptions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(updatedDto.getId()))
                .andExpect(jsonPath("$.name").value(updatedDto.getName()));
    }

    @Test
    void updateSubscription_withValidIdButNullResult_shouldReturnNotFound() throws Exception {
        Long id = 1L;
        doNothing().when(subscriptionService).update(eq(id), any(SubscriptionRequestDto.class));
        when(subscriptionService.read(id)).thenReturn(null);

        mockMvc.perform(put("/api/v1/subscriptions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSubscription_withValidId_shouldReturnNoContent() throws Exception {
        Long id = 1L;
        when(subscriptionService.read(id)).thenReturn(null);

        mockMvc.perform(delete("/api/v1/subscriptions/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSubscription_withInvalidId_shouldReturnBadRequest() throws Exception {
        Long id = 999L;
        when(subscriptionService.read(id)).thenReturn(subscriptionResponseDtos.get(0));

        mockMvc.perform(delete("/api/v1/subscriptions/{id}", id))
                .andExpect(status().isBadRequest());
    }

    private <T> T readValue(String path, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(new ClassPathResource(path).getInputStream(), typeReference);
    }
}