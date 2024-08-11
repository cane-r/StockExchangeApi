package com.inghubs.stockexchangeapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.stockexchangeapi.models.dto.CreateStockDto;
import com.inghubs.stockexchangeapi.models.dto.UpdateStockDto;
import com.inghubs.stockexchangeapi.repository.StockExchangeJpaRepository;
import com.inghubs.stockexchangeapi.rest.controller.StockController;
import com.inghubs.stockexchangeapi.rest.controller.error.GlobalExceptionHandler;
import com.inghubs.stockexchangeapi.service.StockExchangeService;
import com.inghubs.stockexchangeapi.service.StockService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StockControllerIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private StockExchangeService service;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockExchangeJpaRepository exchangeJpaRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private StockController stockController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(applicationContext.getBean("stockController")).setControllerAdvice(new GlobalExceptionHandler()).build();
    }
    @Test
    public void testInValidCreateStock() throws Exception {

        CreateStockDto dto = CreateStockDto.builder()
                .description("Sample")
                //name required
                .name("")
                .price(BigDecimal.ONE)
                .build();

        String serialized = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/stock/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialized)
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
        ;
    }

    @Test
    public void testValidCreateStock() throws Exception {

        CreateStockDto dto = CreateStockDto.builder()
                .description("Sample")
                .name("Sample")
                .price(BigDecimal.ONE)
                .build();

        String serialized = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/stock/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialized)
                )
                .andExpect(status().isCreated())
                .andDo(print())
                ;
    }

    @Test
    @Order(1)
    public void testValidUpdateStockPrice() throws Exception {

        UpdateStockDto dto = new UpdateStockDto(BigDecimal.valueOf(86));


        String serialized = mapper.writeValueAsString(dto);

        mockMvc.perform(patch("/api/v1/stock/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialized)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(handler -> { mockMvc.perform(get("/api/v1/stock/1"))
                        .andExpect(status().isOk()).andExpect(jsonPath("$.price").value(dto.getNewPrice().doubleValue()))
                        .andDo(print());
                });
    }

    @Test
    public void removeStockTest () throws Exception {
        mockMvc.perform(delete("/api/v1/stock/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(handler -> { mockMvc.perform(get("/api/v1/stock/1"))
                        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Stock with id 1 not found on this server!"))
                        .andDo(print());
                });
    }

    @Test
    public void testExistentStockCreationTest() throws Exception {

        CreateStockDto dto = CreateStockDto.builder()
                .description("Sample1")
                .name("Sample1")
                .price(BigDecimal.ONE)
                .build();

        String serialized = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/stock/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialized)
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(handler -> { mockMvc.perform(post("/api/v1/stock/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(serialized))
                        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Stock name is unique.So you can't add the same stock twice."))
                        .andDo(print());
                });
        ;
    }
    @Test
    public void generalErrorTest () throws Exception {
        mockMvc.perform(get("/api/v1/stockk/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                ;
    }

    @Test
    public void testHandleValidationError () throws Exception {
        CreateStockDto dto = CreateStockDto.builder()
                .description(null)
                .name(null)
                .price(null)
                .build();
        String serialized = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/stock/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialized)
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
        ;
    }
}
