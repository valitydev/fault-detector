package dev.vality.faultdetector.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka.ssl")
public class KafkaSslProperties {

    private KafkaKeyProperties truststore;
    private KafkaKeyProperties keystore;
    private KafkaKeyProperties key;
    private boolean enabled;

}
