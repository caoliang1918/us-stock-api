package com.ikbr;

import com.ibkr.IbkrApiApplication;
import com.ibkr.service.StockQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IbkrApiApplication.class)
public class DemoApplicationTests {

    @Autowired
    private StockQueryService stockQueryService;

    @Test
    public void contextLoads() {

        //初始化stock数据
        Map<String, Object> params = new HashMap<>();
        params.put("marketValue", 20000000L);
        stockQueryService.findFocusProduct(params).forEach(product -> {
            System.out.println(product.toString());
        });
    }

}
