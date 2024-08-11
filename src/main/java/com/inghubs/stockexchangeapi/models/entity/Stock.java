package com.inghubs.stockexchangeapi.models.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Stock extends BaseEntity {
    private BigDecimal price;

    @ManyToMany(mappedBy = "stocks")
    @JsonBackReference
    private Set<StockExchange> stockExchanges = new HashSet<>();

    // or do it via an event,like StockRemovedFromSystemEvent
    @PreRemove
    public void removeFromStockExchange() {
        stockExchanges.forEach(s -> s.removeStock(this));
    }

}
