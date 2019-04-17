package com.ibkr.entity;

/**
 * @author : caoliang1918@gmail.com
 * @date :   2018/11/4 18:30
 */
public class StockPrice implements Comparable<StockPrice> {
    private String code;
    private String avgPrice;
    private String price;
    private String time;
    private Integer volume;
    private String tag;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int compareTo(StockPrice o) {
        if (o.time == null || this.time == null) {
            return 0;
        }
        return o.time.compareTo(this.time);
    }
}
