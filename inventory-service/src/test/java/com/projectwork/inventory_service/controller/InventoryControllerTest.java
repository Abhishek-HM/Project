package com.projectwork.inventory_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectwork.inventory_service.dto.InventoryDTO;
import com.projectwork.inventory_service.model.Inventory;
import com.projectwork.inventory_service.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private InventoryController inventoryController;

    @Mock
    private InventoryService inventoryService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void shouldCreateInventoryReturnCreatedSuccessfully() throws Exception {
        // given
        Inventory inventory = new Inventory(1L, "s123cgtu54e", 100);

        // when
        Mockito.when(inventoryService.createInventory(inventory)).thenReturn(inventory);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventory)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Created Successfully"));

        Mockito.verify(inventoryService).createInventory(inventory);
    }

    @Test
    public void shouldReturnTrueWhichAreThereInInventory() throws Exception {
        // given
        List<String> skuCodes = List.of("Iphone", "Realme");
        List<InventoryDTO> inventoryDTOS = List.of(
                new InventoryDTO("Iphone",true),
                new InventoryDTO("Realme",false)
        );

        // when
        Mockito.when(inventoryService.isInStock(skuCodes)).thenReturn(inventoryDTOS);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory")
                        .param("skuCode", "Iphone", "Realme")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].skuCode").value("Iphone"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].inStock").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].skuCode").value("Realme"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].inStock").value(false));

        Mockito.verify(inventoryService).isInStock(skuCodes);
    }

}