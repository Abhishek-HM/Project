package com.projectwork.product_service.controller;

import com.projectwork.product_service.dto.ProductRequest;
import com.projectwork.product_service.dto.ProductResponse;
import com.projectwork.product_service.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void shouldCreateProductReturnProductSuccessfullyCreated() throws Exception {
        // given
        ProductRequest productRequest = new ProductRequest("Iphone", "Iphone-13", BigDecimal.valueOf(120));

        // when
        Mockito.doNothing().when(productService).createProduct(productRequest);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Product Successfully Created"));

        Mockito.verify(productService).createProduct(Mockito.any(ProductRequest.class));
    }

    @Test
    public void shouldReturnAllTheProduct() throws Exception {
        List<ProductResponse> productResponseList = List.of(
                new ProductResponse("1", "IPhone", "Iphone-13", BigDecimal.valueOf(120)),
                new ProductResponse("2", "Redmi", "Redmi note 9", BigDecimal.valueOf(100))
        );

        Mockito.when(productService.getAllProducts()).thenReturn(productResponseList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("IPhone"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Iphone-13"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(120))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Redmi"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Redmi note 9"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].price").value(100));
        Mockito.verify(productService).getAllProducts();
    }

    @Test
    public void shouldNoProductsFoundInProductService() throws Exception {
        List<ProductResponse> productResponseList = new ArrayList<>();
        Mockito.when(productService.getAllProducts()).thenReturn(productResponseList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No Products Found In Product Service."));
    }

}
























/*    @Test
    public void shouldReturnAllTheProduct() throws Exception {
        List<ProductResponse> productResponseList = List.of(
                new ProductResponse("1","IPhone","Iphone-13",BigDecimal.valueOf(120)),
                new ProductResponse("2","Redmi","Redmi note 9",BigDecimal.valueOf(100))
        );

        Mockito.when(productService.getAllProducts()).thenReturn(productResponseList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("IPhone"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Iphone-13"));
        Mockito.verify(productService).getAllProducts();
    }*/





/*
@Test
public void shouldCreateProductReturnProductSuccessfullyCreated() {
        Mockito.doNothing().when(productService).createProduct(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Product Successfully Created"));
        Mockito.verify(productService).createProduct(productRequest);
    }*/
/*    @Test
    public void shouldReturnAllProduct() throws Exception {
        // given
        List<ProductResponse> products = List.of(
                new ProductResponse("1","IPhone","Iphone-13",BigDecimal.valueOf(120)),
                new ProductResponse("2","Redmi","Redmi note 9",BigDecimal.valueOf(100))
        );

        // when
        Mockito.when(productController.getAllProducts()).thenReturn(products);

        // then
        List<ProductResponse> response = productController.getAllProducts();
        assertEquals(products, response);

    }
}*/
