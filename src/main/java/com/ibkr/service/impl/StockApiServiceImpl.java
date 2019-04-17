package com.ibkr.service.impl;

import com.ibkr.entity.StockOptions;
import com.ibkr.service.StockApiService;
import com.ibkr.util.DateUtil;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionBriefItem;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.TradeTickPoint;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionCommonModel;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionBriefQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionTradeTickQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionBriefResponse;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionTradeTickResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
                options.setUts(DateUtil.format(new Date(tradeTickPoint.getTime()), TimeZone.getTimeZone("GMT-5"), DateUtil.DAT_TIME));
                options.setPrice(tradeTickPoint.getPrice().toString());
                options.setVolume(tradeTickPoint.getVolume().intValue());
                options.setCts(new Date());

                list.add(options);
            }
            //取最后10个
            return list.subList(list.size() - 10, list.size());
        } else {
            logger.error("response error:{}", response.getMessage());
        }
        return null;
    }
}
