package org.ikasan.ootb.data.sharing.module.component.endpoint.consumer;

import org.ikasan.component.endpoint.quartz.consumer.CallBackMessageConsumer;
import org.ikasan.component.endpoint.quartz.consumer.CallBackMessageProvider;
import org.ikasan.ootb.data.sharing.module.component.endpoint.consumer.configuration.WiretapConsumerConfiguration;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.dashboard.DataSharingRestService;
import org.ikasan.spec.search.SearchResults;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.quartz.JobExecutionContext;

public class WiretapMessageProvider implements CallBackMessageProvider<Boolean>,
    ConfiguredResource<WiretapConsumerConfiguration> {

    private final DataSharingRestService dataSharingRestService;
    private WiretapConsumerConfiguration configuration;
    private String configurationId;
    private CallBackMessageConsumer callBackMessageConsumer;

    /**
     * Constructs a WiretapMessageProvider with the specified DataSharingRestService dependency.
     *
     * @param dataSharingRestService the DataSharingRestService instance used for querying wiretap data
     */
    public WiretapMessageProvider(DataSharingRestService dataSharingRestService) {
        this.dataSharingRestService = dataSharingRestService;
    }

    @Override
    public Boolean invoke(JobExecutionContext jobExecutionContext) throws EndpointException {
        try {
            long count = this.dataSharingRestService.countWiretap(configuration.getQueryTimestamp(),
                configuration.getQueryTimestamp() + configuration.getQueryWindowMilliseconds(),
                configuration.getModuleNames());

            for(int offset=0; offset<count; offset+=this.configuration.getBatchSize()) {
                SearchResults<WiretapEvent> results = this.dataSharingRestService
                    .queryWiretap(configuration.getQueryTimestamp(),
                        configuration.getQueryTimestamp() + configuration.getQueryWindowMilliseconds()
                        , configuration.getModuleNames(), this.configuration.getBatchSize(), offset);

                if(offset + this.configuration.getBatchSize() > count) {
                    this.configuration.setOffset(0);
                    this.configuration.setQueryTimestamp(configuration.getQueryTimestamp()
                        + configuration.getQueryWindowMilliseconds());
                }

                this.callBackMessageConsumer.invoke(results.getResultList());
            }

            return true;
        }
        catch (Exception e) {
            throw new EndpointException(e);
        }
    }

    @Override
    public void setCallBackMessageConsumer(CallBackMessageConsumer callBackMessageConsumer) {
        this.callBackMessageConsumer = callBackMessageConsumer;
    }

    @Override
    public String getConfiguredResourceId() {
        return this.configurationId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configurationId = id;
    }

    @Override
    public WiretapConsumerConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setConfiguration(WiretapConsumerConfiguration configuration) {
        this.configuration = configuration;
    }
}
