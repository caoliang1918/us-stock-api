package com.ibkr.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibkr.entity.StockOptions;
import com.ibkr.entity.StockProduct;
import com.ibkr.entity.StockQuery;
import com.ibkr.mapper.StockOptionsMapper;
import com.ibkr.mapper.StockProductMapper;
import com.ibkr.mapper.StockQueryMapper;
import com.ibkr.service.StockQueryService;
import com.ibkr.util.DateUtil;
import com.tigerbrokers.stock.openapi.client.constant.ApiServiceType;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionBriefItem;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.TradeTickPoint;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionCommonModel;
import com.tigerbrokers.stock.openapi.client.https.request.TigerHttpRequest;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionBriefQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionTradeTickQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.response.TigerHttpResponse;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionBriefResponse;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionTradeTickResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.tigerbrokers.stock.openapi.client.util.builder.QuoteParamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 行情API
 * Created by caoliang on 2018/11/7
 */
@Service
public class StockQueryServiceImpl implements StockQueryService {
    private Logger logger = LoggerFactory.getLogger(StockQueryServiceImpl.class);

    @Autowired
    private StockProductMapper stockProductMapper;

    @Autowired
    private StockQueryMapper stockQueryMapper;

    @Autowired
    private StockOptionsMapper stockOptionsMapper;

    @Autowired
    private TigerHttpClient tigerHttpClient;


    @Override
    public int saveStockQuery(StockQuery stockQuery) {
        StockQuery exitStockQuery = stockQueryMapper.selectByUniqueKey(stockQuery.getUts(), stockQuery.getSymbol() , stockQuery.getPrice());
        if (exitStockQuery != null) {
            return 0;
        }
        logger.info("add symbol:{} , date:{} , price:{} , volume:{} ", stockQuery.getSymbol(), stockQuery.getUts(), stockQuery.getPrice() , stockQuery.getVolume());
        return stockQueryMapper.insertSelective(stockQuery);
    }

    @Override
    public int saveStockProduct(StockProduct stockProduct) {
        try {
            stockProductMapper.insertSelective(stockProduct);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int updateProduct(List<StockProduct> stockProducts) {
        return stockProductMapper.updateByPrimaryKeySelective(stockProducts);
    }

    @Override
    public StockProduct findStockProduct(String symbol) {
        return stockProductMapper.selectBySymbol(symbol);
    }

    @Override
    public List<StockProduct> findAllProduct(Map<String, Object> params) {
        return stockProductMapper.selectAll(params);
    }

    @Override
    public List<StockQuery> findHistory(Map<String, Object> params) {
        return stockQueryMapper.selectByMap(params);
    }

    @Override
    public int updateAllStock(Market market) {
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.STOCK_DETAIL);
        Map<String, Object> params = new HashMap<>();
        params.put("marketValue", 0);
        params.put("market", market.name());
        List<StockProduct> productList = findAllProduct(params);
        List<StockProduct> list = null;

        int range = 200;
        int a = 0;

        for (int i = 0; i < productList.size(); i = i + range) {
            List<String> symbols = new ArrayList<>();
            list = new ArrayList();
            if (Math.abs(productList.size() - i) > range) {
                productList.subList(i, i + range).forEach(p -> {
                    symbols.add(p.getSymbol());
                });
            } else {
                productList.subList(i, i + Math.abs(productList.size() - i)).forEach(p -> {
                    symbols.add(p.getSymbol());
                });
            }

            /**
             * 请求接口数据
             */
            String bizContent = QuoteParamBuilder.instance()
                    .symbols(symbols)
                    .market(market)
                    .buildJson();
            request.setBizContent(bizContent);
            TigerHttpResponse response = tigerHttpClient.execute(request);
            JSONObject jsonObject = JSON.parseObject(response.getData());
            JSONArray array = jsonObject.getJSONArray("items");
            StockProduct stockProduct = null;

            JSONObject obj = null;
            for (int j = 0; j < array.size(); j++) {
                obj = array.getJSONObject(j);
                stockProduct = new StockProduct();
                stockProduct.setChange(obj.getString("change"));
                stockProduct.setExchange(obj.getString("exchange"));
                stockProduct.setMarket(obj.getString("market"));
                stockProduct.setPreClose(obj.getString("preClose"));
                stockProduct.setPreStart(obj.getString("open"));
                stockProduct.setShares(obj.getLong("shares"));
                stockProduct.setFloatShares(obj.getLong("floatShares"));
                stockProduct.setShortable(obj.getDouble("shortable").intValue());
                stockProduct.setMarketValue((long) (stockProduct.getFloatShares() * Double.parseDouble(stockProduct.getPreStart())));
                stockProduct.setCts(new Date());
                stockProduct.setSymbol(obj.getString("symbol"));
                logger.debug("{}", JSON.toJSONString(obj));
                list.add(stockProduct);
            }
            a = a + updateProduct(list);
            try {
                Thread.sleep(400L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return a;
    }

    @Override
    public List<StockProduct> findFocusProduct(Map<String, Object> params) {
        return stockProductMapper.selectFocusProduct(params);
    }


    @Override
    public int saveStockOptions(StockOptions stockOptions) {
        /**
         * 避免重复数据 行权价、行权期、当前价格、成交量作为唯一Key
         */

        if (stockOptionsMapper.selectByPrimaryKey(stockOptions) != null) {
            return 0;
        }
        logger.info("stockOptions:{}" , JSON.toJSONString(stockOptions));
        return stockOptionsMapper.insert(stockOptions);
    }

    @Override
    public List<StockOptions> getStockOptions(StockOptions stockOptions) {
        return stockOptionsMapper.selectByOptions(stockOptions);
    }


}
