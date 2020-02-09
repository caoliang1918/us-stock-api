package com.ibkr.web;

import com.ibkr.entity.CommonResponse;
import com.ibkr.entity.StockOptions;
import com.ibkr.service.StockApiService;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionExpirationQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionExpirationResponse;
import com.zhongweixian.excel.ExcelExportUtil;
import com.zhongweixian.excel.entity.ExportParams;
import com.zhongweixian.excel.entity.enmus.ExcelType;
import com.zhongweixian.excel.entity.params.ExcelExportEntity;
import com.zhongweixian.excel.view.AbstractExcelView;
import com.zhongweixian.excel.view.BaseView;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by caoliang on 2019/2/28
 * 期权操作相关
 */
@RestController
@RequestMapping("options")
public class OptionsController {
    private Logger logger = LoggerFactory.getLogger(OptionsController.class);

    @Autowired
    private TigerHttpClient tigerHttpClient;

    @Autowired
    private StockApiService stockApiService;

    /**
     * 获取期权逐笔成交
     *
     * @param right
     * @param strike
     * @param expiry
     * @return
     */
    @GetMapping("getOptions")
    public CommonResponse getOptions(String right, String strike, String expiry) {
        StockOptions stockOptions = new StockOptions();
        stockOptions.setExpiry(expiry);
        stockOptions.setSymbol("SPY");
        stockOptions.setRight(right);
        stockOptions.setStrike(strike);
        List<StockOptions> list = stockApiService.getStockOptionsItems(stockOptions);
        return new CommonResponse(list);
    }


    /**
     * "SPY", "MSFT", "AAPL", "GOOG", "AMZN", "FB", "BABA", "NFLX", "NVDA", "AMD", "BA", "TSLA"
     *
     * @param symbol
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("optionChain")
    public void optionChain(@RequestParam("symbol") String symbol, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> optionTimeQuoteList = stockApiService.getOptionExpirations(symbol);
        if (CollectionUtils.isEmpty(optionTimeQuoteList)) {
            return;
        }

        List<ExcelExportEntity> entityList = new ArrayList<ExcelExportEntity>();
        entityList.add(new ExcelExportEntity("股票代码", "symbol"));
        entityList.add(new ExcelExportEntity("行权期", "expiry"));
        entityList.add(new ExcelExportEntity("期权方向", "right"));
        entityList.add(new ExcelExportEntity("最新价格", "latestPrice"));
        entityList.add(new ExcelExportEntity("行权价格", "strike"));
        entityList.add(new ExcelExportEntity("成交量", "volume"));
        entityList.add(new ExcelExportEntity("未平仓数", "openInterest"));
        String fileName = "美股[" + symbol + "]期权链";

        ExportParams exportParams = new ExportParams(symbol, ExcelType.XSSF);
        exportParams.setFreezeRow(1);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entityList, optionTimeQuoteList);

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

}
