package org.ikasan.ootb.data.sharing.module.component.broker;

import org.ikasan.ootb.data.sharing.module.component.broker.configuration.ModuleMetaDataBrokerConfiguration;
import org.ikasan.ootb.data.sharing.module.model.ModuleAndConfigMetaData;
import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.dashboard.DataSharingRestService;
import org.ikasan.spec.metadata.model.ModuleMetaData;
import org.ikasan.spec.metadata.model.ConfigurationMetaData;
import org.ikasan.spec.metadata.model.FlowMetaData;
import org.ikasan.spec.metadata.model.FlowElementMetaData;
import org.ikasan.spec.metadata.model.DecoratorMetaData;
import org.ikasan.spec.search.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.IntFunction;

/** Fetches module metadata and any available referenced configurations on each scheduled trigger. */
public class ModuleMetaDataBroker implements Broker<Object, ModuleAndConfigMetaData>,
    ConfiguredResource<ModuleMetaDataBrokerConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleMetaDataBroker.class);

    private final DataSharingRestService dataSharingRestService;
    private ModuleMetaDataBrokerConfiguration configuration;
    private String configurationId;

    public ModuleMetaDataBroker(DataSharingRestService dataSharingRestService) {
        this.dataSharingRestService = Objects.requireNonNull(dataSharingRestService, "dataSharingRestService");
    }

    @Override
    public ModuleAndConfigMetaData invoke(Object trigger) throws EndpointException {
        try {
            if (configuration == null || configuration.getBatchSize() <= 0) {
                throw new IllegalArgumentException("Metadata broker requires a positive batchSize");
            }
            if (configuration.getModuleNames() == null || configuration.getModuleNames().isEmpty()) {
                throw new IllegalArgumentException("Metadata broker requires moduleNames");
            }
            List<ModuleMetaData> modules = fetchPages(offset ->
                dataSharingRestService.queryModuleMetadata(configuration.getModuleNames(),
                    configuration.getBatchSize(), offset));
            Set<String> ids = new LinkedHashSet<>();
            for (ModuleMetaData module : modules) {
                addId(ids, module.getConfiguredResourceId());
                if (module.getFlows() == null) {
                    continue;
                }
                for (FlowMetaData flow : module.getFlows()) {
                    addId(ids, flow.getConfigurationId());
                    addComponentIds(ids, flow.getConsumer());
                    if (flow.getFlowElements() != null) {
                        for (FlowElementMetaData element : flow.getFlowElements()) {
                            addComponentIds(ids, element);
                        }
                    }
                }
            }
            List<String> configurationIds = new ArrayList<>(ids);
            List<ConfigurationMetaData> configurations = new ArrayList<>();
            // Bound the number of IDs in each REST URL as well as the result page size.
            for (int start = 0; start < configurationIds.size();) {
                int end = start + Math.min(configuration.getBatchSize(), configurationIds.size() - start);
                List<String> batch = new ArrayList<>(configurationIds.subList(start, end));
                List<ConfigurationMetaData> batchResults = new ArrayList<>();
                try {
                    fetchPages(offset -> dataSharingRestService.queryConfiguration(
                        batch, configuration.getBatchSize(), offset), batchResults);
                } catch (RuntimeException e) {
                    LOGGER.warn("Configuration lookup failed for IDs {}; retaining {} records and continuing",
                        batch, batchResults.size(), e);
                }
                configurations.addAll(batchResults);
                Set<String> missingIds = new LinkedHashSet<>(batch);
                for (ConfigurationMetaData result : batchResults) {
                    missingIds.remove(result.getConfigurationId());
                }
                if (!missingIds.isEmpty()) {
                    LOGGER.warn("Configurations unavailable for IDs {}; continuing with available metadata", missingIds);
                }
                start = end;
            }
            return new ModuleAndConfigMetaData(modules, configurations);
        } catch (Exception e) {
            throw new EndpointException(e);
        }
    }

    private <T> List<T> fetchPages(IntFunction<SearchResults<T>> query) {
        List<T> values = new ArrayList<>();
        fetchPages(query, values);
        return values;
    }

    private <T> void fetchPages(IntFunction<SearchResults<T>> query, List<T> values) {
        int offset = 0;
        SearchResults<T> results;
        do {
            results = query.apply(offset);
            if (results == null || results.getResultList() == null) {
                throw new IllegalStateException("Dashboard returned no metadata result list");
            }
            List<T> page = results.getResultList();
            if (page.isEmpty() && offset < results.getTotalNumberOfResults()) {
                throw new IllegalStateException("Dashboard returned an empty page before all metadata was fetched");
            }
            values.addAll(page);
            offset = Math.addExact(offset, page.size());
        } while (offset < results.getTotalNumberOfResults());
    }

    private void addComponentIds(Set<String> ids, FlowElementMetaData element) {
        if (element == null) {
            return;
        }
        addId(ids, element.getConfigurationId());
        addId(ids, element.getInvokerConfigurationId());
        if (element.getDecorators() != null) {
            for (DecoratorMetaData decorator : element.getDecorators()) {
                addId(ids, decorator.getConfigurationId());
            }
        }
    }

    private void addId(Set<String> ids, String id) {
        if (id != null && !id.isBlank()) {
            ids.add(id);
        }
    }

    @Override
    public String getConfiguredResourceId() { return configurationId; }
    @Override
    public void setConfiguredResourceId(String id) { this.configurationId = id; }
    @Override
    public ModuleMetaDataBrokerConfiguration getConfiguration() { return configuration; }
    @Override
    public void setConfiguration(ModuleMetaDataBrokerConfiguration configuration) { this.configuration = configuration; }
}
