package com.ibkr.web;

import com.ibkr.entity.CommonResponse;
import com.ibkr.entity.StockOptions;
import com.ibkr.service.StockApiService;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by caoliang on 2019/2/28
 */

@RestController
@RequestMapping("options")
public class OptionsController {
    private Logger logger = LoggerFactory.getLogger(OptionsController.class);

    @Autowired
    private TigerHttpClient tigerHttpClient;

    @Autowired
    private StockApiService stockApiService;

    @GetMapping("getOptions")
    public CommonResponse getOptions(String right, String strike, String expiry) {
        StockOptions stockOptions = new StockOptions();
        stockOptions.setExpiry(expiry);
        stockOptions.setSymbol("SPY");
        stockOptions.setRight(right);
        stockOptions.setStrike(strike);
        List<StockOptions> list = stockApiService.getStockOptionsItems(stockOptions);
        return new CommonResponse(list.subList(list.size() - 10, list.size()));
    }
}
