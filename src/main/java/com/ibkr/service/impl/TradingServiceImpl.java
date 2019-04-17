package com.ibkr.service.impl;

import com.ibkr.service.TradingService;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.struct.param.OrderParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 交易IPI
 * Created by caoliang on 2019/2/26
 */
@Service
public class TradingServiceImpl implements TradingService {
    private Logger logger = LoggerFactory.getLogger(TradingServiceImpl.class);

    @Autowired
    private TigerHttpClient client;

    @Value("${account}")
    private String account;

    @Override
    public OrderParameter createOrder(OrderParameter order) {
        return null;
    }

    @Override
    public int cancleOrder(Long orderId) {
        return 0;
    }

    @Override
    public OrderParameter updtaeOrder(OrderParameter ooder) {
        return null;
    }
}
