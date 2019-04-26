package com.ibkr.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibkr.entity.StockPrice;
import com.ibkr.entity.StockProduct;
import com.ibkr.entity.StockQuery;
import com.ibkr.service.StockQueryService;
import com.ibkr.util.DateUtil;
import com.tigerbrokers.stock.openapi.client.constant.ApiServiceType;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.request.TigerHttpRequest;
import com.tigerbrokers.stock.openapi.client.https.response.TigerHttpResponse;
import com.tigerbrokers.stock.openapi.client.socket.ApiAuthentication;
import com.tigerbrokers.stock.openapi.client.socket.ApiComposeCallback;
import com.tigerbrokers.stock.openapi.client.socket.WebSocketClient;
import com.tigerbrokers.stock.openapi.client.struct.SubscribedSymbol;
import com.tigerbrokers.stock.openapi.client.struct.enums.KType;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.tigerbrokers.stock.openapi.client.util.builder.QuoteParamBuilder;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static com.tigerbrokers.stock.openapi.client.struct.enums.Subject.*;


/**
 * Created by caoliang on 2018/11/7
 */

@Component
public class TigerTask {
    Logger logger = LoggerFactory.getLogger(TigerTask.class);

    private static final String MARKET_STATUS = "交易中";

    @Autowired
    private TigerHttpClient tigerHttpClient;

