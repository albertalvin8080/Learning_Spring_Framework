package com.albert.orderservice.services;

import com.albert.core.dto.InventoryResponseDto;
import com.albert.core.dto.OrderDto;
import com.albert.core.dto.OrderLineItemsDto;
import com.albert.core.mappers.OrderMapper;
import com.albert.core.models.order.Order;
import com.albert.orderservice.event.OrderPlacedEvent;
import com.albert.orderservice.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService
{
    private final OrderRepository repository;
    private final OrderMapper orderMapper;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Value("${inventory.uri}")
    private String inventoryUri;

    public String placeOrder(OrderDto dto) {
        final List<String> skuCodeList = dto.getOrderLineItemsDtoList()
                .stream().map(OrderLineItemsDto::getSkuCode).toList();

        final InventoryResponseDto[] response = webClientBuilder.build().get()
                .uri(inventoryUri + "/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodeList).build()
                )
                .retrieve()
                .bodyToMono(InventoryResponseDto[].class)
                .block();

        log.info(Arrays.toString(response));

        final boolean allMatch = Arrays.stream(response)
                .allMatch(InventoryResponseDto::isHasInStock);

        if (!allMatch || response.length < skuCodeList.size())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Some of the products are out of stock.");

        final Order order = orderMapper.from(dto);
        repository.save(order);

        // send to kafka broker
//        final OrderPlacedEvent orderPlacedEvent = OrderPlacedEvent.builder()
//                .orderNumber(order.getOrderNumber()).build();
//        kafkaTemplate.send("notificationTopic", orderPlacedEvent);

        return "Order %s placed successfully.".formatted(order.getOrderNumber());
    }
}
