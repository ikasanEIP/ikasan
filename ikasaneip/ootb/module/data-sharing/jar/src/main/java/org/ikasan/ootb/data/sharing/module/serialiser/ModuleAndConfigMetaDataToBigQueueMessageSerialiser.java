package org.ikasan.ootb.data.sharing.module.serialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.dashboard.dto.metadata.configuration.ConfigurationMetaDataImpl;
import org.ikasan.dashboard.dto.metadata.configuration.ConfigurationParameterMetaDataImpl;
import org.ikasan.dashboard.dto.metadata.module.*;
import org.ikasan.ootb.data.sharing.module.model.ModuleAndConfigMetaData;
import org.ikasan.spec.metadata.model.*;
import org.ikasan.spec.serialiser.Serialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serialises module metadata and its configurations together in one BigQueue message.
 *
 * @author Ikasan Development Team
 */
public class ModuleAndConfigMetaDataToBigQueueMessageSerialiser implements Serialiser<ModuleAndConfigMetaData, byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleAndConfigMetaDataToBigQueueMessageSerialiser.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ModuleAndConfigMetaDataToBigQueueMessageSerialiser() {
        // Nested metadata properties use interfaces; tell Jackson which DTOs to create.
        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(ModuleMetaData.class, ModuleMetaDataImpl.class);
        module.addAbstractTypeMapping(FlowMetaData.class, FlowMetaDataImpl.class);
        module.addAbstractTypeMapping(FlowElementMetaData.class, FlowElementMetaDataImpl.class);
        module.addAbstractTypeMapping(Transition.class, TransitionImpl.class);
        module.addAbstractTypeMapping(DecoratorMetaData.class, DecoratorMetaDataImpl.class);
        module.addAbstractTypeMapping(ConfigurationMetaData.class, ConfigurationMetaDataImpl.class);
        module.addAbstractTypeMapping(ConfigurationParameterMetaData.class, ConfigurationParameterMetaDataImpl.class);
        objectMapper.registerModule(module);
    }

    @Override
    public synchronized byte[] serialise(ModuleAndConfigMetaData moduleAndConfig) {
        try {
            return objectMapper.writeValueAsBytes(new BigQueueMessageBuilder<ModuleAndConfigMetaData>()
                .withMessage(moduleAndConfig).build());
        } catch (Exception e) {
            throw new RuntimeException("Could not serialise metadata snapshot", e);
        }
    }

    @Override
    public synchronized ModuleAndConfigMetaData deserialise(byte[] source) {
        try {
            LOGGER.debug("De-serialising");
            BigQueueMessageImpl message = objectMapper.readValue(source, BigQueueMessageImpl.class);
            return objectMapper.convertValue(message.getMessage(), ModuleAndConfigMetaData.class);
        } catch (Exception e) {
            LOGGER.warn("Could not deserialise metadata", e);
            throw new RuntimeException("Could not deserialise module and configuration metadata", e);
        }
    }
}
