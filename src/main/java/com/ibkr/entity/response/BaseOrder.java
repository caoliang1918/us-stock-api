package com.ibkr.entity.response;

import java.io.Serializable;

/**
 * Created by caoliang on 2019/2/26
 */
public class BaseOrder implements Serializable {


    private Long id;

    private String account;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
