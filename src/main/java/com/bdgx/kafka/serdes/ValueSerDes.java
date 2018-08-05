package com.bdgx.kafka.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

/**
 * @author hai
 * description
 * email hilin2333@gmail.com
 * date 2018/8/5 4:03 PM
 */
public interface ValueSerDes<T> extends Serializer<T>, Deserializer<T> {

}
