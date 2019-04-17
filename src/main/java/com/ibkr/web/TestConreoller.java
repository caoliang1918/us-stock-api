package com.ibkr.web;

import com.ibkr.entity.CommonResponse;
import com.ibkr.service.AccountService;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by caoliang on 2019/2/25
 */
@RestController
@RequestMapping("test")
public class TestConreoller {

    @Autowired
    private AccountService accountService;


    @GetMapping("getAccountOrders")
    public CommonResponse getAccountPosition(SecType secType) {

        return new CommonResponse(accountService.getAccountOrders(secType));
    }

    @GetMapping("getPendingOrders")
    public CommonResponse getPendingOrders(SecType secType) {
        return new CommonResponse(accountService.getPendingOrders(secType));
    }
}
