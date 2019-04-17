package com.ibkr.entity.response;

import com.tigerbrokers.stock.openapi.client.struct.enums.*;

/**
 * Created by caoliang on 2019/2/26
 */
public class PendingOrder extends BaseOrder {
    private String symbol;
    private OrderType orderType;
    private Long orderId;
    private String channel;
    private Integer discount;
    private SecType secType;
    private Integer filledQuantity;
    private Integer totalQuantity;
    private ActionType actionType;
    private Integer commission;
    private Currency currency;
    private Long openTime;
    private String timeInForce;
    private Double limitPrice;
    private Market market;
    private Boolean outsideRth;
    private Double avgFillPrice;
    private String  stockId;
    private Integer realizedPnl;
    private Long latestTime;
    private OrderStatus status;


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public SecType getSecType() {
        return secType;
    }

    public void setSecType(SecType secType) {
        this.secType = secType;
    }

    public Integer getFilledQuantity() {
        return filledQuantity;
    }

    public void setFilledQuantity(Integer filledQuantity) {
        this.filledQuantity = filledQuantity;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Integer getCommission() {
        return commission;
    }

    public void setCommission(Integer commission) {
        this.commission = commission;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Long openTime) {
        this.openTime = openTime;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public Double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(Double limitPrice) {
        this.limitPrice = limitPrice;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public Boolean getOutsideRth() {
        return outsideRth;
    }

    public void setOutsideRth(Boolean outsideRth) {
        this.outsideRth = outsideRth;
    }

    public Double getAvgFillPrice() {
        return avgFillPrice;
    }

    public void setAvgFillPrice(Double avgFillPrice) {
        this.avgFillPrice = avgFillPrice;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public Integer getRealizedPnl() {
        return realizedPnl;
    }

    public void setRealizedPnl(Integer realizedPnl) {
        this.realizedPnl = realizedPnl;
    }

    public Long getLatestTime() {
        return latestTime;
    }

    public void setLatestTime(Long latestTime) {
        this.latestTime = latestTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
