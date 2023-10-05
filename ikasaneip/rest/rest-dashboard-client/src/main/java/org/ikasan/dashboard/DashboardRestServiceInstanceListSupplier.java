package org.ikasan.dashboard;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class DashboardRestServiceInstanceListSupplier implements ServiceInstanceListSupplier {

    private final String serviceId;
    private List<DefaultServiceInstance> serviceInstances = new ArrayList<>();

    DashboardRestServiceInstanceListSupplier(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceInstances(List<DefaultServiceInstance> serviceInstances) {
        this.serviceInstances = serviceInstances;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return Flux.just(this.serviceInstances.stream()
            .map(serviceInstance -> (ServiceInstance)serviceInstance)
            .collect(Collectors.toList()));
    }
}