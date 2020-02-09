package com.ibkr.entity;

import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionRealTimeQuote;

public class OptionTimeQuote extends OptionRealTimeQuote {

    /**
     * 股票代码
     */
    private String symbol;

    /**
     * 行权期
     */
    private String expiry;

    private String right;

    /**
     * @param symbol       期权代码
     * @param right        CALL/PUT
     * @param expiry       行权期
     * @param latestPrice  最新价
     * @param strike       行权价
     * @param openInterest 未平仓数
     */
    public OptionTimeQuote(String symbol, String right, String expiry, Double latestPrice, String strike, int openInterest) {
        this.symbol = symbol;
        this.right = right;
        this.expiry = expiry;
        setLatestPrice(latestPrice);
        setStrike(strike);
        setOpenInterest(openInterest);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
