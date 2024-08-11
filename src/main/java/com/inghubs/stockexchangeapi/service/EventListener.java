package com.inghubs.stockexchangeapi.service;

import com.inghubs.stockexchangeapi.models.entity.Stock;
import com.inghubs.stockexchangeapi.models.entity.StockExchange;
import com.inghubs.stockexchangeapi.models.event.StockAddedToExchangeEvent;
import com.inghubs.stockexchangeapi.models.event.StockRemovedFromExchangeEvent;
import com.inghubs.stockexchangeapi.models.event.StockRemovedFromSystemEvent;
import com.inghubs.stockexchangeapi.models.exception.StockException;
import com.inghubs.stockexchangeapi.repository.StockExchangeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;

/**
 * Event listener for updating liveInMarket property of a stock.
 * Note that it can be equally used for adding and removing stocks to/from exchanges for better concurrency control( but harder to test and visually confirm )
 * Also note that methods have REQUIRES_NEW transactional propagation,which means adding/removing stocks and updating liveInMarket operations
 * happen in separate transactions
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class EventListener {

    private final StockExchangeJpaRepository stockExchangeJpaRepository;

    @Value("${liveInMarket.threshold}")
    private Integer threshold;

    /**
     * Handles the {@link com.inghubs.stockexchangeapi.models.event.StockAddedToExchangeEvent} event
     */
    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleStockAddedToExchangeEvent(StockAddedToExchangeEvent event) {
        String exchangeName = event.getStockExchangeName();
        Long count = stockExchangeJpaRepository.countStocksByExchangeName(exchangeName);
        log.info("handleStockAddedToExchangeEvent event listener!");
        if(count >= threshold) {
            StockExchange exchange = stockExchangeJpaRepository.findByName(event.getStockExchangeName()).orElseThrow(() -> new StockException(String.format("Stock with name %s not found on this server!",exchangeName)));
            exchange.setLiveInMarket(true);
            stockExchangeJpaRepository.save(exchange);
        }
    }
    /**
     * Handles the {@link com.inghubs.stockexchangeapi.models.event.StockRemovedFromExchangeEvent} event
     */
    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleStockRemovedFromExchangeEvent(StockRemovedFromExchangeEvent event) {
        String exchangeName = event.getStockExchangeName();
        Long count = stockExchangeJpaRepository.countStocksByExchangeName(exchangeName);
        log.info("handleStockRemovedFromExchangeEvent event listener!");
        if(count < threshold) {
            StockExchange exchange = stockExchangeJpaRepository.findByName(event.getStockExchangeName()).orElseThrow(() -> new StockException(String.format("Stock with name %s not found on this server!",exchangeName)));
            exchange.setLiveInMarket(false);
            stockExchangeJpaRepository.save(exchange);
        }
    }
}
