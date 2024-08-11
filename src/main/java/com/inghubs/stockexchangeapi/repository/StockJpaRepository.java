package com.inghubs.stockexchangeapi.repository;

import com.inghubs.stockexchangeapi.models.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockJpaRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByName(String name);
}
