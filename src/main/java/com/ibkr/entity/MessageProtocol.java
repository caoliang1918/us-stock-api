package com.ibkr.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by caoliang on 2018/7/18
 */
public class MessageProtocol implements Serializable {

    private Long id;
    private String station;
    private Date cts = new Date();
    private String body;

    public MessageProtocol() {

    }


    public MessageProtocol(long id, String body) {
        this.id = id;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Date getCts() {
        return cts;
    }

    public void setCts(Date cts) {
        this.cts = cts;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", station='" + station + '\'' +
                ", cts=" + cts +
                ", body='" + body + '\'' +
                '}';
    }
}
