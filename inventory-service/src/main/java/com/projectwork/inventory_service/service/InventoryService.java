package com.projectwork.inventory_service.service;

import com.projectwork.inventory_service.dto.InventoryDTO;
import com.projectwork.inventory_service.model.Inventory;
import com.projectwork.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public Inventory createInventory(Inventory inventory){
       return inventoryRepository.save(inventory);
    }

    @Transactional(readOnly = true)
    //@SneakyThrows
    public  List<InventoryDTO> isInStock(List<String> skuCode) {
        /*// for lock thread TimeLimiter, we are making this wait see the TimeLimiter
        log.info("Wait Started");
        Thread.sleep(10000);
        log.info("Wait Ended");*/
        return inventoryRepository.findBySkuCodeIn(skuCode)
               .stream()
               .map(inventory ->
                   InventoryDTO.builder()
                           .skuCode(inventory.getSkuCode())
                           .isInStock(inventory.getQuantity() > 0)
                           .build()
               ).toList();
    }
}
