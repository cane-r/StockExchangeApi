package com.inghubs.stockexchangeapi.models.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStockDto {
    @NotNull(message = "New stock price can not be empty")
    private BigDecimal newPrice;
}
