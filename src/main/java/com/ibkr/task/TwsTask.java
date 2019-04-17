package com.ibkr.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.*;

/**
 * Created by caoliang on 2018/11/7
 */


public class TwsTask {
    private Logger logger = LoggerFactory.getLogger(TwsTask.class);


    private final static String BASE_URL = "";

    @Scheduled(cron = "0/10 * * * * ?")
    public void timeLine() {

    }

    /**
     * 默认10个线程,尽量维持队列中不积压排队线程
     */
    ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("common-pool-%d").build();
    ExecutorService threadPoolExecutor = new ThreadPoolExecutor(10, 25, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(1024), factory, new ThreadPoolExecutor.AbortPolicy());


    public void index() {

        Integer start = 0;
        Integer end = 100000000;

        while (true) {
            threadPoolExecutor.execute(new PullThread(start));
            start = start + 1000;
            if (start >= end) {
                return;
            }
            logger.info("totalTask : {} , activeTask : {} , complateTask:{} ,  queueSize : {} , start:{}",
                    ((ThreadPoolExecutor) threadPoolExecutor).getTaskCount(),
                    ((ThreadPoolExecutor) threadPoolExecutor).getActiveCount(),
                    ((ThreadPoolExecutor) threadPoolExecutor).getCompletedTaskCount(),
                    ((ThreadPoolExecutor) threadPoolExecutor).getQueue().size(), start);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class PullThread implements Runnable {
        private Integer start;


        public PullThread(Integer start) {
            this.start = start;
        }

        @Override
        public void run() {


        }
    }


}
