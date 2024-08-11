package com.inghubs.stockexchangeapi.models.event;

import com.inghubs.stockexchangeapi.models.entity.Stock;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class StockRemovedFromSystemEvent extends ApplicationEvent {
    private final Stock stock;

    public StockRemovedFromSystemEvent(Object source, Stock stock) {
        super(source);
        this.stock = stock;
    }
}