    @Autowired
    private StockQueryService stockQueryService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.address}")
    private String wxAddress;

    private BlockingQueue<StockQuery> queue = new LinkedBlockingDeque<>();


    @Value("${tigerId}")
    private String tigerId;
    @Value("${private.key}")
    private String privateKey;
    @Value("${tiger.host}")
    private String tigerHost;

    private List<StockProduct> stockPriceList = null;
    private Map<String, StockProduct> stockProductMap = new HashMap<>();


    //    @Scheduled(cron = "0/50 * * * * ?")
    public void timeLine() throws ParseException {
        logger.debug("task start:{}", new Date());

        StockQuery stockQuery = null;
        for (StockProduct stockProduct : stockPriceList) {
            /**
             * 判断是盘前还是盘后
             */
            List<StockPrice> list = null;

            Date now = DateUtil.format(TimeZone.getTimeZone(stockProduct.getTimeZone()), DateUtil.TIME);

            Date start = stockProduct.getStartTime();
            Date end = stockProduct.getEndTime();

            Date before = stockProduct.getBeforeTime();
            Date after = stockProduct.getAfterTime();
            //当前时间大于开盘时间，且小于结束时间
            if (now.after(start) && now.before(end)) {
                list = getApiFromTiger(stockProduct);
                //小于盘前时间，或大于盘后时间
            } else if ((now.after(before) && now.before(start)) ||
                    (now.after(end) && now.before(after))) {
                list = getApiFromTigerHour(stockProduct);
            }
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            list = list.size() > 10 ? list.subList(list.size() - 10, list.size()) : list;
            for (int i = 0; i < list.size(); i++) {
                stockQuery = new StockQuery();
                stockQuery.setName(stockProduct.getName());
                StockPrice stockPrice = list.get(i);
                stockQuery.setSymbol(stockProduct.getSymbol());
                stockQuery.setAvgPrice(stockPrice.getAvgPrice());
                stockQuery.setUts(DateUtils.parseDate(stockPrice.getTime(), DateUtil.DAT_TIME));
                stockQuery.setPrice(stockPrice.getPrice());
                stockQuery.setVolume(stockPrice.getVolume());
                stockQueryService.saveStockQuery(stockQuery);
            }
        }
        logger.debug("task end:{}", new Date());
    }

    /**
     * 盘中数据
     *
     * @param stockProduct
     * @return
     */
    private List<StockPrice> getApiFromTiger(StockProduct stockProduct) {
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.TIMELINE);
        String bizContent = QuoteParamBuilder.instance()
                .symbol(stockProduct.getSymbol())
                .period(KType.day)
                .market(Market.valueOf(stockProduct.getMarket()))
                .limit(100)
                .buildJson();
        request.setBizContent(bizContent);
        TigerHttpResponse response = tigerHttpClient.execute(request);
        JSONObject jsonObject = JSON.parseObject(response.getData());
        if (jsonObject == null || !jsonObject.containsKey("items")) {
            //logger.error(" sympol:{} , code:{},  format data error : {}", stockProduct.getSymbol(), response.getCode(), response.getMessage());
            return null;
        }
        List<StockPrice> list = new ArrayList<>();
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("items").getJSONObject(0).getJSONArray("items");
        } catch (Exception e) {
            e.printStackTrace();
        }
        StockPrice stockPrice = null;
        for (Object obj : array) {
            stockPrice = new StockPrice();
            stockPrice.setAvgPrice(((JSONObject) obj).getString("avgPrice"));
            stockPrice.setPrice(((JSONObject) obj).getString("price"));
            String time = format(((JSONObject) obj).getDate("time"), TimeZone.getTimeZone(stockProduct.getTimeZone()), DateUtil.DAT_TIME);
            stockPrice.setTime(time);
            stockPrice.setVolume(((JSONObject) obj).getInteger("volume"));
            stockPrice.setCode(stockProduct.getSymbol());
            stockPrice.setTag("盘中");
            list.add(stockPrice);
        }
        return list;
    }

    /**
     * 盘前、盘后数据
     *
     * @param stockProduct
     * @return
     */
    private List<StockPrice> getApiFromTigerHour(StockProduct stockProduct) {
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.HOUR_TRADING_TIMELINE);
        String bizContent = QuoteParamBuilder.instance()
                .symbol(stockProduct.getSymbol())
                .period(KType.min60)
                .market(Market.valueOf(stockProduct.getMarket()))
                .buildJson();
        request.setBizContent(bizContent);

        TigerHttpResponse response = null;
        try {
            response = tigerHttpClient.execute(request);
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
        }
        if (response == null) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(response.getData());
        if (jsonObject == null || !jsonObject.containsKey("items")) {
            //logger.error(" sympol:{} , code:{},  format data error : {}", stockProduct.getSymbol(), response.getCode(), response.getMessage());
            return null;
        }
        List<StockPrice> list = new ArrayList<>();
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("items");
        } catch (Exception e) {
            e.printStackTrace();
        }
        StockPrice stockPrice = null;
        for (Object obj : array) {
            stockPrice = new StockPrice();
            stockPrice.setAvgPrice(((JSONObject) obj).getString("avgPrice"));
            stockPrice.setPrice(((JSONObject) obj).getString("price"));
            String time = format(((JSONObject) obj).getDate("time"), TimeZone.getTimeZone(stockProduct.getTimeZone()), DateUtil.DAT_TIME);
            stockPrice.setTime(time);
            stockPrice.setVolume(((JSONObject) obj).getInteger("volume"));
            stockPrice.setCode(stockProduct.getSymbol());
            list.add(stockPrice);
        }
        return list;
    }

    public String format(Date date, TimeZone timeZone, String parrten) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(parrten);
        sdf.setTimeZone(timeZone);
        return sdf.format(date);
    }

    public Date format(Long timestamp, TimeZone timeZone) {
        if (timestamp == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DAT_TIME);
        simpleDateFormat.setTimeZone(timeZone);
        String d = DateFormatUtils.format(timestamp, DateUtil.DAT_TIME, timeZone);
        try {
            return simpleDateFormat.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    @PostConstruct
    public void callBack() {
        //消费线程
        new Thread(new ConsumerThread()).start();

        //初始化stock数据
        Map<String, Object> params = new HashMap<>();
        params.put("marketValue", 20000000L);
        stockPriceList = stockQueryService.findFocusProduct(params);
        for (StockProduct stockProduct : stockPriceList) {
            stockProductMap.put(stockProduct.getSymbol(), stockProduct);
        }

        //websocket订阅
        wsCallBack();
    }


    /**
     * ws订阅数据
     */
    @Scheduled(cron = "0 0 9,13,16 * * ?")
    private void wsCallBack() {
        ApiComposeCallback callback = new ApiComposeCallback() {
            @Override
            public void orderStatusChange(JSONObject jsonObject) {
                logger.info("orderStatusChange : {} ", jsonObject);
            }

            @Override
            public void positionChange(JSONObject jsonObject) {
                logger.info("positionChange : {} ", jsonObject);
                /**
                 * 持仓变化，推送到微信filehelp
                 */
                restTemplate.postForEntity(wxAddress + "positionChange", jsonObject, String.class);
            }

            @Override
            public void assetChange(JSONObject jsonObject) {
                logger.info("assetChange : {} ", jsonObject);
            }

            @Override
            public void quoteChange(JSONObject jsonObject) {
                logger.info("quoteChange : {} ", jsonObject);
                if (!MARKET_STATUS.equals(jsonObject.getString("marketStatus"))) {
                    return;
                }
                StockProduct stockProduct = stockProductMap.get(jsonObject.getString("symbol"));
                if (stockProduct == null) {
                    return;
                }
                StockQuery stockQuery = new StockQuery();
                stockQuery.setSymbol(stockProduct.getSymbol());
                stockQuery.setVolume(jsonObject.getInteger("volume"));
                stockQuery.setPrice(jsonObject.getString("latestPrice"));
                if (jsonObject.containsKey("mi")) {
                    stockQuery.setAvgPrice(jsonObject.getJSONObject("mi").getString("a"));
                }
                Date time = format(jsonObject.getLong("timestamp"), TimeZone.getTimeZone(stockProduct.getTimeZone()));
                stockQuery.setUts(time);
                stockQuery.setName(stockProduct.getName());
                queue.add(stockQuery);
            }

            @Override
            public void optionChange(JSONObject jsonObject) {
                logger.info("optionChange : {} ", jsonObject);
            }

            @Override
            public void subscribeEnd(JSONObject jsonObject) {
                logger.info("subscribeEnd : {} ", jsonObject);
            }

            @Override
            public void cancelSubscribeEnd(JSONObject jsonObject) {
                logger.info("cancelSubscribeEnd : {} ", jsonObject);
            }

            @Override
            public void getSubscribedSymbolEnd(SubscribedSymbol subscribedSymbol) {
                logger.info("getSubscribedSymbolEnd : {} ", subscribedSymbol);
            }

            @Override
            public void client(WebSocketClient client) {
                logger.info("client : {} ", client);
            }

            @Override
            public void error(String errorMsg) {
                logger.error("error : {} ", errorMsg);
            }

            @Override
            public void error(int id, int errorCode, String errorMsg) {
                logger.error("errorCode : {} , errorMsg:{} ", errorCode, errorMsg);
            }

            @Override
            public void connectionClosed() {
                logger.info("connectionClosed =======");
            }

            @Override
            public void connectAck() {
                logger.info("connectAck ========= ");
            }
        };

        ApiAuthentication apiAuthentication = ApiAuthentication.build(tigerId, privateKey);
        WebSocketClient client = new WebSocketClient("wss://openapi.itiger.com:8883", apiAuthentication, callback);
        client.connect();


        Set<String> symbols = new HashSet<>();
        stockPriceList.forEach(p -> {
            symbols.add(p.getSymbol());
        });
        /**
         * 订阅行情数据接口
         */
        client.subscribeQuote(symbols);

        /**
         * 订单、资产、持仓
         */
        client.subscribe(OrderStatus);
        client.subscribe(Asset);
        client.subscribe(Position);


        callback.client(client);
    }


    /**
     * 消费线程
     */
    class ConsumerThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                StockQuery stockQuery = null;
                try {
                    stockQuery = queue.take();
                    if (stockQuery != null) {
                        stockQueryService.saveStockQuery(stockQuery);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
