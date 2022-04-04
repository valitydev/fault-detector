package dev.vality.faultdetector.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerProperties {

    private String clientId;
    private String groupId;
    private String topic;
    private String pollTimeout;
    private String maxPoolRecords;
    private String fetchMinBytes;
    private String fetchMaxWaitMs;
    private int concurrency;
    private int reconnectBackoffMs;
    private int reconnectBackoffMaxMs;
    private int retryBackoffMs;

}
