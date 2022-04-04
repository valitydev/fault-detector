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

    @Bean
    public ProducerFactory<String, ServiceOperation> producerFactory(KafkaConsumerProperties kafkaConsumerProperties,
                                                                     KafkaSslProperties kafkaSslProperties) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProperties.getBootstrapServers());
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConsumerProperties.getClientId());
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ServiceOperationSerializer.class);

        if (kafkaSslProperties.isEnable()) {
            addSslKafkaProps(configProps, kafkaSslProperties);
        }

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ServiceOperation> kafkaTemplate(KafkaConsumerProperties kafkaConsumerProperties,
                                                                 KafkaSslProperties kafkaSslProperties) {
        return new KafkaTemplate<>(producerFactory(kafkaConsumerProperties, kafkaSslProperties));
    }

    @Bean
    public ConsumerFactory<String, ServiceOperation> serviceOperationConsumerFactory(
            KafkaConsumerProperties kafkaConsumerProperties,
            KafkaSslProperties kafkaSslProperties
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProperties.getBootstrapServers());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaConsumerProperties.getClientId());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ServiceOperationDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConsumerProperties.getMaxPoolRecords());
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, kafkaConsumerProperties.getFetchMinBytes());
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, kafkaConsumerProperties.getFetchMaxWaitMs());

        if (kafkaSslProperties.isEnable()) {
            addSslKafkaProps(props, kafkaSslProperties);
        }

        return new DefaultKafkaConsumerFactory<>(props);
    }

    private void addSslKafkaProps(Map<String, Object> props, KafkaSslProperties kafkaSslProperties) {
        // configure the following three settings for SSL Encryption/Decryption
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SSL.name);
        // The truststore stores all the certificates that the machine should trust
        KafkaKeyProperties truststore = kafkaSslProperties.getTruststore();
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                new File(truststore.getLocationConfig()).getAbsolutePath());
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststore.getPasswordConfig());
        props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, truststore.getType());

        // The keystore stores each machine's own identity
        KafkaKeyProperties keystore = kafkaSslProperties.getKeystore();
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, new File(keystore.getLocationConfig()).getAbsolutePath());
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystore.getPasswordConfig());
        props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, keystore.getType());

        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaSslProperties.getKey().getPasswordConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ServiceOperation> kafkaListenerContainerFactory(
            KafkaConsumerProperties kafkaConsumerProperties,
            KafkaSslProperties kafkaSslProperties
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ServiceOperation> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(serviceOperationConsumerFactory(kafkaConsumerProperties, kafkaSslProperties));
        factory.setBatchListener(false);
        factory.setConcurrency(kafkaConsumerProperties.getConcurrency());
        return factory;
    }

}