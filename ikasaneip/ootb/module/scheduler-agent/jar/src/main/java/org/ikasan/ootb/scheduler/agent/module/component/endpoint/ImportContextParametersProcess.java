package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import org.ikasan.dashboard.ContextParametersRestServiceImpl;
import org.ikasan.ootb.scheduler.agent.module.component.cache.ContextParametersCache;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public class ImportContextParametersProcess<T> implements Producer<T> {

    @Resource
    private Environment environment;

    private ContextParametersRestServiceImpl service;

    @Override
    public void invoke(T payload) throws EndpointException {

        Map<String, List<ContextParameterInstance>> contextParameters = getService().getAll();

        ContextParametersCache.instance().put(contextParameters);
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
}
