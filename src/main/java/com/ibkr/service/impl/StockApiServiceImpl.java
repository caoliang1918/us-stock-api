package com.ibkr.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ibkr.entity.OptionTimeQuote;
import com.ibkr.entity.StockOptions;
import com.ibkr.service.StockApiService;
import com.ibkr.util.DateUtil;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.*;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainModel;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionCommonModel;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionBriefQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionChainQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionExpirationQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionTradeTickQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionBriefResponse;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionChainResponse;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionExpirationResponse;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionTradeTickResponse;
import com.zhongweixian.excel.entity.params.ExcelExportEntity;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.*;

/**
 * 行情API
 * Created by caoliang on 2019/2/27
 */
@Service
public class StockApiServiceImpl implements StockApiService {
    private Logger logger = LoggerFactory.getLogger(StockApiServiceImpl.class);

    @Autowired
    private TigerHttpClient tigerHttpClient;

    @Override
    public OptionBriefItem getOptionDetail(String sympol, String right, String strike, Long expiry) {
        OptionCommonModel model = new OptionCommonModel();
        model.setSymbol(sympol);
        model.setRight(right);
        model.setStrike(strike);
        model.setExpiry(expiry);
        OptionBriefResponse response = tigerHttpClient.execute(OptionBriefQueryRequest.of(model));
        if (response.isSuccess()) {
            return response.getOptionBriefItems().get(0);
        } else {
            return null;
        }
    }


    @Override
    public List<StockOptions> getStockOptionsItems(StockOptions stockOptions) {
        OptionCommonModel model = new OptionCommonModel();
        model.setSymbol(stockOptions.getSymbol());
        model.setRight(stockOptions.getRight());
        //行权价
        model.setStrike(stockOptions.getStrike());
        //行权期
        model.setExpiry(stockOptions.getExpiry());
        OptionTradeTickResponse response = tigerHttpClient.execute(OptionTradeTickQueryRequest.of(model));
        if (response.isSuccess() && !CollectionUtils.isEmpty(response.getOptionTradeTickItems())) {
            List<StockOptions> list = new ArrayList<>();
            StockOptions options = null;
            for (TradeTickPoint tradeTickPoint : response.getOptionTradeTickItems().get(0).getItems()) {
                options = new StockOptions();
                options.setStrike(stockOptions.getStrike());
                options.setSymbol(stockOptions.getSymbol());
                options.setRight(stockOptions.getRight());
                options.setExpiry(stockOptions.getExpiry());
                options.setCts(new Date());
                options.setUts(DateUtil.format(new Date(tradeTickPoint.getTime()), TimeZone.getTimeZone("GMT-5"), DateUtil.DAT_TIME));
                options.setPrice(tradeTickPoint.getPrice().toString());
                options.setVolume(tradeTickPoint.getVolume().intValue());
                list.add(options);
            }
            //取最后10个
            return list;
        } else {
            logger.error("response error:{}", response.getMessage());
        }
        return null;
    }


    /**
     * 获取期权过期日
     */
    @Override
    public List<Map<String, Object>> getOptionExpirations(String symbol) {
        List<String> symbols = new ArrayList<>();
        symbols.add(symbol);
        OptionExpirationResponse response = tigerHttpClient.execute(new OptionExpirationQueryRequest(symbols));
        if (!response.isSuccess() || CollectionUtils.isEmpty(response.getOptionExpirationItems())) {
            logger.warn("{}", "response error:" + response.getMessage());
            return null;
        }
        List<Map<String, Object>> optionTimeQuoteList = new ArrayList<>();
        for (OptionExpirationItem optionExpirationItem : response.getOptionExpirationItems()) {
            optionExpirationItem.getTimestamps().forEach(s -> {
                if (s < Instant.now().getEpochSecond()) {
                    logger.warn("{}", s);
                    return;
                }
                logger.info("symbol : {} , date ： {}", optionExpirationItem.getSymbol(), DateFormatUtils.format(s , "yyyy-MM-dd"));
                optionTimeQuoteList.addAll(optionChain(optionExpirationItem.getSymbol(), DateFormatUtils.format(s , "yyyy-MM-dd")));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        return optionTimeQuoteList;
    }


    /**
     * 期权链
     *
     * @param symbol 代码
     * @param expiry 行权期
     */
    private List<Map<String, Object>> optionChain(String symbol, String expiry) {
        OptionChainModel model = new OptionChainModel();
        model.setSymbol(symbol);
        model.setExpiry(expiry);
        OptionChainResponse response = tigerHttpClient.execute(OptionChainQueryRequest.of(model));
        if (!response.isSuccess() || CollectionUtils.isEmpty(response.getOptionChainItems())) {
            logger.warn("response error:" + response.getMessage());
            return null;
        }
        List<Map<String, Object>> optionTimeQuoteList = new ArrayList<>();

        logger.debug(Arrays.toString(response.getOptionChainItems().toArray()));
        for (OptionRealTimeQuoteGroup optionRealTimeQuoteGroup : response.getOptionChainItems().get(0).getItems()) {
            if (optionRealTimeQuoteGroup.getCall() != null) {
                logger.info("sympol:{}, expiry:{}, CALL:{}", symbol, expiry, JSON.toJSONString(optionRealTimeQuoteGroup.getCall()));
                Map<String, Object> mapcall = new HashMap<>();
                mapcall.put("symbol", symbol);
                mapcall.put("right", "CALL");
                mapcall.put("expiry", expiry);
                mapcall.put("latestPrice", optionRealTimeQuoteGroup.getCall().getLatestPrice());
                mapcall.put("strike", optionRealTimeQuoteGroup.getCall().getStrike());
                mapcall.put("volume", optionRealTimeQuoteGroup.getCall().getVolume());
                mapcall.put("openInterest", optionRealTimeQuoteGroup.getCall().getOpenInterest());
                optionTimeQuoteList.add(mapcall);
            }
            if (optionRealTimeQuoteGroup.getPut() != null) {
                logger.info("sympol:{}, expiry:{}, PUT:{}", symbol, expiry, JSON.toJSONString(optionRealTimeQuoteGroup.getPut()));
                Map<String, Object> mapput = new HashMap<>();
                mapput.put("symbol", symbol);
                mapput.put("right", "PUT");
                mapput.put("expiry", expiry);
                mapput.put("latestPrice", optionRealTimeQuoteGroup.getPut().getLatestPrice());
                mapput.put("strike", optionRealTimeQuoteGroup.getPut().getStrike());
                mapput.put("volume", optionRealTimeQuoteGroup.getPut().getVolume());
                mapput.put("openInterest", optionRealTimeQuoteGroup.getPut().getOpenInterest());
                optionTimeQuoteList.add(mapput);
            }
        }
        return optionTimeQuoteList;
    }
}
