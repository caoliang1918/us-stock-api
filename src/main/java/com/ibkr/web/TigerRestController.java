package com.ibkr.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibkr.entity.*;
import com.ibkr.service.StockQueryService;
import com.tigerbrokers.stock.openapi.client.constant.ApiServiceType;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainModel;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionCommonModel;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionKlineModel;
import com.tigerbrokers.stock.openapi.client.https.request.TigerHttpRequest;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionBriefQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionChainQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionKlineQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.response.TigerHttpResponse;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionBriefResponse;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionChainResponse;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionKlineResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.KType;
import com.tigerbrokers.stock.openapi.client.struct.enums.Language;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import com.tigerbrokers.stock.openapi.client.util.builder.AccountParamBuilder;
import com.tigerbrokers.stock.openapi.client.util.builder.QuoteParamBuilder;
import com.zhongweixian.excel.ExcelExportUtil;
import com.zhongweixian.excel.entity.ExportParams;
import com.zhongweixian.excel.entity.enmus.ExcelType;
import com.zhongweixian.excel.entity.params.ExcelExportEntity;
import com.zhongweixian.excel.view.AbstractExcelView;
import com.zhongweixian.excel.view.BaseView;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author : caoliang1918@gmail.com
 * @date :   2018/11/4 0:39
 */
@RestController
@RequestMapping("tiger")
public class TigerRestController {
    private Logger logger = LoggerFactory.getLogger(TigerRestController.class);

    @Value("${account}")
    private String account;

    @Autowired
    private TigerHttpClient tigerHttpClient;

    @Autowired
    private StockQueryService stockQueryService;


