package com.ibkr.service;

import com.ibkr.entity.response.AccountOrder;
import com.ibkr.entity.response.PendingOrder;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import com.tigerbrokers.stock.openapi.client.struct.param.OrderParameter;

import java.util.List;

/**
 * Created by caoliang on 2019/2/22
 */
public interface AccountService {


    /**
     * 获取持仓数据
     *
     * @param secType
     * @return
     */
    List<AccountOrder> getAccountOrders(SecType secType);


    /**
     * 获取待成交订单
     *
     * @param secType
     * @return
     */
    List<PendingOrder> getPendingOrders(SecType secType);
}
