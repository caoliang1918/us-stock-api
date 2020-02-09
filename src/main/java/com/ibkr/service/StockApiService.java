package com.ibkr.service;

import com.ibkr.entity.OptionTimeQuote;
import com.ibkr.entity.StockOptions;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionBriefItem;

import java.util.List;
import java.util.Map;

/**
 * Created by caoliang on 2019/2/27
 */
public interface StockApiService {

    /**
     * 获取某一个期权详情
     *
     * @param sympol
     * @param right
     * @param strike
     * @param expiry
     * @return
     */
    OptionBriefItem getOptionDetail(String sympol, String right, String strike, Long expiry);


    /**
     * 获取期权逐笔交易
     *
     * @param stockOptions
     * @return
     */
    List<StockOptions> getStockOptionsItems(StockOptions stockOptions);


    /**
     * 期权
     */
    List<Map<String , Object>> getOptionExpirations(String symbol);


}