    @GetMapping("timeLineJson")
    public CommonResponse timeLineJson(@RequestParam String code) {
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.TIMELINE);
        String bizContent = QuoteParamBuilder.instance()
                .symbol(code)
                .period(KType.day)
                .market(Market.HK)
                .limit(100)
                .buildJson();
        request.setBizContent(bizContent);
        TigerHttpResponse response = tigerHttpClient.execute(request);
        JSONObject jsonObject = JSON.parseObject(response.getData());
        if (!jsonObject.containsKey("items")) {
            return new CommonResponse();
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
            stockPrice.setTime(DateFormatUtils.format(((JSONObject) obj).getDate("time"), "HH:mm:ss"));
            stockPrice.setVolume(((JSONObject) obj).getInteger("volume"));
            stockPrice.setCode(code);
            list.add(stockPrice);
        }
        Collections.sort(list);
        return new CommonResponse(list);
    }

    @GetMapping("timeLine")
    public ModelAndView timeLine(@RequestParam String code, String x) {
        ModelAndView mav = new ModelAndView("timeLine");
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", code);
        params.put("limit", 100);
        List<StockQuery> list = stockQueryService.findHistory(params);
        list.sort(Comparator.comparing(StockQuery::getUts));
        StringBuffer xAxis = new StringBuffer();
        StringBuffer seriesData = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            xAxis.append(DateFormatUtils.format(list.get(i).getUts(), "HH:mm:ss"));
            seriesData.append(new BigDecimal(list.get(i).getPrice()));
            if (i < list.size() - 1) {
                xAxis.append(",");
                seriesData.append(",");
            }
        }
        mav.addObject("code", code);
        mav.addObject("name", code);
        mav.addObject("xAxis", xAxis.toString());
        mav.addObject("seriesData", seriesData.toString());
        return mav;
    }


    @GetMapping("position")
    public CommonResponse position() {
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.POSITIONS);
        String bizContent = AccountParamBuilder.instance()
                .account(account)
                .secType(SecType.STK)
                .buildJson();

        request.setBizContent(bizContent);
        TigerHttpResponse response = tigerHttpClient.execute(request);
        return new CommonResponse(response.getData());
    }

    @GetMapping("getAllSymbols")
    public CommonResponse getAllSymbols(Market market) {
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.ALL_SYMBOL_NAMES);

        String bizContent = QuoteParamBuilder.instance()
                .market(market)
                .language(Language.zh_CN)
                .buildJson();
        request.setBizContent(bizContent);
        TigerHttpResponse response = tigerHttpClient.execute(request);
        JSONObject jsonObject = JSON.parseObject(response.getData());
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        List<StockProduct> list = new ArrayList<>();
        for (Object obj : jsonArray) {
            JSONObject json = (JSONObject) obj;
            StockProduct product = new StockProduct();
            product.setName(json.getString("name"));
            product.setSymbol(json.getString("symbol"));
            product.setSecType("STK");
            product.setMarket(market.name());
            product.setTimeZone("GMT+8");
            product.setUnit("HKD");
            product.setStatus(1);

            StockProduct p = stockQueryService.findStockProduct(product.getSymbol());
            if (p == null) {
                stockQueryService.saveStockProduct(product);
                list.add(product);
            }
        }
        return new CommonResponse(list);
    }

    @PutMapping("stockInfo")
    public CommonResponse updateStack(Market market) {
        return new CommonResponse(stockQueryService.updateAllStock(market));
    }

    @GetMapping("stockInfo")
    public CommonResponse stackInfo(String code, Market market) {
        TigerHttpRequest request = new TigerHttpRequest(ApiServiceType.STOCK_DETAIL);
        List<String> symbols = new ArrayList<>();
        symbols.add(code);
        String bizContent = QuoteParamBuilder.instance()
                .symbols(symbols)
                .market(market)
                .buildJson();
        request.setBizContent(bizContent);
        TigerHttpResponse response = tigerHttpClient.execute(request);
        return new CommonResponse(response.getData());
    }

    @GetMapping("simpleMapData")
    public void simpleMapData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ExcelExportEntity> entityList = new ArrayList<ExcelExportEntity>();
        entityList.add(new ExcelExportEntity("股票代码", "symbol"));
        entityList.add(new ExcelExportEntity("股票名称", "name"));
        entityList.add(new ExcelExportEntity("交易所", "exchange"));
        entityList.add(new ExcelExportEntity("发行地区", "market"));
        entityList.add(new ExcelExportEntity("时区", "timeZone"));
        entityList.add(new ExcelExportEntity("盘前时间", "beforeTime"));
        entityList.add(new ExcelExportEntity("开盘时间", "startTime"));
        entityList.add(new ExcelExportEntity("结束时间", "endTime"));
        entityList.add(new ExcelExportEntity("盘后时间", "afterTime"));
        entityList.add(new ExcelExportEntity("昨日收盘价", "preClose"));
        entityList.add(new ExcelExportEntity("今日开盘价", "preStart"));
        entityList.add(new ExcelExportEntity("涨跌幅度", "change"));
        entityList.add(new ExcelExportEntity("流通股本", "floatShares"));
        entityList.add(new ExcelExportEntity("总股本", "shares"));
        entityList.add(new ExcelExportEntity("市值", "marketValue"));
        entityList.add(new ExcelExportEntity("可做空", "shortable"));

        Map<String, Object> params = new HashMap<>();
        params.put("marketValue", 5000000000L);
        List<StockProduct> productList = stockQueryService.findAllProduct(params);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        StockProduct stockProduct;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        for (int i = 0; i < productList.size(); i++) {
            map = new HashMap<String, Object>();
            stockProduct = productList.get(i);
            if (stockProduct.getFloatShares() == 0) {
                continue;
            }
            map.put("symbol", stockProduct.getSymbol());
            map.put("name", stockProduct.getName());
            map.put("exchange", stockProduct.getExchange());
            map.put("market", stockProduct.getMarket());
            map.put("timeZone", stockProduct.getTimeZone());
            map.put("beforeTime", simpleDateFormat.format(stockProduct.getBeforeTime()));
            map.put("startTime", simpleDateFormat.format(stockProduct.getStartTime()));
            map.put("endTime", simpleDateFormat.format(stockProduct.getEndTime()));
            map.put("afterTime", simpleDateFormat.format(stockProduct.getAfterTime()));
            map.put("preClose", stockProduct.getPreClose());
            map.put("preStart", stockProduct.getPreStart());
            map.put("change", stockProduct.getChange());
            map.put("floatShares", stockProduct.getFloatShares());
            map.put("shares", stockProduct.getShares());
            map.put("marketValue", stockProduct.getMarketValue());
            map.put("shortable", stockProduct.getShortable());
            list.add(map);
        }

        ExportParams exportParams = new ExportParams("sheet1", ExcelType.XSSF);
        exportParams.setFreezeRow(1);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entityList, list);


        String fileName = "美股实盘数据";
        if (workbook instanceof HSSFWorkbook) {
            fileName += AbstractExcelView.HSSF;
        } else {
            fileName += AbstractExcelView.XSSF;
        }
        if (BaseView.isIE(request)) {
            fileName = java.net.URLEncoder.encode(fileName, "UTF8");
        } else {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        }
        response.setHeader("content-disposition", "attachment;filename=" + fileName);
        response.setContentType(AbstractExcelView.CONTENT_TYPE);
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
    }

    @GetMapping("optionsChainList")
    public CommonResponse optionsChainList(String symbol, String expiry) {
        OptionChainModel model = new OptionChainModel();
        model.setSymbol("SPY");
        model.setExpiry(expiry);
        OptionChainResponse response = tigerHttpClient.execute(OptionChainQueryRequest.of(model));
        return new CommonResponse(response);
    }

    @GetMapping("optionsChainDetail")
    public CommonResponse optionsChainDetail(String right, String strike, String expiry) {
        OptionCommonModel model = new OptionCommonModel();
        model.setSymbol("SPY");
        model.setRight(right.toUpperCase());
        model.setStrike(strike);
        model.setExpiry(expiry);
        OptionBriefResponse response = tigerHttpClient.execute(OptionBriefQueryRequest.of(model));
        if (response.isSuccess()) {
            return new CommonResponse(response.getOptionBriefItems().toArray());
        } else {
            System.out.println("response error:" + response.getMessage());
            return null;
        }
    }

    @GetMapping("optionsChainLine")
    public ModelAndView optionsChainLine(String right, String strike, String expiry) {
        ModelAndView mav = new ModelAndView("timeLine");
        StockOptions stockOptions = new StockOptions();
        stockOptions.setSymbol("SPY");
        stockOptions.setExpiry(expiry);
        stockOptions.setStrike(strike);
        stockOptions.setRight(right);
        StringBuffer xAxis = new StringBuffer();
        StringBuffer seriesData = new StringBuffer();
        List<StockOptions> list = stockQueryService.getStockOptions(stockOptions);
        for (int i = 0; i < list.size(); i++) {
            seriesData.append(new BigDecimal(list.get(i).getPrice()));
            xAxis.append(DateFormatUtils.format(list.get(i).getUts(), "HH:mm:ss"));
            if (i < list.size() - 1) {
                seriesData.append(",");
                xAxis.append(",");
            }
        }


        mav.addObject("code", "SPY");
        mav.addObject("name", "SPY-" + expiry + "-" + right);
        mav.addObject("xAxis", xAxis.toString());
        mav.addObject("seriesData", seriesData.toString());
        return mav;

    }

    @GetMapping("shortStock")
    public CommonResponse shortStock(String symbol) {


        return new CommonResponse(stockQueryService.findShortStock("BABA"));

    }

}
