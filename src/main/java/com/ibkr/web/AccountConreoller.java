package com.ibkr.web;

import com.ibkr.entity.CommonResponse;
import com.ibkr.service.AccountService;
import com.tigerbrokers.stock.openapi.client.https.response.TigerHttpResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by caoliang on 2019/2/25
 */
@RestController
@RequestMapping("account")
public class AccountConreoller {

    @Autowired
    private AccountService accountService;


    /**
     * 获取持仓
     *
     * @param secType
     * @return
     */
    @GetMapping("getAccountOrders")
    public CommonResponse getAccountPosition(SecType secType) {

        return new CommonResponse(accountService.getAccountOrders(secType));
    }

    /**
     * 待成交订单
     *
     * @param secType
     * @return
     */
    @GetMapping("getPendingOrders")
    public CommonResponse getPendingOrders(SecType secType) {
        return new CommonResponse(accountService.getPendingOrders(secType));
    }

    /**
     * 撤销待成交订单
     *
     * @param orderId
     * @return
     */
    @PostMapping("cancelOrder")
    public CommonResponse<TigerHttpResponse> cancelOrder(@RequestParam Long orderId) {
        return new CommonResponse(accountService.cancelOrder(orderId));
    }
}
