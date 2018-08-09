package com.bdgx.kafka.sample;

import java.text.SimpleDateFormat;
import java.util.*;

import com.bdgx.kafka.serdes.StockPriceUpdateSerDes;
import com.bdgx.resolvers.StockPriceUpdate;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;
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
    private final Object lock = new Object();
    private static final Logger log = LoggerFactory.getLogger(SampleConsumer.class.getName());

    public static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "demo-topic";

    private final ReceiverOptions<String, StockPriceUpdate> receiverOptions;
    private final SimpleDateFormat dateFormat;
    private final Scheduler scheduler;
    private Map<String,Disposable> disposableMap = new HashMap<>();
    private Map<String,List<FluxSink<StockPriceUpdate>>> sinks = new HashMap<>();

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

    public void consumeMessages(String topic,FluxSink<StockPriceUpdate> sink) {
        if (disposableMap.get(topic) == null) {
            synchronized (lock) {
                if (sinks.get(topic) == null){
                    List<FluxSink<StockPriceUpdate>> sinkList = new LinkedList<>();
                    sinkList.add(Objects.requireNonNull(sink));
                    sinks.put(topic,sinkList);
                }
                ReceiverOptions<String, StockPriceUpdate> options = receiverOptions.subscription(Collections.singleton(topic))
                        .addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
                        .addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));
                Disposable disposable = KafkaReceiver.create(options)
                        .receive()
                        .doOnNext(record ->{
                            ReceiverOffset offset = record.receiverOffset();
                            System.out.printf("Received message: topic-partition=%s offset=%d timestamp=%s key=%s value=%s\n",
                                    offset.topicPartition(),
                                    offset.offset(),
                                    dateFormat.format(new Date(record.timestamp())),
                                    record.key(),
                                    record.value());
                            offset.acknowledge();
                            sinks.get(topic).forEach(sink1 -> sink1.next(record.value()));
                        })
                        .map(ReceiverRecord::value)
                        .publishOn(scheduler)
                        .doOnSubscribe(s -> System.out.println("subscribed to source"))
                        .doOnCancel(() -> scheduler.dispose())
                        .subscribe();
                disposableMap.put(topic,disposable);
            }
        }
    }
}