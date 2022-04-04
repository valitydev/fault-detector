package dev.vality.faultdetector.config.property;

import lombok.Data;

@Data
public class KafkaKeyProperties {

    private String locationConfig;
    private String passwordConfig;
    private String type;

}
