package com.bdgx.kafka.serdes;

import com.bdgx.resolvers.StockPriceUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * @author hai
 * description
 * email hilin2333@gmail.com
 * date 2018/8/5 4:31 PM
 */
public class StockPriceUpdateSerDes implements ValueSerDes<StockPriceUpdate>{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, StockPriceUpdate stockPriceUpdate) {
        try {
            return objectMapper.writeValueAsBytes(stockPriceUpdate);
        } catch (JsonProcessingException e) {
            return new byte[0];
        }
    }

    @Override
    public StockPriceUpdate deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data,StockPriceUpdate.class);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void close() {
    }
}
