package com.bdgx.resolvers;

import com.bdgx.kafka.sample.SampleConsumer;
import com.bdgx.publishers.StockTickerPublisher;
import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.GraphQLContext;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
class Subscription implements GraphQLSubscriptionResolver {

    private StockTickerPublisher stockTickerPublisher;

    Subscription(StockTickerPublisher stockTickerPublisher) {
        this.stockTickerPublisher = stockTickerPublisher;
    }

    Publisher<StockPriceUpdate> stockFluxQuotes(List<String> stockCodes) {
        SampleConsumer consumer = new SampleConsumer(SampleConsumer.BOOTSTRAP_SERVERS);
        return Flux.<StockPriceUpdate>create(sink -> {
            consumer.consumeMessages("kafka-testing",sink);
        }).filter(update -> stockCodes.contains(update.getStockCode()));
    }

    Publisher<StockPriceUpdate> stockRxQuotes(List<String> stockCodes) {
        return stockTickerPublisher.getFluxPublisher(stockCodes);
    }

    Publisher<StockPriceUpdate> stockFlux2RxQuotes(List<String> stockCodes) {
        return RxJava2Adapter.fluxToFlowable(stockTickerPublisher.getFluxPublisher(stockCodes));
    }

    Publisher<StockPriceUpdate> stockRx2FluxQuotes(List<String> stockCodes) {
        return stockTickerPublisher.getFluxPublisher(stockCodes);
    }
}
