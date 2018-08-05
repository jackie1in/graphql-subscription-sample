package com.bdgx.kafka.scenario;

import com.bdgx.kafka.serdes.StockPriceUpdateSerDes;
import com.bdgx.kafka.serdes.ValueSerDes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.*;

/**
 * @author hai
 * description
 * email hilin2333@gmail.com
 * date 2018/8/5 3:55 PM
 */
public abstract class AbstractScenario<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractScenario.class);
    protected final KafkaProperties kafkaProperties;
    protected Class<? extends ValueSerDes> valueSerDesClass;
    protected KafkaSender<String, T> sender;
    protected List<Disposable> disposables = new ArrayList<>();

    public AbstractScenario(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }
    public AbstractScenario(KafkaProperties kafkaProperties, Class<ValueSerDes> valueSerDesClass) {
        this.kafkaProperties = kafkaProperties;
        this.valueSerDesClass = valueSerDesClass;
    }
    public abstract Flux<?> flux();

    public void runScenario() throws InterruptedException {
        flux().blockLast();
        close();
    }

    public void close() {
        if (sender != null)
            sender.close();
        for (Disposable disposable : disposables)
            disposable.dispose();
    }

    public SenderOptions<String, T> senderOptions() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProperties.getProducer().getClientId());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducer().getAcks());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerDesClass);
        return SenderOptions.create(props);
    }

    public KafkaSender<String, T> sender(SenderOptions<String, T> senderOptions) {
        return KafkaSender.create(senderOptions);
    }

    public ReceiverOptions<String, T> receiverOptions() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaProperties.getConsumer().getClientId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueSerDesClass);
        return ReceiverOptions.<String, T>create(props);
    }

    public ReceiverOptions<String, T> receiverOptions(Collection<String> topics) {
        return receiverOptions()
                .addAssignListener(p -> log.info("Group {} partitions assigned {}", kafkaProperties.getConsumer().getGroupId(), p))
                .addRevokeListener(p -> log.info("Group {} partitions assigned {}", kafkaProperties.getConsumer().getGroupId(), p))
                .subscription(topics);
    }
}
