package com.bdgx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@EnableAutoConfiguration
public class SubscriptionSampleApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SubscriptionSampleApplication.class, args);
    }
}
