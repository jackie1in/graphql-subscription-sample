package com.bdgx.resolvers;

import com.bdgx.kafka.sample.SampleConsumer;
import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Subscription implements GraphQLSubscriptionResolver {
    private static final Logger log = LoggerFactory.getLogger(Subscription.class);
    @Autowired
    private SampleConsumer sampleConsumer;
    Publisher<StockPriceUpdate> stockFluxQuotes(List<String> stockCodes) {
       return sampleConsumer.getFlux().filter(update -> stockCodes.contains(update.getStockCode()));
    }
}
