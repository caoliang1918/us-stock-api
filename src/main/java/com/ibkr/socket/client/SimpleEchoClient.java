package com.ibkr.socket.client;

import com.ibkr.queue.Consumer;
import com.ibkr.queue.QueueService;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Component
public class SimpleEchoClient {
    private Logger logger = LoggerFactory.getLogger(SimpleEchoClient.class);

    private String host = "wss://realtime-prod.wallstreetcn.com/ws";
    private String startMessage = "{\"command\":\"ENTER_CHANNEL\",\"data\":{\"chann_name\":\"live\",\"cursor\":\"3484360\"}}";

    private static Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

    @Autowired
    public QueueService queueService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.address}")
    private String wxAddress;


    @PostConstruct
    public void connect() {
        WebSocketClient client = new WebSocketClient();
        try {
            client.start();
            URI uri = new URI(host);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            SimpleEchoSocket simpleEchoSocket = new SimpleEchoSocket(host, startMessage, queueService, wxAddress);
            Future<Session> sessionFuture = client.connect(simpleEchoSocket, uri, request);
            logger.info("Connecting to : {}", uri);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        new Thread(new Consumer(queueService, restTemplate, wxAddress)).start();
    }

    /**
     * @param username
     */
    public void close(String username) {
        Session session = sessionMap.get(username);
        if (session != null && session.isOpen()) {
            try {
                session.getRemote().sendString("{\"cmd\": \"logout\"}");
                session.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, Session> getSessionMap() {
        return sessionMap;
    }

}
