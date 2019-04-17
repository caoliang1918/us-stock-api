package com.ibkr.entity.response;

import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;

import java.io.Serializable;

/**
 * Created by caoliang on 2019/2/25
 */
public class AccountOrder extends BaseOrder {


    /**
     * 持仓均价
     */
    private Double averageCost;
    /**
     * 合约编号(期权貌似为0，正股有数字编号)
     */
    private Integer contractId;
    /**
     * HK/USD
     */
    private String currency;
    private Double latestPrice;

    private String market;

    private Double marketValue;

    private Double multiplier;

    private Integer position;

    private Double preClose;

    private Double realizedPnl;

    private SecType secType;

    private Integer status;

    private String stockId;

    private String symbol;

    private Double unrealizedPnl;


    public Double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(Double averageCost) {
        this.averageCost = averageCost;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(Double latestPrice) {
        this.latestPrice = latestPrice;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(Double marketValue) {
        this.marketValue = marketValue;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Double getPreClose() {
        return preClose;
    }

    public void setPreClose(Double preClose) {
        this.preClose = preClose;
    }

    public Double getRealizedPnl() {
        return realizedPnl;
    }

    public void setRealizedPnl(Double realizedPnl) {
        this.realizedPnl = realizedPnl;
    }

    public SecType getSecType() {
        return secType;
    }

    public void setSecType(SecType secType) {
        this.secType = secType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getUnrealizedPnl() {
        return unrealizedPnl;
    }

    public void setUnrealizedPnl(Double unrealizedPnl) {
        this.unrealizedPnl = unrealizedPnl;
    }
}
