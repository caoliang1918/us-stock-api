package com.ibkr.entity;

import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionBriefItem;

/**
 * Created by caoliang on 2019/2/21
 */
public class OptionDetail extends OptionBriefItem implements Comparable {

    /**
     * 行权期
     */
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int compareTo(Object o) {
        OptionDetail other = (OptionDetail) o;
        return other.getVolume().compareTo(this.getVolume());
    }
}
