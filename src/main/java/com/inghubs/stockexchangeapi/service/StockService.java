package com.inghubs.stockexchangeapi.service;

import com.inghubs.stockexchangeapi.models.dto.CreateStockDto;
import com.inghubs.stockexchangeapi.models.dto.UpdateStockDto;
import com.inghubs.stockexchangeapi.models.entity.Stock;
import com.inghubs.stockexchangeapi.models.exception.StockException;
import com.inghubs.stockexchangeapi.repository.StockJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
@RequiredArgsConstructor
public class StockService {
    private final StockJpaRepository stockRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Creates a Stock.
     *
     * @param dto {@link com.inghubs.stockexchangeapi.models.dto.CreateStockDto} object for Stock creation
     * @return {@link com.inghubs.stockexchangeapi.models.entity.Stock}
     */
    public Stock createStock(CreateStockDto dto) {
        Stock stock = new Stock();
        // or use a converter object/library
        BeanUtils.copyProperties(dto,stock);
        return stockRepository.save(stock);
    }
    public Stock updateStockPrice(UpdateStockDto dto,Long stockId) {
        // or use name property ( i.e findByName ) ?
        Optional<Stock> existingStock = stockRepository.findById(stockId);
        if (existingStock.isEmpty()) {
            throw new StockException(String.format("Stock with id %s not found on this server!",stockId));
        }
        Stock stock = existingStock.get();
        stock.setPrice(dto.getNewPrice());
        return stockRepository.save(stock);
    }
    @Transactional(readOnly = true)
    public Stock getStockById(Long stockId) {
        return stockRepository.findById(stockId).orElseThrow(() -> new StockException(String.format("Stock with id %s not found on this server!",stockId)));
    }
    /**
     * Creates a Stock.
     *
     * @param stockId {@link java.lang.Long}
     */
    public void removeStockById(Long stockId) {
        Stock stockToBeRemoved = getStockById(stockId);
        stockRepository.delete(stockToBeRemoved);
    }
}
