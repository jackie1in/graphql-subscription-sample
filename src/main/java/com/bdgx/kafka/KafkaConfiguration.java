package com.bdgx.kafka;

import com.bdgx.kafka.sample.SampleConsumer;
import com.bdgx.kafka.sample.SampleProducer;
import com.bdgx.kafka.sample.StockScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author hai
 * description
 * email hilin2333@gmail.com
 * date 2018/8/14 4:29 PM
 */
@Configuration
public class KafkaConfiguration {
    @Bean
    public SampleProducer producer(KafkaTemplate<byte[],byte[]> kafkaTemplate){
        return new SampleProducer(kafkaTemplate);
    }

    @Bean
    public StockScheduler stockTickerPublisher(SampleProducer sampleProducer){
        return new StockScheduler(sampleProducer);
    }

    @Bean
    @ConditionalOnClass(KafkaAutoConfiguration.class)
    public SampleConsumer sampleConsumer(){
        return new SampleConsumer();
    }
}
