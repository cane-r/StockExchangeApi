package com.inghubs.stockexchangeapi.rest.controller;

import com.inghubs.stockexchangeapi.models.dto.CreateStockDto;
import com.inghubs.stockexchangeapi.models.dto.UpdateStockDto;
import com.inghubs.stockexchangeapi.models.entity.Stock;
import com.inghubs.stockexchangeapi.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Tag(name = "StockController", description = "Stock controller endpoints")
public class StockController {

    private final StockService stockService;

    @Operation(summary = "Creates a new stock")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Stock> createStock(@RequestBody @Valid CreateStockDto stock) {
        Stock created = stockService.createStock(stock);
        return new ResponseEntity<Stock>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Updates a stock's price")
    @PatchMapping("/{stockId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
            @ApiResponse(responseCode = "400", description = "Non existent stock update attempt", content = @Content),
    })

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Stock> updateStockPrice(@Valid @RequestBody UpdateStockDto stock, @PathVariable Long stockId) {
        return ResponseEntity.ok(stockService.updateStockPrice(stock,stockId));
    }

    @Operation(summary = "Gets the stock by Id parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock obtained ok"),
            @ApiResponse(responseCode = "400", description = "Non existent stock", content = @Content),
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{stockId}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long stockId) {
        return ResponseEntity.ok(stockService.getStockById(stockId));
    }

    @DeleteMapping("/{stockId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Stock> removeStockById(@PathVariable Long stockId) {
        stockService.removeStockById(stockId);
        return ResponseEntity.noContent().build();
    }
}
