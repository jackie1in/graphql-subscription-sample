package com.bdgx.kafka.scenario;

import com.bdgx.kafka.serdes.StockPriceUpdateSerDes;
import com.bdgx.resolvers.StockPriceUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @author hai
 * description
 * email hilin2333@gmail.com
 * date 2018/8/5 4:27 PM
 */
public class StockPriceUpdateSink extends AbstractScenario<StockPriceUpdate> {
    private final KafkaProperties kafkaProperties;
    public StockPriceUpdateSink(KafkaProperties kafkaProperties) {
        super(kafkaProperties);
        super.valueSerDesClass = StockPriceUpdateSerDes.class;
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public Flux<?> flux() {
//        SenderOptions<String, StockPriceUpdate> senderOptions = senderOptions()
//                .producerProperty(ProducerConfig.ACKS_CONFIG, "all")
//                .producerProperty(ProducerConfig.MAX_BLOCK_MS_CONFIG, Long.MAX_VALUE)
//                .producerProperty(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
//        Flux<StockPriceUpdate> srcFlux = new AbstractScenario.CommittableSource<>()
//        return sender(senderOptions)
//                .send(srcFlux.map(p -> SenderRecord.create(new ProducerRecord<>(topic, p.id(), p), p.id())))
//                .doOnError(e -> log.error("Send failed, terminating.", e))
//                .doOnNext(r -> {
//                    int id = r.correlationMetadata();
//                    log.trace("Successfully stored person with id {} in Kafka", id);
//                    source.commit(id);
//                })
//                .doOnCancel(() -> close());
        return null;
    }
}
