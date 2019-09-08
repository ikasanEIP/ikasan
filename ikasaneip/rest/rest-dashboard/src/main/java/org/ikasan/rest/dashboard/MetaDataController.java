package org.ikasan.rest.dashboard;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.rest.dashboard.model.dto.ErrorDto;
import org.ikasan.rest.dashboard.model.metadata.configuration.ConfigurationMetaDataImpl;
import org.ikasan.rest.dashboard.model.metadata.configuration.ConfigurationParameterMetaDataImpl;
import org.ikasan.rest.dashboard.model.metadata.module.FlowElementMetaDataImpl;
import org.ikasan.rest.dashboard.model.metadata.module.FlowMetaDataImpl;
import org.ikasan.rest.dashboard.model.metadata.module.ModuleMetaDataImpl;
import org.ikasan.rest.dashboard.model.metadata.module.TransitionImpl;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.persistence.BatchInsert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * Metadata application implementing the REST contract
 */

@RequestMapping("/rest")
@RestController
public class MetaDataController
{
    private static Logger logger = LoggerFactory.getLogger(MetaDataController.class);

    private ObjectMapper mapper;

    private BatchInsert<ModuleMetaData> moduleMetaDataBatchInsert;
    private BatchInsert<ConfigurationMetaData> configurationMetaDataBatchInsert;

    public MetaDataController(BatchInsert<ModuleMetaData> moduleMetaDataBatchInsert
        , BatchInsert<ConfigurationMetaData> configurationMetaDataBatchInsert)
    {
        this.moduleMetaDataBatchInsert = moduleMetaDataBatchInsert;
        if(this.moduleMetaDataBatchInsert == null)
        {
            throw new IllegalArgumentException("moduleMetaDataBatchInsert cannot be null!");
        }
        this.configurationMetaDataBatchInsert = configurationMetaDataBatchInsert;
        if(this.configurationMetaDataBatchInsert == null)
        {
            throw new IllegalArgumentException("configurationMetaDataBatchInsert cannot be null!");
        }

        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule m = new SimpleModule();
        m.addAbstractTypeMapping(ModuleMetaData.class, ModuleMetaDataImpl.class);
        m.addAbstractTypeMapping(FlowMetaData.class, FlowMetaDataImpl.class);
        m.addAbstractTypeMapping(FlowElementMetaData.class, FlowElementMetaDataImpl.class);
        m.addAbstractTypeMapping(Transition.class, TransitionImpl.class);
        m.addAbstractTypeMapping(ConfigurationParameterMetaData.class,ConfigurationParameterMetaDataImpl.class);
        m.addAbstractTypeMapping(ConfigurationMetaData.class,ConfigurationMetaDataImpl.class);

        this.mapper.registerModule(m);
    }

    @RequestMapping(method = RequestMethod.PUT,
        value = "/module/metadata")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity harvestModuleMetadata(@RequestBody String moduleMetadataJsonPayload)
    {
        try
        {
            logger.debug(moduleMetadataJsonPayload);
            ModuleMetaData moduleMetadata = this.mapper.readValue(moduleMetadataJsonPayload
                , ModuleMetaDataImpl.class);

            List<ModuleMetaData> entities = new ArrayList<>();
            entities.add(moduleMetadata);

            this.moduleMetaDataBatchInsert.insert(entities);
        }
        catch (Exception e)
        {
            return new ResponseEntity(
                new ErrorDto("An error has occurred attempting to perform a batch insert of ModuleMetaData!"),
                HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,
        value = "/configuration/metadata")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity harvestConfigurationMetadata(@RequestBody String configurationMetadataJsonPayload)
    {
        try
        {
            logger.debug(configurationMetadataJsonPayload);

            List<ConfigurationMetaData> configurationMetaDataList = this.mapper.readValue(configurationMetadataJsonPayload
                , mapper.getTypeFactory().constructCollectionType(List.class, ConfigurationMetaDataImpl.class));

            this.configurationMetaDataBatchInsert.insert(configurationMetaDataList);
        }
        catch (Exception e)
        {
            return new ResponseEntity(
                new ErrorDto("An error has occurred attempting to perform a batch insert of ConfigurationMetaData!"),
                HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
