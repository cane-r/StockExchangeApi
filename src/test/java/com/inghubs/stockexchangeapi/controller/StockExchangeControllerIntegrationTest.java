package com.inghubs.stockexchangeapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.stockexchangeapi.models.dto.CreateStockExchangeDto;
import com.inghubs.stockexchangeapi.repository.StockExchangeJpaRepository;
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

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StockExchangeControllerIntegrationTest {

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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(applicationContext.getBean("stockExchangeController")).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    public void testGetStockExchangeWithStocks() throws Exception {
        mockMvc.perform(get("/api/v1/stock-exchange/BIST30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks.length()", greaterThan(0)))
                .andExpect(jsonPath("$.name").value("BIST30"))
                .andDo(print())
        ;
    }

    @Test
    public void testGetStockwithNonExistentExchange() throws Exception {
        mockMvc.perform(get("/api/v1/stock-exchange/BIST")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
        ;
    }
    @Order(1)
    @Test
    public void testAddStockToExchange() throws Exception {
        mockMvc.perform(post("/api/v1/stock-exchange/BIST30/6")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(handler -> {
                    await().atMost(Duration.ofSeconds(15))
                            .pollInterval(Duration.ofSeconds(1))
                            .untilAsserted(() -> {
                        mockMvc.perform(get("/api/v1/stock-exchange/BIST30/"))
                                .andExpect(status().isOk()).andExpect(jsonPath("$.liveInMarket").value("true"))
                                .andDo(print());
                    });

                });
        ;
    }

    @Test
    public void testRemoveStockFromExchange() throws Exception {
        mockMvc.perform(delete("/api/v1/stock-exchange/BIST30/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
        ;
    }

    @Test
    public void testCreateStockExchange() throws Exception {

        CreateStockExchangeDto dto = CreateStockExchangeDto.builder()
                .description("A stock exchange")
                .name("Sample")
                .build();
        String serialized = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/stock-exchange/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serialized))
                .andExpect(status().isCreated())
                .andDo(print())
        ;
    }
}
