package com.bdgx;

import graphql.servlet.GraphQLInvocationInputFactory;
import graphql.servlet.GraphQLObjectMapper;
import graphql.servlet.GraphQLQueryInvoker;
import graphql.servlet.GraphQLWebsocketServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@EnableAutoConfiguration
public class SubscriptionSampleApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SubscriptionSampleApplication.class, args);
        Flux<Integer> source = Flux.range(1, 3)
                .doOnSubscribe(s -> System.out.println("subscribed to source"));

        Flux<Integer> autoCo1 = source.publish().autoConnect();

        autoCo1.subscribe(System.out::println, e -> {}, () -> {});
        System.out.println("subscribed first");
        Thread.sleep(500);
        Flux<Integer> autoCo2 = source.publish().autoConnect();
        System.out.println("subscribing second");
        autoCo2.subscribe(System.out::println, e -> {}, () -> {});
    }

    @Bean
    @ConditionalOnMissingBean
    public GraphQLWebsocketServlet graphQLWebsocketServlet(GraphQLInvocationInputFactory invocationInputFactory, GraphQLQueryInvoker queryInvoker, GraphQLObjectMapper graphQLObjectMapper) {
        return new MyServlet(queryInvoker, invocationInputFactory, graphQLObjectMapper);
    }
}
