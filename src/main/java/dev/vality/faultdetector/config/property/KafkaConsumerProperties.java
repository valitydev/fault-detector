package dev.vality.faultdetector.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerProperties {

    private int reconnectBackoffMs;
    private int reconnectBackoffMaxMs;
    private int retryBackoffMs;

}
