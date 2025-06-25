package dev.vality.faultdetector.handlers;

import dev.vality.faultdetector.binders.FaultDetectorMetricsBinder;
import dev.vality.faultdetector.data.ServiceAggregates;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "service.metrics.enabled", havingValue = "true")
public class AddProviderMetricsHandler implements Handler<String> {

    private final Map<String, ServiceAggregates> aggregatesMap;

    private final MeterRegistry meterRegistry;

    private final Map<String, List<Gauge>> serviceMetersMap = new ConcurrentHashMap<>();

    private final List<FaultDetectorMetricsBinder> meters = new CopyOnWriteArrayList<>();

    @Override
    public void handle(String serviceId) {
        log.warn("Add gauge metrics for the service {} get started", serviceId);
        FaultDetectorMetricsBinder faultDetectorMetricsBinder =
                new FaultDetectorMetricsBinder(aggregatesMap, serviceId);
        faultDetectorMetricsBinder.bindTo(meterRegistry);
        meters.add(faultDetectorMetricsBinder);
        log.debug("Gauge metrics for the service {} was added. Service meter map size is {}. " +
                        "Registry meter size is {}. Meter registry config: {}",
                serviceId, serviceMetersMap.size(), meterRegistry.getMeters().size(),
                meterRegistry.config());
    }
}
