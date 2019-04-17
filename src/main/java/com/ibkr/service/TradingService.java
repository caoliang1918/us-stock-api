package com.ibkr.service;

import com.tigerbrokers.stock.openapi.client.struct.param.OrderParameter;

/**
 * Created by caoliang on 2019/2/22
 */
public interface TradingService {


    /**
     * 创建订单
     *
     * @param order
     * @return
     */
    OrderParameter createOrder(OrderParameter order);

    /**
     * 取消订单
     *
     * @param orderId
     * @return
     */
    int cancleOrder(Long orderId);


    /**
     * 修改订单
     *
     * @param ooder
     * @return
     */
    OrderParameter updtaeOrder(OrderParameter ooder);
}
