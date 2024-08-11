package com.inghubs.stockexchangeapi.service;

import com.inghubs.stockexchangeapi.models.dto.CreateStockExchangeDto;
import com.inghubs.stockexchangeapi.models.entity.Stock;
import com.inghubs.stockexchangeapi.models.entity.StockExchange;
import com.inghubs.stockexchangeapi.models.exception.StockException;
import com.inghubs.stockexchangeapi.repository.StockExchangeJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockExchangeServiceTest {

    @Mock
    private StockExchangeJpaRepository stockExchangeRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private StockService stockService;

    @InjectMocks
    private StockExchangeService stockExchangeService;
    private StockExchange value;

    @Test
    public void givenValidDto_thenExchangeCreated(){
        CreateStockExchangeDto dto = CreateStockExchangeDto.builder()
                .name("BIST")
                .description("BIST")
                .build();

        StockExchange exchange = new StockExchange();

        exchange.setName(dto.getName());
        exchange.setDescription(dto.getDescription());
        exchange.setId(1L);

        ArgumentCaptor<StockExchange> argumentCaptor = ArgumentCaptor.forClass(StockExchange.class);
        when(stockExchangeRepository.save(any(StockExchange.class))).thenReturn(exchange);

        StockExchange exchange1 = stockExchangeService.creteStockExchange(dto);

        verify(stockExchangeRepository,times(1)).save(argumentCaptor.capture());

        verifyNoMoreInteractions(stockExchangeRepository);
        assertThat(exchange1).isNotNull();

        StockExchange captured = argumentCaptor.getValue();

        assertThat(captured.getName()).isEqualTo(dto.getName());
        assertThat(exchange1.getName()).isEqualTo(captured.getName());
    }

    @Test
    public void givenValidStockAndExchange_thenStockAddedToExchange(){

        Stock existing = new Stock();
        existing.setPrice(BigDecimal.ONE);
        existing.setId(1L);
        existing.setName("Name");

        StockExchange exchange = new StockExchange();
        exchange.setName("Exchange");
        exchange.setDescription("Exchange");
        exchange.setId(1L);

        StockExchange exchange2 = new StockExchange();

        exchange2.setName(exchange.getName());
        exchange2.setDescription(exchange.getDescription());
        exchange2.setId(exchange.getId());
        exchange2.addStock(existing);



        when(stockExchangeRepository.findByName(anyString())).thenReturn(Optional.of(exchange));
        when(stockService.getStockById(existing.getId())).thenReturn(existing);
        when(stockExchangeRepository.save(any(StockExchange.class))).thenReturn(exchange2);

        StockExchange res = stockExchangeService.addStockToStockExchange(exchange.getName(),existing.getId());

        assertThat(res).isNotNull();

        // or get the passed object via ArgumentCaptor and prove passed+1 = result
        assertThat(res.getStocks().size()).isGreaterThan(0);

        verify(stockExchangeRepository,times(1)).findByName(exchange.getName());

        verify(stockService,times(1)).getStockById(existing.getId());

        verify(stockExchangeRepository,times(1)).save(any(StockExchange.class));

        verifyNoMoreInteractions(stockExchangeRepository);

        verifyNoMoreInteractions(stockService);
    }

    @Test
    public void givenNonExistentStock_thenStockNotAddedToExchange(){
        Stock existing = new Stock();
        existing.setPrice(BigDecimal.ONE);
        existing.setId(1L);
        existing.setName("Name");

        StockExchange exchange = new StockExchange();
        exchange.setName("Exchange");
        exchange.setDescription("Exchange");
        exchange.setId(1L);

        StockExchange exchange2 = new StockExchange();

        exchange2.setName(exchange.getName());
        exchange2.setDescription(exchange.getDescription());
        exchange2.setId(exchange.getId());
        exchange2.addStock(existing);

        when(stockService.getStockById(existing.getId())).thenReturn(null);

        assertThrows(StockException.class,() -> stockExchangeService.addStockToStockExchange(exchange.getName(),existing.getId()) );
        verifyNoInteractions(stockExchangeRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    public void givenNonExistentExchange_thenStockNotAddedToExchange(){
        Stock existing = new Stock();
        existing.setPrice(BigDecimal.ONE);
        existing.setId(1L);
        existing.setName("Name");

        StockExchange exchange = new StockExchange();
        exchange.setName("Exchange");
        exchange.setDescription("Exchange");
        exchange.setId(1L);

        lenient().doThrow(new StockException("")).when(stockExchangeRepository).findByName(anyString());
        assertThrows(StockException.class,() -> stockExchangeService.addStockToStockExchange(exchange.getName(),existing.getId()) );
        verifyNoInteractions(stockExchangeRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    public void givenExistingStock_thenStockIsNotAddedToExchange(){

        Stock existing = new Stock();
        existing.setPrice(BigDecimal.ONE);
        existing.setId(1L);
        existing.setName("Name");

        StockExchange exchange = new StockExchange();
        exchange.setName("Exchange");
        exchange.setDescription("Exchange");
        exchange.setId(1L);

        StockExchange exchange2 = new StockExchange();

        exchange2.setName(exchange.getName());
        exchange2.setDescription(exchange.getDescription());
        exchange2.setId(exchange.getId());
        exchange2.addStock(existing);

        when(stockExchangeRepository.findByName(anyString())).thenReturn(Optional.of(exchange2));
        when(stockService.getStockById(existing.getId())).thenReturn(existing);

        assertThrows(StockException.class,() -> stockExchangeService.addStockToStockExchange(exchange.getName(),existing.getId()) );

        verify(stockExchangeRepository,times(1)).findByName(exchange.getName());

        verify(stockService,times(1)).getStockById(existing.getId());

        verify(stockExchangeRepository,times(0)).save(any(StockExchange.class));

        verifyNoMoreInteractions(stockExchangeRepository);

        verifyNoMoreInteractions(stockService);

        verifyNoInteractions(eventPublisher);
    }

    @Test
    public void givenValidStockAndExchange_thenStockRemovedFromExchange(){

        Stock existing = new Stock();
        existing.setPrice(BigDecimal.ONE);
        existing.setId(1L);
        existing.setName("Name");

        StockExchange exchange = new StockExchange();
        exchange.setName("Exchange");
        exchange.setDescription("Exchange");
        exchange.setId(1L);

        StockExchange exchange2 = new StockExchange();

        exchange2.setName(exchange.getName());
        exchange2.setDescription(exchange.getDescription());
        exchange2.setId(exchange.getId());
        exchange2.addStock(existing);



        when(stockExchangeRepository.findByName(anyString())).thenReturn(Optional.of(exchange2));
        when(stockService.getStockById(existing.getId())).thenReturn(existing);
        when(stockExchangeRepository.save(any(StockExchange.class))).thenReturn(exchange);

        StockExchange res = stockExchangeService.removeStockFromStockExchange(exchange.getName(),existing.getId());

        assertThat(res).isNotNull();

        // or get the passed object via ArgumentCaptor and prove passed+1 = result
        assertThat(res.getStocks().size()).isEqualTo(0);

        verify(stockExchangeRepository,times(1)).findByName(exchange.getName());

        verify(stockService,times(1)).getStockById(existing.getId());

        verify(stockExchangeRepository,times(1)).save(any(StockExchange.class));

        verifyNoMoreInteractions(stockExchangeRepository);

        verifyNoMoreInteractions(stockService);
    }

    @Test
    public void givenNonExistentStock_thenStockNotRemovedFromExchange(){
        Stock existing = new Stock();
        existing.setPrice(BigDecimal.ONE);
        existing.setId(1L);
        existing.setName("Name");

        StockExchange exchange = new StockExchange();
        exchange.setName("Exchange");
        exchange.setDescription("Exchange");
        exchange.setId(1L);

        when(stockService.getStockById(existing.getId())).thenReturn(null);

        assertThrows(StockException.class,() -> stockExchangeService.removeStockFromStockExchange(exchange.getName(),existing.getId()) );
        verifyNoInteractions(stockExchangeRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    public void givenNonExistingStock_thenStockIsNotRemovedExchange(){

        Stock existing = new Stock();
        existing.setPrice(BigDecimal.ONE);
        existing.setId(1L);
        existing.setName("Name");

        StockExchange exchange = new StockExchange();
        exchange.setName("Exchange");
        exchange.setDescription("Exchange");
        exchange.setId(1L);

        StockExchange exchange2 = new StockExchange();

        exchange2.setName(exchange.getName());
        exchange2.setDescription(exchange.getDescription());
        exchange2.setId(exchange.getId());

        when(stockExchangeRepository.findByName(anyString())).thenReturn(Optional.of(exchange2));
        when(stockService.getStockById(existing.getId())).thenReturn(existing);

        assertThrows(StockException.class,() -> stockExchangeService.removeStockFromStockExchange(exchange.getName(),existing.getId()) );

        verify(stockExchangeRepository,times(1)).findByName(exchange.getName());

        verify(stockService,times(1)).getStockById(existing.getId());

        verify(stockExchangeRepository,times(0)).save(any(StockExchange.class));

        verifyNoMoreInteractions(stockExchangeRepository);

        verifyNoMoreInteractions(stockService);

        verifyNoInteractions(eventPublisher);
    }
}
