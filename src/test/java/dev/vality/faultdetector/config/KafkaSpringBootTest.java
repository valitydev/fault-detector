package dev.vality.faultdetector.config;

import dev.vality.testcontainers.annotations.DefaultSpringBootTest;
import dev.vality.testcontainers.annotations.kafka.KafkaTestcontainerSingleton;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@KafkaTestcontainerSingleton(
        properties = {
                "kafka.consumer.reconnect-backoff-ms=1000",
                "kafka.consumer.reconnect-backoff-max-ms=1000",
                "kafka.consumer.retry-backoff-ms=1000",
                "operations.preAggregationPeriod=600000"
        },
        topicsKeys = {
                "kafka.consumer.topic"
        })
@DefaultSpringBootTest
public @interface KafkaSpringBootTest {
}
