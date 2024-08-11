package com.inghubs.stockexchangeapi.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@Builder
public class CreateStockExchangeDto {
    @NotBlank(message = "StockExchange name can not be empty")
    @Size(min = 1, max = 64)
    private String name;

    @NotBlank(message = "StockExchange description can not be empty")
    @Size(min = 1, max = 64)
    private String description;
}
