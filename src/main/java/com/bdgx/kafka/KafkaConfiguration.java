package com.bdgx.kafka;

import com.bdgx.kafka.sample.SampleProducer;
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
}
