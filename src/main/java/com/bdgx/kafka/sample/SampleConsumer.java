package com.bdgx.kafka.sample;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.bdgx.kafka.SampleScenarios;
import com.bdgx.kafka.serdes.StockPriceUpdateSerDes;
import com.bdgx.resolvers.StockPriceUpdate;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;

/**
 * Sample consumer application using Reactive API for Kafka.
 * To run sample consumer
 * <ol>
 *   <li> Start Zookeeper and Kafka server
 *   <li> Update {@link #BOOTSTRAP_SERVERS} and {@link #TOPIC} if required
 *   <li> Create Kafka topic {@link #TOPIC}
 *   <li> Send some messages to the topic, e.g. by running {@link SampleProducer}
 *   <li> Run {@link SampleConsumer} as Java application with all dependent jars in the CLASSPATH (eg. from IDE).
 *   <li> Shutdown Kafka server and Zookeeper when no longer required
 * </ol>
 */
public class SampleConsumer {

    private static final Logger log = LoggerFactory.getLogger(SampleConsumer.class.getName());

    public static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "demo-topic";

    private final ReceiverOptions<String, StockPriceUpdate> receiverOptions;
    private final SimpleDateFormat dateFormat;
    private final Scheduler scheduler;

    public SampleConsumer(String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "sample-consumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StockPriceUpdateSerDes.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StockPriceUpdateSerDes.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        receiverOptions = ReceiverOptions.create(props);
        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
        this.scheduler = Schedulers.newSingle("sample", true);
    }

    public Flux<StockPriceUpdate> consumeMessages(String topic) {
        ReceiverOptions<String, StockPriceUpdate> options = receiverOptions.subscription(Collections.singleton(topic))
                .addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));
        Flux<ReceiverRecord<String, StockPriceUpdate>> kafkaFlux = KafkaReceiver.create(options).receive();
        return kafkaFlux
                .doOnNext(record ->{
                    ReceiverOffset offset = record.receiverOffset();
                    System.out.printf("Received message: topic-partition=%s offset=%d timestamp=%s key=%s value=%s\n",
                            offset.topicPartition(),
                            offset.offset(),
                            dateFormat.format(new Date(record.timestamp())),
                            record.key(),
                            record.value());
                    offset.acknowledge();
                })
                .map(ReceiverRecord::value)
                .publishOn(scheduler)
                .doOnCancel(() -> scheduler.dispose());
    }
}