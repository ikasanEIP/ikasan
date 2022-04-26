package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import org.ikasan.dashboard.ContextParametersRestServiceImpl;
import org.ikasan.ootb.scheduler.agent.module.component.cache.ContextParametersCache;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public class ImportContextParametersProcess<T> implements Producer<T>, ManagedResource {

    @Resource
    private Environment environment;

    private ContextParametersRestServiceImpl service;

    @Override
    public void invoke(T payload) throws EndpointException {

        importContextParameters();
    }

    public ContextParametersRestServiceImpl getService() {
        if (this.service == null) {
            this.service = new ContextParametersRestServiceImpl(environment,
                                                new HttpComponentsClientHttpRequestFactory(),
                                                "/rest/jobContext/getAll");
        }
        return this.service;
    }

    public void setService(ContextParametersRestServiceImpl service) {
        this.service = service;
    }

    private void importContextParameters() {
        Map<String, List<ContextParameterInstance>> contextParameters = getService().getAll();

        ContextParametersCache.instance().put(contextParameters);
    }

    @Override
    public void startManagedResource() {
        // on module startup, we import all context parameters
        importContextParameters();
    }

    @Override
    public void stopManagedResource() {

    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager) {

    }

    @Override
    public boolean isCriticalOnStartup() {
        return false;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup) {

    }
}
