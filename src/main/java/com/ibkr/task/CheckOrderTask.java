package com.ibkr.task;

import com.ibkr.entity.response.PendingOrder;
import com.ibkr.service.AccountService;
import com.ibkr.util.DateUtil;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by caoliang on 2019/2/26
 */
@Component
public class CheckOrderTask {
    private Logger logger = LoggerFactory.getLogger(CheckOrderTask.class);

    private String startTime = "23:30:00";
    private String endTime = "02:30:00";


    @Autowired
    private AccountService accountService;

    private void checkOrder() throws ParseException {
        Date start = null;
        start = DateUtils.parseDate(startTime, DateUtil.TIME);
        Date end = DateUtils.parseDate(endTime, DateUtil.TIME);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.TIME);
        String s = sdf.format(date);
        Date now = DateUtils.parseDate(s, DateUtil.TIME);
        if (now.before(start) && now.after(end)) {
            return;
        }
        List<PendingOrder> orderList = accountService.getPendingOrders(SecType.OPT);
        for (PendingOrder order : orderList) {
            logger.info("");
        }
    }

}
