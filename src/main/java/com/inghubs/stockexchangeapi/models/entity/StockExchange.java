package com.inghubs.stockexchangeapi.models.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@ToString
public class StockExchange extends BaseEntity{
    @ColumnDefault("false")
    private Boolean liveInMarket;

    @ManyToMany
    @JoinTable(
            name = "stock_exchange_stock",
            joinColumns = @JoinColumn(name = "stock_exchange_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    private Set<Stock> stocks = new HashSet<>();

    public void addStock(Stock stock) {
        this.stocks.add(stock);
    }

    public void removeStock(Stock stock) {
        this.stocks.remove(stock);
    }
}
