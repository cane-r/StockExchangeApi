package com.inghubs.stockexchangeapi.models.event;

import com.inghubs.stockexchangeapi.models.entity.Stock;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class StockRemovedFromExchangeEvent extends ApplicationEvent {
    private final Stock stock;
    private final String stockExchangeName;

    public StockRemovedFromExchangeEvent(Object source, Stock stock, String stockExchangeName) {
        super(source);
        this.stock = stock;
        this.stockExchangeName = stockExchangeName;
    }
}