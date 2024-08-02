package com.projectwork.order_service.services.client;

import com.projectwork.order_service.dto.InventoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryFeignClient {
    @GetMapping(value = "/api/inventory")
    public List<InventoryDTO> isINStock(@RequestParam List<String> skuCode);
}

// For Local
// FeignClient(name = "inventory-service", url = "http://localhost:8082")