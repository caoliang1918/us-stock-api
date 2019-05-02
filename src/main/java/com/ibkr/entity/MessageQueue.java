package com.ibkr.entity;


import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by caoliang on 2019/1/14
 * <p>
 * 延迟消息队列
 */
public class MessageQueue implements Serializable, Delayed {

    /**
     * 消息id
     */
    private Long id;
    /**
     * 发送时间
     */
    private Date date;

    /**
     * 消息体
     */
    private String content;

    /**
     * create/update
     */
    private String option;

    public MessageQueue() {
    }

    public MessageQueue(Long i, String test) {
        this.id = i;
        this.content = test;
        this.date = new Date();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return "MessageQueue{" +
                "id=" + id +
                ", date=" + date +
                ", content='" + content + '\'' +
                ", option='" + option + '\'' +
                '}';
    }

    @Override
    public long getDelay(TimeUnit unit) {
        //延迟20秒
        return unit.convert(date.getTime() + 60000 - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    public static void main(String[] args) {
        DelayQueue<MessageQueue> delayQueue = new DelayQueue<MessageQueue>();

        //生产者
        producer(delayQueue);

        //消费者
        consumer(delayQueue);

        while (true) {
            try {
                TimeUnit.HOURS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


    private static void producer(final DelayQueue<MessageQueue> delayQueue) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    MessageQueue element = new MessageQueue(System.currentTimeMillis(), RandomStringUtils.randomAlphabetic(32));
                    delayQueue.offer(element);
                    System.out.println("delayQueue size:" + delayQueue.size());
                }
            }
        }).start();


    }

    private static void consumer(final DelayQueue<MessageQueue> delayQueue) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    MessageQueue element = null;
                    try {
                        // 没有满足延时的元素 用poll返回 null
                        // 没有满足延时的元素 用take会阻塞
                        element = delayQueue.take();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(System.currentTimeMillis() + "---" + element);
                }
            }
        }).start();
    }
}
