package com.ibkr.entity;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.Serializable;
import java.util.Date;

public class StockQuery implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_query.id
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    private Integer id;


    /**
     * 股票名称
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_query.cts
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    private Date cts;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_query.uts
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    private Date uts;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_query.symbol
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    private String symbol;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_query.avg_price
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    private String avgPrice;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_query.price
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    private String price;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column stock_query.volume
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    private Integer volume;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table stock_query
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_query.id
     *
     * @return the value of stock_query.id
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_query.id
     *
     * @param id the value for stock_query.id
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_query.cts
     *
     * @return the value of stock_query.cts
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */


    public Date getCts() {
        return cts;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_query.cts
     *
     * @param cts the value for stock_query.cts
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public void setCts(Date cts) {
        this.cts = cts;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_query.uts
     *
     * @return the value of stock_query.uts
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public Date getUts() {
        return uts;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_query.uts
     *
     * @param uts the value for stock_query.uts
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public void setUts(Date uts) {
        this.uts = uts;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_query.symbol
     *
     * @return the value of stock_query.symbol
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_query.symbol
     *
     * @param symbol the value for stock_query.symbol
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_query.avg_price
     *
     * @return the value of stock_query.avg_price
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public String getAvgPrice() {
        return avgPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_query.avg_price
     *
     * @param avgPrice the value for stock_query.avg_price
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice == null ? null : avgPrice.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_query.price
     *
     * @return the value of stock_query.price
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public String getPrice() {
        return price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_query.price
     *
     * @param price the value for stock_query.price
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public void setPrice(String price) {
        this.price = price == null ? null : price.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column stock_query.volume
     *
     * @return the value of stock_query.volume
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public Integer getVolume() {
        return volume;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column stock_query.volume
     *
     * @param volume the value for stock_query.volume
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    public void setVolume(Integer volume) {
        this.volume = volume;
    }
}