package com.bdgx.kafka.sample;

import com.bdgx.resolvers.StockPriceUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.io.IOException;

/**
 * @author hai
 * description 消费端
 * email hilin2333@gmail.com
 * date 2018/9/4 4:09 PM
 */
public class SampleConsumer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private UnicastProcessor<StockPriceUpdate> hotSource;
    private final Flux<StockPriceUpdate> flux;

    public SampleConsumer() {
        UnicastProcessor<StockPriceUpdate> hotSource = UnicastProcessor.create();
        this.hotSource = hotSource;
        this.flux = hotSource.publish().autoConnect().log().doOnSubscribe(s -> System.out.println("又订阅了一个"));
    }

    /**
     * kafka通过groupId不同来实现消息分发，这边通过主机名称来区分
     *
     * @param payload 消息
     * @throws IOException
     */
    @KafkaListener(topics = "kafka-testing", groupId = "#{T(java.net.InetAddress).getLocalHost().getHostName()}")
    public void consume(@Payload byte[] payload) throws IOException {
            StockPriceUpdate update = objectMapper.readValue(payload, StockPriceUpdate.class);
            hotSource.onNext(update);
    }

    public Flux<StockPriceUpdate> getFlux() {
        return flux;
    }
}
