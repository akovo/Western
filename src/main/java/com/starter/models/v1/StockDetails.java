package com.starter.models.v1;

import lombok.Data;

@Data
public class StockDetails {

    private Double price;

    private int numberOfShares;

    private Double marketValue;

    private Double bookValue;

    private String ticker;

}
