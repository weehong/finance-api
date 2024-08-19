package com.mattemat.finance.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class CurrencyConversionResponseDto {

    private Map<String, CurrencyDto> data;

    @Getter
    @Setter
    public static class CurrencyDto {
        private String code;
        private BigDecimal value;
    }
}
