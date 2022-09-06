package dev.vality.faultdetector.config;

import dev.vality.faultdetector.config.property.KafkaConsumerProperties;
import dev.vality.faultdetector.config.property.KafkaKeyProperties;
import dev.vality.faultdetector.config.property.KafkaSslProperties;
import dev.vality.faultdetector.data.ServiceOperation;
import dev.vality.faultdetector.serializer.ServiceOperationDeserializer;
import dev.vality.faultdetector.serializer.ServiceOperationSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    private static final String EARLIEST = "earliest";

    @Value("${kafka.bootstrap-servers}")
    private String servers;

    @Bean
    public ProducerFactory<String, ServiceOperation> producerFactory(KafkaConsumerProperties kafkaConsumerProperties) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, kafkaConsumerProperties.getReconnectBackoffMs());
        configProps.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG,
                kafkaConsumerProperties.getReconnectBackoffMaxMs());
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, kafkaConsumerProperties.getRetryBackoffMs());

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ServiceOperation> kafkaTemplate(KafkaConsumerProperties kafkaConsumerProperties) {
        return new KafkaTemplate<>(producerFactory(kafkaConsumerProperties));
    }

    @Bean
    public ConsumerFactory<String, ServiceOperation> serviceOperationConsumerFactory(
            KafkaConsumerProperties kafkaConsumerProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, kafkaConsumerProperties.getReconnectBackoffMs());
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG,
                kafkaConsumerProperties.getReconnectBackoffMaxMs());
        props.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, kafkaConsumerProperties.getRetryBackoffMs());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ServiceOperation> kafkaListenerContainerFactory(
            KafkaConsumerProperties kafkaConsumerProperties
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ServiceOperation> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(serviceOperationConsumerFactory(kafkaConsumerProperties));
        factory.setBatchListener(false);
        factory.setConcurrency(kafkaConsumerProperties.getConcurrency());
        return factory;
    }

}