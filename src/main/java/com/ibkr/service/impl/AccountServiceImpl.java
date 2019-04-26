package com.ibkr.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibkr.entity.response.AccountOrder;
import com.ibkr.entity.response.PendingOrder;
import com.ibkr.service.AccountService;
import com.tigerbrokers.stock.openapi.client.constant.ApiServiceType;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.request.TigerHttpRequest;
import com.tigerbrokers.stock.openapi.client.https.response.TigerHttpResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import com.tigerbrokers.stock.openapi.client.util.builder.AccountParamBuilder;
import com.tigerbrokers.stock.openapi.client.util.builder.TradeParamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 账户相关API
 * Created by caoliang on 2019/2/22
 */
@Service
public class AccountServiceImpl implements AccountService {
    private Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private TigerHttpClient client;

    @Value("${account}")
    private String account;

    @Override
    public List<AccountOrder> getAccountOrders(SecType secType) {
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.POSITIONS);

        String bizContent = AccountParamBuilder.instance()
                .account(account)
                .secType(secType)
                .buildJson();

        request.setBizContent(bizContent);
        TigerHttpResponse response = client.execute(request);
        logger.info("getAccountOrders : {}", response.getData());
        if (!response.isSuccess()) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(response.getData());
        JSONArray array = jsonObject.getJSONArray("items");
        if (array == null || array.size() == 0) {
            return null;
        }
        List<AccountOrder> accountPositionList = new ArrayList<>();
        AccountOrder accountOrder = null;
        for (Object object : array) {
            accountOrder = JSONObject.parseObject(object.toString(), AccountOrder.class);
            accountPositionList.add(accountOrder);
        }
        return accountPositionList;
    }

    @Override
    public List<PendingOrder> getPendingOrders(SecType secType) {
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.ACTIVE_ORDERS);
        String bizContent = AccountParamBuilder.instance()
                .account(account)
                .secType(secType)
                .buildJson();
        request.setBizContent(bizContent);
        TigerHttpResponse response = client.execute(request);
        if (!response.isSuccess()) {
            return null;
        }
        logger.info("getPendingOrders : {}", response.getData());
        List<PendingOrder> orderList = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(response.getData());
        JSONArray array = jsonObject.getJSONArray("items");
        if (array == null || array.size() == 0) {
            return null;
        }
        PendingOrder pendingOrder = null;
        for (Object object : array) {
            pendingOrder = JSONObject.parseObject(object.toString(), PendingOrder.class);
            orderList.add(pendingOrder);
        }

        return orderList;
    }

    @Override
    public TigerHttpResponse cancelOrder(Long orderId) {
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.CANCEL_ORDER);

        String bizContent = TradeParamBuilder.instance()
                .account(account)
                .id(orderId)
                .buildJson();

        request.setBizContent(bizContent);
        TigerHttpResponse response = client.execute(request);
        return response;
    }
}
