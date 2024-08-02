package com.projectwork.order_service.services;

import com.projectwork.order_service.dto.InventoryDTO;
import com.projectwork.order_service.dto.OrderLineItemsDTO;
import com.projectwork.order_service.dto.OrderRequest;
import com.projectwork.order_service.event.OrderPlacedEvent;
import com.projectwork.order_service.model.Order;
import com.projectwork.order_service.model.OrderLineItems;
import com.projectwork.order_service.repository.OrderRepository;
import com.projectwork.order_service.services.client.InventoryFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor  //create the constructor automatically based on parameter on compile time
@Transactional
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryFeignClient inventoryFeignClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest){
        Order order  = new Order();

        order.setOrderNumber(UUID.randomUUID().toString()); //Universally Unique Identifier 128 bits

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDTOList()
                                                    .stream()
                                                    .map(orderLineItemsDTO -> mapToDto(orderLineItemsDTO))
                                                    .toList();

        order.setOrderLineItemsList(orderLineItemsList);

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(orderLineItems -> orderLineItems.getSkuCode()).toList();


        List<InventoryDTO> inventoryStockList = inventoryFeignClient.isINStock(skuCodes);

        log.info(inventoryStockList.toString());

        List<String> noItemFoundInInventory = skuCodes.stream()
                        .filter(skuCode -> inventoryStockList.stream().noneMatch(item -> item.getSkuCode().equals(skuCode)))
                                .collect(Collectors.toList());

        log.info("These Item Are Not Found In Inventory "+ noItemFoundInInventory.toString());

        List<String> orderCanPlace = inventoryStockList.stream().map(item -> item.getSkuCode()).toList();

        boolean isStock = inventoryStockList.stream().allMatch(inventory -> inventory.isInStock());

        List<String> noStock = inventoryStockList.stream().filter(inventory -> !inventory.isInStock()).map(inventoryItems -> inventoryItems.getSkuCode()).toList();

        if(isStock){
            if(noItemFoundInInventory.isEmpty()) {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                return "Order Placed";
            }
            else
                return "Stock currently unavailable for one of the requested items....\n"+orderCanPlace+" Stock available in inventory,\n"+noItemFoundInInventory+" The requested item is unavailable. \n"+ "Please select a valid item from the list";
        }else {
            return noStock.toString();
        }
    }
    private boolean checkStock(InventoryDTO inventoryDTO){
        return false;
    }

    private OrderLineItems mapToDto(OrderLineItemsDTO orderLineItemsDTO) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDTO.getPrice());
        orderLineItems.setQuantity(orderLineItemsDTO.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDTO.getSkuCode());
        return orderLineItems;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
