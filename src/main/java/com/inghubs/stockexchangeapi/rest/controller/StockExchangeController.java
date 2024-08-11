package com.inghubs.stockexchangeapi.rest.controller;

import com.inghubs.stockexchangeapi.models.dto.CreateStockDto;
import com.inghubs.stockexchangeapi.models.dto.CreateStockExchangeDto;
import com.inghubs.stockexchangeapi.models.entity.Stock;
import com.inghubs.stockexchangeapi.models.entity.StockExchange;
import com.inghubs.stockexchangeapi.service.StockExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stock-exchange")
@RequiredArgsConstructor
public class StockExchangeController {
    private final StockExchangeService stockExchangeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockExchange> createStockExchange(@RequestBody @Valid CreateStockExchangeDto dto) {
        StockExchange created = stockExchangeService.creteStockExchange(dto);
        return new ResponseEntity<StockExchange>(created, HttpStatus.CREATED);
    }
//    @GetMapping("/{stockExchangeId}")
//    public ResponseEntity<StockExchange> getStockExchangeById(@PathVariable Long stockExchangeId) {
//        StockExchange stockExchange = stockExchangeService.getStockExchangeById(stockExchangeId);
//        return ResponseEntity.ok(stockExchange);
//    }
    @Operation(summary = "Gets the stock-exchange by Name parameter with its stocks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exchange returned successfully"),
        @ApiResponse(responseCode = "400", description = "Non existent exchange", content = @Content),
    })
    @GetMapping("/{stockExchangeName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<StockExchange> getStockExchangeByName(@PathVariable String stockExchangeName) {
        StockExchange stockExchange = stockExchangeService.findByExchangeNameWithStocks(stockExchangeName);
        return ResponseEntity.ok(stockExchange);
    }
//    @PostMapping("/{stockExchangeId}/{stockId}")
//    public ResponseEntity<StockExchange> addStockToStockExchange(@PathVariable Long stockExchangeId,@PathVariable Long stockId) {
//        StockExchange stockExchangeDTO = stockExchangeService.addStockToStockExchange(stockExchangeId, stockId);
//        return new ResponseEntity<>(stockExchangeDTO, HttpStatus.OK);
//    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{stockExchangeName}/{stockId}")
    public ResponseEntity<StockExchange> addStockToStockExchange(@PathVariable String stockExchangeName,@PathVariable Long stockId) {
        StockExchange stockExchangeDTO = stockExchangeService.addStockToStockExchange(stockExchangeName, stockId);
        return new ResponseEntity<>(stockExchangeDTO, HttpStatus.CREATED);
    }
    @Operation(summary = "Removes the stock from exchange by exchange Name and stock id parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Stock is removed from the exchange successfully"),
            @ApiResponse(responseCode = "400", description = "Non existent stock attempted to be removed from the exchange", content = @Content),
    })
    @DeleteMapping("/{stockExchangeName}/{stockId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockExchange> removeStockFromStockExchange(@PathVariable String stockExchangeName,@PathVariable Long stockId) {
        StockExchange stockExchangeDTO = stockExchangeService.removeStockFromStockExchange(stockExchangeName, stockId);
        return ResponseEntity.noContent().build();
    }
//    @PostMapping("/{stockExchangeName/}}")
//    public ResponseEntity<StockExchange> openOrCloseTheExchange(@PathVariable String stockExchangeName) {
//        StockExchange stockExchangeDTO = stockExchangeService.removeStockFromStockExchange(stockExchangeName, stockId);
//        return ResponseEntity.noContent().build();
//    }
}
