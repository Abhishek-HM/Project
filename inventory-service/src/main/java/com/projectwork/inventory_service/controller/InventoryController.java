package com.projectwork.inventory_service.controller;

import com.projectwork.inventory_service.dto.InventoryDTO;
import com.projectwork.inventory_service.model.Inventory;
import com.projectwork.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<String> createInventory(@RequestBody Inventory inventory) {
        inventoryService.createInventory(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created Successfully");
    }

    //http://localhost:8082/api/inventory?skuCode=iphone_13,iphone_13_red

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryDTO> isINStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }


}
