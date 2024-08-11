package com.inghubs.stockexchangeapi.service;

import com.inghubs.stockexchangeapi.models.dto.CreateStockExchangeDto;
import com.inghubs.stockexchangeapi.models.entity.Stock;
import com.inghubs.stockexchangeapi.models.entity.StockExchange;
import com.inghubs.stockexchangeapi.models.event.StockAddedToExchangeEvent;
import com.inghubs.stockexchangeapi.models.event.StockRemovedFromExchangeEvent;
import com.inghubs.stockexchangeapi.models.exception.StockException;
import com.inghubs.stockexchangeapi.repository.StockExchangeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
@RequiredArgsConstructor
@Log4j2
public class StockExchangeService {
    private final StockExchangeJpaRepository stockExchangeRepository;
    private final StockService stockService;
    private final ApplicationEventPublisher eventPublisher;

    public StockExchange creteStockExchange(CreateStockExchangeDto dto) {
        StockExchange stockExchange = new StockExchange();
        BeanUtils.copyProperties(dto,stockExchange);
        return stockExchangeRepository.save(stockExchange);
    }

    @Transactional(readOnly = true)
    public StockExchange getStockExchangeByName(String name) {
        return stockExchangeRepository.findByName(name).orElseThrow(() -> new StockException(String.format("Stock with name %s not found on this server!",name)));
    }

    public StockExchange addStockToStockExchange(String stockExchangeName, Long stockId) {
        Stock stock = stockService.getStockById(stockId);

        if(stock == null) {
            throw new StockException(String.format("Stock with id %s not found on this server!",stockId));
        }
        StockExchange stockExchange = getStockExchangeByName(stockExchangeName);

        if (stockExchange.getStocks().contains(stock)) {
            throw new StockException(String.format("Stock with id %d already in the exchange with the name of %s",stockId,stockExchangeName));
        }
        stockExchange.addStock(stock);
        // or , use the event to add the stock to the exchange for finer concurrency control.
        // This is simpler to understand and test,so went with this
        // Can demonstrate eventual consistency when walking you through my code with screensharing like you said in the doc
        StockExchange exchange = stockExchangeRepository.save(stockExchange);
        eventPublisher.publishEvent(new StockAddedToExchangeEvent(this,stock,stockExchangeName));
        return exchange;
    }

    @Transactional(readOnly = true)
    public StockExchange findByExchangeNameWithStocks (String exchangeName) {
        return stockExchangeRepository.findByExchangeNameWithStocks(exchangeName).orElseThrow(()-> new StockException(String.format("Stock exchange with id %s not found on this server!",exchangeName)));
    }

    @Transactional(readOnly = true)
    public Long countStocksByExchangeName (String exchangeName) {
        return stockExchangeRepository.countStocksByExchangeName(exchangeName);
    }

    public StockExchange removeStockFromStockExchange(String stockExchangeName, Long stockId) {
        Stock stock = stockService.getStockById(stockId);

        if(stock == null) {
            throw new StockException(String.format("Stock with id %s not found on this server!",stockId));
        }
        StockExchange stockExchange = getStockExchangeByName(stockExchangeName);

        if (!stockExchange.getStocks().contains(stock)) {
            throw new StockException(String.format("Stock with the id %d is not present in the exchange with the name of %s",stockId,stockExchangeName));
        }
        // or , use the event to remove the stock from the exchange for finer concurrency control.
        // This is simpler to understand and test,so went with this
        // Can demonstrate eventual consistency when walking you through my code with screensharing like you said in the doc
        stockExchange.removeStock(stock);
        StockExchange exchange = stockExchangeRepository.save(stockExchange);
        eventPublisher.publishEvent(new StockRemovedFromExchangeEvent(this,stock,stockExchangeName));
        return exchange;
    }
}
