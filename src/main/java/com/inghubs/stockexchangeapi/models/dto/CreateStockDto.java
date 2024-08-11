package com.inghubs.stockexchangeapi.models.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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
public class CreateStockDto {
    @NotBlank(message = "Stock name can not be empty")
    @Size(min = 1, max = 64)
    private String name;

    @NotBlank(message = "Stock description can not be empty")
    @Size(min = 1, max = 64)
    private String description;

    @NotNull(message = "Stock price can not be empty")
    private BigDecimal price;
}
