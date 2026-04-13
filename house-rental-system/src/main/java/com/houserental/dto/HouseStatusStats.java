package com.houserental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseStatusStats {

    private Integer status;

    private String statusName;

    private Long count;

    private BigDecimal percentage;
}