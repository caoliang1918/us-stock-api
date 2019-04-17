package com.ibkr.service;

import com.ibkr.entity.OptionDetail;
import com.ibkr.entity.StockOptions;
import com.ibkr.entity.StockProduct;
import com.ibkr.entity.StockQuery;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionBriefItem;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;

import java.util.List;
import java.util.Map;

/**
 * Created by caoliang on 2018/11/7
 */
public interface StockQueryService {

    /**
     * @param stockQuery
     * @return
     */
    int saveStockQuery(StockQuery stockQuery);

    /**
     * @param stockProduct
     * @return
     */
    int saveStockProduct(StockProduct stockProduct);

    /**
     * @param stockProducts
     * @return
     */
    int updateProduct(List<StockProduct> stockProducts);

    /**
     * @param symbol
     * @return
     */
    StockProduct findStockProduct(String symbol);

    /**
     * @return
     */
    List<StockProduct> findAllProduct(Map<String, Object> params);


    List<StockQuery> findHistory(Map<String, Object> params);


    int updateAllStock(Market market);


    List<StockProduct> findFocusProduct(Map<String, Object> params);


    /**
     * 保存期权数据
     *
     * @param stockOptions
     * @return
     */
    int saveStockOptions(StockOptions stockOptions);


    /**
     * 获取期权列表
     *
     * @param stockOptions
     * @return
     */
    List<StockOptions> getStockOptions(StockOptions stockOptions);
}
