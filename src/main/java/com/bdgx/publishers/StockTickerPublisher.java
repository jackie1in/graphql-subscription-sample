package com.bdgx.publishers;

import com.bdgx.kafka.sample.SampleConsumer;
import com.bdgx.kafka.sample.SampleProducer;
import com.bdgx.resolvers.StockPriceUpdate;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.observables.ConnectableObservable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class StockTickerPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(StockTickerPublisher.class);

    private final Flux<StockPriceUpdate> fluxPublisher;

    private Flowable<StockPriceUpdate> publisher;
    public StockTickerPublisher() {
//        Observable<StockPriceUpdate> stockPriceUpdateObservable = Observable.create(emitter -> {
//
//            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
//            executorService.scheduleAtFixedRate(newStockTick(emitter), 0, 2, TimeUnit.SECONDS);
//
//        });
//
//        ConnectableObservable<StockPriceUpdate> connectableObservable = stockPriceUpdateObservable.share().publish();
//        connectableObservable.connect();
//        Flux<StockPriceUpdate> publisher = Flux.create(sink -> {
//            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
//            executorService.scheduleAtFixedRate(newStockTick(sink), 0, 2, TimeUnit.SECONDS);
//        });
//        fluxPublisher = publisher;


        SampleProducer producer = new SampleProducer(SampleProducer.BOOTSTRAP_SERVERS);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(newStockTick(producer), 0, 2, TimeUnit.SECONDS);
        SampleConsumer consumer = new SampleConsumer(SampleConsumer.BOOTSTRAP_SERVERS);
        fluxPublisher = consumer.consumeMessages("kafka-testing");
    }

    private Runnable newStockTick(ObservableEmitter<StockPriceUpdate> emitter) {
        return () -> {
            List<StockPriceUpdate> stockPriceUpdates = getUpdates(rollDice(0, 5));
            if (stockPriceUpdates != null) {
                emitStocks(emitter, stockPriceUpdates);
            }
        };
    }
    private Runnable newStockTick(FluxSink<StockPriceUpdate> emitter) {
        return () -> {
            List<StockPriceUpdate> stockPriceUpdates = getUpdates(rollDice(0, 5));
            if (stockPriceUpdates != null) {
                emitStocks(emitter, stockPriceUpdates);
            }
        };
    }

    private Runnable newStockTick(SampleProducer producer) {
        return () -> {
            List<StockPriceUpdate> stockPriceUpdates = getUpdates(rollDice(0, 5));
            if (stockPriceUpdates != null) {
                emitStocks(producer, stockPriceUpdates);
            }
        };
    }

    private void emitStocks(ObservableEmitter<StockPriceUpdate> emitter, List<StockPriceUpdate> stockPriceUpdates) {
        for (StockPriceUpdate stockPriceUpdate : stockPriceUpdates) {
            try {
                emitter.onNext(stockPriceUpdate);
            } catch (RuntimeException e) {
                LOG.error("Cannot send StockUpdate", e);
            }
        }
    }

    private void emitStocks(SampleProducer producer, List<StockPriceUpdate> stockPriceUpdates) {
        for (StockPriceUpdate stockPriceUpdate : stockPriceUpdates) {
            try {
                producer.sendMessages("kafka-testing",stockPriceUpdate);
            }catch (InterruptedException e) {
                LOG.error("Cannot send StockUpdate to kafka", e);
            }
        }
    }
    private void emitStocks(FluxSink<StockPriceUpdate> emitter, List<StockPriceUpdate> stockPriceUpdates) {
        for (StockPriceUpdate stockPriceUpdate : stockPriceUpdates) {
            try {
                emitter.next(stockPriceUpdate);
            } catch (RuntimeException e) {
                LOG.error("Cannot send StockUpdate", e);
            }
        }
    }

    public Flowable<StockPriceUpdate> getPublisher() {
        return publisher;
    }

    public Flowable<StockPriceUpdate> getPublisher(List<String> stockCodes) {
        if (stockCodes != null) {
            return publisher.filter(stockPriceUpdate -> stockCodes.contains(stockPriceUpdate.getStockCode()));
        }
        return publisher;
    }

    public Flux<StockPriceUpdate> getFluxPublisher(List<String> stockCodes) {
        if (stockCodes != null) {
            return fluxPublisher.filter(stockPriceUpdate -> stockCodes.contains(stockPriceUpdate.getStockCode()));
        }
        return fluxPublisher;
    }

    private List<StockPriceUpdate> getUpdates(int number) {
        List<StockPriceUpdate> updates = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            updates.add(rollUpdate());
        }
        return updates;
    }


    private final static Map<String, BigDecimal> CURRENT_STOCK_PRICES = new ConcurrentHashMap<>();

    static {
        CURRENT_STOCK_PRICES.put("TEAM", dollars(39, 64));
        CURRENT_STOCK_PRICES.put("IBM", dollars(147, 10));
        CURRENT_STOCK_PRICES.put("AMZN", dollars(1002, 94));
        CURRENT_STOCK_PRICES.put("MSFT", dollars(77, 49));
        CURRENT_STOCK_PRICES.put("GOOGL", dollars(1007, 87));
    }

    private StockPriceUpdate rollUpdate() {
        ArrayList<String> STOCK_CODES = new ArrayList<>(CURRENT_STOCK_PRICES.keySet());

        String stockCode = STOCK_CODES.get(rollDice(0, STOCK_CODES.size() - 1));
        BigDecimal currentPrice = CURRENT_STOCK_PRICES.get(stockCode);


        BigDecimal incrementDollars = dollars(rollDice(0, 1), rollDice(0, 99));
        if (rollDice(0, 10) > 7) {
            // 0.3 of the time go down
            incrementDollars = incrementDollars.negate();
        }
        BigDecimal newPrice = currentPrice.add(incrementDollars);

        CURRENT_STOCK_PRICES.put(stockCode, newPrice);
        return new StockPriceUpdate(stockCode, LocalDateTime.now(), newPrice, incrementDollars);
    }

    private static BigDecimal dollars(int dollars, int cents) {
        return truncate("" + dollars + "." + cents);
    }

    private static BigDecimal truncate(final String text) {
        BigDecimal bigDecimal = new BigDecimal(text);
        if (bigDecimal.scale() > 2)
            bigDecimal = new BigDecimal(text).setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.stripTrailingZeros();
    }

    private final static Random rand = new Random();

    private static int rollDice(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

}
