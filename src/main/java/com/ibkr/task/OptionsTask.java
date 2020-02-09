package com.ibkr.task;

import com.alibaba.fastjson.JSON;
import com.ibkr.entity.MessageQueue;
import com.ibkr.entity.OptionDetail;
import com.ibkr.entity.StockOptions;
import com.ibkr.service.StockApiService;
import com.ibkr.service.StockQueryService;
import com.ibkr.util.DateUtil;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionBriefItem;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionRealTimeQuoteGroup;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainModel;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionChainQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionChainResponse;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.*;

/**
 * Created by caoliang on 2019/2/21
 * <p>
 * 美股的期权
 */
@Component
public class OptionsTask {
    private Logger logger = LoggerFactory.getLogger(OptionsTask.class);


    /**
     * 关注的期权链
     */
    private String[] symbols = new String[]{"SPY", "MSFT", "AAPL", "GOOG", "AMZN", "FB", "BABA", "NFLX", "NVDA", "AMD", "BA", "TSLA"};


    private final static String TIME_ZONE = "GMT-5";

    @Autowired
    private TigerHttpClient client;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.address}")
    private String wxAddress;


    @Autowired
    private StockQueryService stockQueryService;

    @Autowired
    private StockApiService stockApiService;


    private Boolean simple = false;

    private String startTime = "08:40:00";
    private String endTime = "15:30:00";


    /**
     * 每10秒获取最大交易量的10支期权作为历史K线
     *
     * @throws ParseException
     */
    @Scheduled(cron = "0 0/2 * * * ?")
    public void gatHistory() throws ParseException {
        optionChain("SPY", startTime, endTime, 10);
    }

    /**
     * 每5分钟获取交易量最大的3条推送到微信
     *
     * @throws ParseException
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void sendWechat() throws ParseException {
        optionChain("SPY", startTime, endTime, 3);
    }

    /**
     * 采用纽约时间
     *
     * @param size
     */
    public void optionChain(String symbol, String startTime, String endTime, Integer size) throws ParseException {
        Date start = DateUtils.parseDate(startTime, DateUtil.TIME);
        Date end = DateUtils.parseDate(endTime, DateUtil.TIME);
        Date now = DateUtil.format(TimeZone.getTimeZone(TIME_ZONE), DateUtil.TIME);
        if (now.before(start) || now.after(end)) {
            return;
        }
        String stringDay = checkoutWeek();
        if (stringDay == null) {
            return;
        }

        List<OptionDetail> optionDetails = new ArrayList<>();
        OptionChainModel model = new OptionChainModel();
        model.setSymbol(symbol);
        /**
         * 周一 周三 周五
         */
        model.setExpiry(stringDay);
        OptionChainResponse response = client.execute(OptionChainQueryRequest.of(model));
        if (response.isSuccess() && !CollectionUtils.isEmpty(response.getOptionChainItems()) && !CollectionUtils.isEmpty(response.getOptionChainItems().get(0).getItems())) {
            for (OptionRealTimeQuoteGroup optionReal : response.getOptionChainItems().get(0).getItems()) {
                OptionDetail call = new OptionDetail();
                //call.setSymbol(symbol);
                call.setRight(optionReal.getCall().getRight().toUpperCase());
                call.setVolume(optionReal.getCall().getVolume());
                call.setStrike(optionReal.getCall().getStrike());
                call.setLatestPrice(optionReal.getCall().getLatestPrice());
                call.setBidPrice(optionReal.getCall().getBidPrice());
                call.setBidSize(optionReal.getCall().getBidSize());
                call.setAskPrice(optionReal.getCall().getAskPrice());
                call.setAskSize(optionReal.getCall().getAskSize());
                optionDetails.add(call);

                OptionDetail put = new OptionDetail();
                //put.setSymbol(symbol);
                put.setRight(optionReal.getPut().getRight().toUpperCase());
                put.setVolume(optionReal.getPut().getVolume());
                put.setStrike(optionReal.getPut().getStrike());
                put.setLatestPrice(optionReal.getPut().getLatestPrice());
                put.setBidPrice(optionReal.getPut().getBidPrice());
                put.setBidSize(optionReal.getPut().getBidSize());
                put.setAskPrice(optionReal.getPut().getAskPrice());
                put.setAskSize(optionReal.getPut().getAskSize());
                optionDetails.add(put);
            }
        } else {
            logger.error("response error:" + response.getMessage());
            return;
        }
        if (CollectionUtils.isEmpty(optionDetails)) {
            return;
        }
        Collections.sort(optionDetails);
        optionDetails = optionDetails.size() > size ? optionDetails.subList(0, size) : optionDetails;
        /**
         *
         */
        for (OptionDetail optionDetail : optionDetails) {
            logger.info("optionDetail :{}", JSON.toJSONString(optionDetail));
            if (size < 5) {
                //发送到微信
                MessageQueue messageQueue = new MessageQueue();
                messageQueue.setId(System.currentTimeMillis());
                messageQueue.setOption("option");
                messageQueue.setDate(new Date());
                OptionBriefItem optionBriefItem = stockApiService.getOptionDetail(symbol, optionDetail.getRight(), optionDetail.getStrike(), model.getExpiry());
                if (optionBriefItem != null) {
                    optionDetail.setLow(optionBriefItem.getLow());
                    optionDetail.setHigh(optionBriefItem.getHigh());
                }
                optionDetail.setTime(stringDay);
                if (simple) {
                    optionDetail.setAskSize(null);
                    optionDetail.setAskPrice(null);
                    optionDetail.setBidSize(null);
                    optionDetail.setBidPrice(null);
                }
                messageQueue.setContent(JSON.toJSONString(optionDetail));
                restTemplate.postForEntity(wxAddress + "sendOption", messageQueue, String.class);
            } else {
                /**
                 * 获取期权逐笔成交数据，再保存到数据库
                 */
                StockOptions stockOptions = new StockOptions();
                stockOptions.setExpiry(stringDay);
                stockOptions.setSymbol(symbol);
                stockOptions.setRight(optionDetail.getRight());
                stockOptions.setStrike(optionDetail.getStrike());
                List<StockOptions> list = stockApiService.getStockOptionsItems(stockOptions);
                if (CollectionUtils.isEmpty(list)) {
                    logger.warn("获取逐笔成交数据为空:{}", stockOptions.toString());
                    continue;
                }
                for (StockOptions options : list.subList(list.size() - 10, list.size())) {
                    stockQueryService.saveStockOptions(options);
                }
            }
        }
        simple = !simple;
    }


    /**
     * 判断时间
     *
     * @return
     */
    private String checkoutWeek() {
        Date now = DateUtil.format(TimeZone.getTimeZone(TIME_ZONE), DateUtil.DAT_TIME);
        Integer week = DateUtil.getWeek(now);

        /**
         * 周末不开盘
         */
        if (week == 0 || week == 6) {
            return null;
        }

        /**
         * 周二和周四没有末日，需要日期加1
         */
        if (week == 2 || week == 4) {
            return DateUtil.getStringDay(now, 1);
        } else {
            return DateUtil.getStringDay(now, 0);
        }
    }
}
