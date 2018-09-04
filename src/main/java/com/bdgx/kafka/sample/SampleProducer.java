package com.bdgx.kafka.sample;

import com.bdgx.resolvers.StockPriceUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

public class SampleProducer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(SampleProducer.class.getName());

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final KafkaTemplate<byte[],byte[]> kafkaTemplate;

    public SampleProducer(KafkaTemplate<byte[], byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessages(String topic,StockPriceUpdate update) {
        try {
            kafkaTemplate.send(topic,objectMapper.writeValueAsBytes(update));
        }catch (JsonProcessingException e){
            log.error("object serialized fail");
        }
    }
}