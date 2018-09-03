package com.bdgx.resolvers;

import com.bdgx.MyServlet;
import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class Subscription implements GraphQLSubscriptionResolver {
    private static final Logger log = LoggerFactory.getLogger(Subscription.class);
    private final Object lock = new Object();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final Map<String,ConcurrentHashMap<String,FluxSink<?>>> topicSessionCache = new ConcurrentHashMap<>();

    /**
     * kafka通过groupId不同来实现消息分发，这边通过主机名称来区分
     * @param payload 消息
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @KafkaListener(topics = "kafka-testing",groupId = "#{T(java.net.InetAddress).getLocalHost().getHostName()}")
    public void consume(@Payload byte[] payload) throws IOException {
        ConcurrentHashMap<String,FluxSink<?>> sinkConcurrentHashMap = topicSessionCache.get("kafka-testing");
        if (!CollectionUtils.isEmpty(sinkConcurrentHashMap)) {
            for (Map.Entry<String,FluxSink<?>> entry : sinkConcurrentHashMap.entrySet()) {
                StockPriceUpdate update = objectMapper.readValue(payload, StockPriceUpdate.class);
                if (update == null) {
                    return;
                }
                System.out.println(update.toString());
                FluxSink<Object> sink = (FluxSink<Object>) entry.getValue();
                if (MyServlet.closeSession.contains(entry.getKey())){
                    sinkConcurrentHashMap.remove(entry.getKey());
                }else {
                    sink.next(update);
                }
            }
        }
    }
    Publisher<StockPriceUpdate> stockFluxQuotes(List<String> stockCodes, DataFetchingEnvironment environment) {
        return Flux.<StockPriceUpdate>create(sink -> {
            put("kafka-testing",environment,sink);
        }).filter(update -> stockCodes.contains(update.getStockCode()));
    }

    public void put(String key,DataFetchingEnvironment environment,FluxSink<?> sink){
        ConcurrentHashMap<String,FluxSink<?>> concurrentHashMap = topicSessionCache.get(key);
        if (concurrentHashMap ==  null){
            synchronized (lock) {
                concurrentHashMap = new ConcurrentHashMap<>();
            }
        }
        concurrentHashMap.put(environment.getExecutionId().toString(),sink);
        topicSessionCache.put(key,concurrentHashMap);
    }
}
