package com.inghubs.stockexchangeapi.service;

import com.inghubs.stockexchangeapi.models.dto.CreateStockDto;
import com.inghubs.stockexchangeapi.models.dto.UpdateStockDto;
import com.inghubs.stockexchangeapi.models.entity.Stock;
import com.inghubs.stockexchangeapi.models.exception.StockException;
import com.inghubs.stockexchangeapi.repository.StockJpaRepository;
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
public class StockServiceTest {
    @Mock
    private StockJpaRepository stockRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private StockService stockService;

    @Test
    public void givenValidDto_thenStockCreated(){
        CreateStockDto dto = CreateStockDto.builder()
                .price(BigDecimal.valueOf(12.3))
                .name("Sample")
                .description("Sample")
                .build();

        Stock entity = new Stock();

        entity.setPrice(dto.getPrice());
        entity.setId(1L);
        entity.setDescription(dto.getDescription());
        entity.setName(dto.getName());

        ArgumentCaptor<Stock> argumentCaptor = ArgumentCaptor.forClass(Stock.class);

        when(stockRepository.save(any(Stock.class))).thenReturn(entity);

        Stock stock = stockService.createStock(dto);

        verify(stockRepository,times(1)).save(argumentCaptor.capture());

        assertThat(stock).isNotNull();

        Stock captured = argumentCaptor.getValue();

        assertThat(captured.getName()).isEqualTo(dto.getName());
        assertThat(stock.getName()).isEqualTo(captured.getName());
    }

    @Test
    public void givenValidDto_thenStockUpdated(){
        UpdateStockDto dto = new UpdateStockDto(BigDecimal.TEN);

        Stock existing = new Stock();

        existing.setPrice(BigDecimal.ONE);
        existing.setId(1L);
        existing.setName("Name");

        Stock saved = new Stock();

        saved.setPrice(BigDecimal.TEN);
        saved.setId(existing.getId());
        saved.setName(existing.getName());

        when(stockRepository.findById(anyLong())).thenReturn(Optional.of(existing));
        when(stockRepository.save(any(Stock.class))).thenReturn(saved);

        Stock stock = stockService.updateStockPrice(dto,existing.getId());

        ArgumentCaptor<Stock> argumentCaptor = ArgumentCaptor.forClass(Stock.class);

        verify(stockRepository,times(1)).findById(existing.getId());
        verify(stockRepository,times(1)).save(argumentCaptor.capture());

        Stock captured = argumentCaptor.getValue();

        assertThat(captured.getPrice()).isEqualTo(stock.getPrice());
        assertThat(stock.getPrice()).isEqualTo(dto.getNewPrice());
    }

    @Test
    public void givenNonExistentStock_thenStockNotUpdated(){
        UpdateStockDto dto = new UpdateStockDto(BigDecimal.TEN);

        Stock existing = new Stock();

        existing.setPrice(BigDecimal.ONE);
        existing.setId(1L);
        existing.setName("Name");

        Stock saved = new Stock();

        saved.setPrice(BigDecimal.TEN);
        saved.setId(existing.getId());
        saved.setName(existing.getName());

        // no record
        when(stockRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(StockException.class,() -> stockService.updateStockPrice(dto,existing.getId()));

        verify(stockRepository,times(1)).findById(existing.getId());

        verify(stockRepository,times(0)).save(any(Stock.class));

        verifyNoMoreInteractions(stockRepository);
    }

    @Test
    public void givenNonExistentStock_thenStockNotDeleted(){

        // no record
        when(stockRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(StockException.class,() -> stockService.removeStockById(anyLong()));

        verify(stockRepository,times(0)).delete(any(Stock.class));

        verifyNoMoreInteractions(stockRepository);
    }

    @Test
    public void givenExistentStock_thenStockDeleted(){

        Stock existing = new Stock();

        existing.setPrice(BigDecimal.ONE);
        existing.setId(1L);
        existing.setName("Name");

        doNothing().when(stockRepository).delete(existing);
        when(stockRepository.findById(anyLong())).thenReturn(Optional.of(existing));

        ArgumentCaptor<Stock> argumentCaptor = ArgumentCaptor.forClass(Stock.class);

        stockService.removeStockById(existing.getId());

        verify(stockRepository,times(1)).findById(existing.getId());
        verify(stockRepository,times(1)).delete(argumentCaptor.capture());

        verifyNoMoreInteractions(stockRepository);
        assertThat(argumentCaptor.getValue().getId()).isEqualTo(existing.getId());
    }
}

