package com.bdgx.resolvers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockPriceUpdate {

    private  String stockCode;
    private  String dateTime;
    private  BigDecimal stockPrice;
    private  BigDecimal stockPriceChange;

    public StockPriceUpdate() {
    }

    public StockPriceUpdate(String stockCode, LocalDateTime dateTime, BigDecimal stockPrice, BigDecimal stockPriceChange) {
        this.stockCode = stockCode;
        this.dateTime = dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
        this.stockPrice = stockPrice;
        this.stockPriceChange = stockPriceChange;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(BigDecimal stockPrice) {
        this.stockPrice = stockPrice;
    }

    public BigDecimal getStockPriceChange() {
        return stockPriceChange;
    }

    public void setStockPriceChange(BigDecimal stockPriceChange) {
        this.stockPriceChange = stockPriceChange;
    }

    @Override
    public String toString() {
        return "StockPriceUpdate{" +
                "stockCode='" + stockCode + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", stockPrice=" + stockPrice +
                ", stockPriceChange=" + stockPriceChange +
                '}';
    }
}
