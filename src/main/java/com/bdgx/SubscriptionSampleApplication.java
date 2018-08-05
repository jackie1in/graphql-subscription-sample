package com.bdgx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
public class SubscriptionSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubscriptionSampleApplication.class, args);
    }

}
