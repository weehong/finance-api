package com.mattemat.finance.controller;

import com.mattemat.finance.dto.request.SubscriptionRequestDto;
import com.mattemat.finance.dto.response.SubscriptionResponseDto;
import com.mattemat.finance.service.SubscriptionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionServiceImpl subscriptionService;

    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDto>> getSubscriptions() {
        log.info("SubscriptionController - getSubscriptions() called");
        var subscriptions = subscriptionService.readAll();

        log.info("SubscriptionController - getSubscriptions() returned {} subscriptions", subscriptions.size());
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDto> getSubscription(@PathVariable Long id) {
        log.info("SubscriptionController - getSubscription() called with id {}", id);
        var subscription = subscriptionService.read(id);

        if (subscription == null) {
            log.warn("getSubscription() found no subscription with id {}", id);
            return ResponseEntity.notFound().build();
        }

        log.info("SubscriptionController - getSubscription() returned subscription with id {}", subscription.getId());
        return ResponseEntity.ok(subscription);
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponseDto> createSubscription(@RequestBody SubscriptionRequestDto request) {
        log.info("SubscriptionController - createSubscription() called");
        SubscriptionResponseDto subscription = subscriptionService.create(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(subscription.getId())
                .toUri();

        log.info("SubscriptionController - createSubscription() created record successfully with id: {}", subscription.getId());
        return ResponseEntity.created(location).body(subscription);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubscription(
            @PathVariable Long id, @RequestBody SubscriptionRequestDto request) {
        log.info("SubscriptionController - updateSubscription() called with id {}", id);
        subscriptionService.update(id, request);

        log.info("SubscriptionController - updateSubscription() updated record with id {}", id);
        var subscription = subscriptionService.read(id);

        if (subscription == null) {
            log.warn("updateSubscription() found no subscription with id {}", id);
            return ResponseEntity.notFound().build();
        }

        log.info("SubscriptionController - updateSubscription() returned updated subscription with id {}", subscription.getId());
        return ResponseEntity.ok(subscription);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscription(@PathVariable Long id) {
        log.info("SubscriptionController - deleteSubscription() called with id {}", id);
        subscriptionService.delete(id);

        log.info("SubscriptionController - deleteSubscription() deleted record with id {}", id);
        if (subscriptionService.read(id) != null) {
            log.warn("deleteSubscription() found no subscription with id {}", id);
            return ResponseEntity.badRequest().build();
        }

        log.info("SubscriptionController - deleteSubscription() successfully deleted record with id {}", id);
        return ResponseEntity.noContent().build();
    }
}
