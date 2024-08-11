package com.inghubs.stockexchangeapi.repository;

import com.inghubs.stockexchangeapi.models.entity.StockExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StockExchangeJpaRepository extends JpaRepository<StockExchange, Long> {
    Optional<StockExchange> findByName(String name);
    @Query("SELECT se FROM StockExchange se LEFT JOIN fetch se.stocks st WHERE se.name =:exchangeName")
    Optional<StockExchange> findByExchangeNameWithStocks(@Param("exchangeName") String exchangeName);

    @Query("select count(st) from StockExchange se join se.stocks st where se.name =:exchangeName")
    Long countStocksByExchangeName(@Param("exchangeName") String exchangeName);

    @Query("SELECT se FROM StockExchange se JOIN se.stocks s WHERE s.id = :stockId")
    Set<StockExchange> findAllExchangesStockListed(@Param("stockId") Long stockId);
}