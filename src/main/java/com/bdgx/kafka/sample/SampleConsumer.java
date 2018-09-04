package com.bdgx.kafka.sample;

import com.bdgx.resolvers.StockPriceUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.IOException;

/**
 * @author hai
 * description
 * email hilin2333@gmail.com
 * date 2018/9/4 4:09 PM
 */
@Component
public class SampleConsumer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private FluxSink<StockPriceUpdate> fluxSink;
    private final Flux<StockPriceUpdate> flux;

    public SampleConsumer() {
        this.flux = Flux.<StockPriceUpdate>create(emitter -> fluxSink = emitter).log().doOnSubscribe(s -> System.out.println("又订阅了一个"));
    }

    /**
     * kafka通过groupId不同来实现消息分发，这边通过主机名称来区分
     *
     * @param payload 消息
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @KafkaListener(topics = "kafka-testing", groupId = "#{T(java.net.InetAddress).getLocalHost().getHostName()}")
    public void consume(@Payload byte[] payload) throws IOException {
        if (fluxSink != null) {
            StockPriceUpdate update = objectMapper.readValue(payload, StockPriceUpdate.class);
            fluxSink.next(update);
        }
    }

    public Flux<StockPriceUpdate> getFlux() {
        return flux;
    }
}
