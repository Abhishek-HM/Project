package com.projectwork.order_service.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectwork.order_service.dto.OrderLineItemsDTO;
import com.projectwork.order_service.dto.OrderRequest;
import com.projectwork.order_service.model.Order;
import com.projectwork.order_service.model.OrderLineItems;
import com.projectwork.order_service.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void shouldReturnOrderPlacedSuccessfully() throws Exception {
        // given
        OrderLineItemsDTO orderLineItemsDTO = new OrderLineItemsDTO();
        orderLineItemsDTO.setSkuCode("Iphone");
        orderLineItemsDTO.setPrice(BigDecimal.valueOf(120));
        orderLineItemsDTO.setQuantity(1);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderLineItemsDTOList(List.of(orderLineItemsDTO));

        // when
        Mockito.when(orderService.placeOrder(any(OrderRequest.class))).thenReturn("Order Placed");

        String jsonContent = objectMapper.writeValueAsString(orderRequest);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Order Placed Successfully"));
    }
    @Test
    public void shouldReturnAllOrders() throws Exception {
        // given
        OrderLineItems orderLineItem = new OrderLineItems(352L, "iphone_13", BigDecimal.valueOf(100.00), 12);
        Order order1 = new Order(352L, "b377f7df-31da-4829-917c-13664e4538b6", List.of(orderLineItem));

        OrderLineItems orderLineItem2 = new OrderLineItems(302L, "iphone_13", BigDecimal.valueOf(100.00), 12);
        Order order2 = new Order(302L, "142d2a69-de59-492f-bb3d-5d07b2eb8c5f", List.of(orderLineItem));

        List<Order> orders = List.of(order1, order2);

        Mockito.when(orderService.getAllOrders()).thenReturn(orders);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/order")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(orders)));
    }

}
