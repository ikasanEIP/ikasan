package org.ikasan.mapping.helper;

import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.ParameterName;

import java.util.List;

/**
 * Created by elliga on 19/08/2015.
 */
public interface MappingConfigurationExportHelper {

    /**
     * Helper method to create the XML export document.
     *
     * @param mappingConfiguration
     * @return
     */
    public String getMappingConfigurationExportXml(MappingConfiguration mappingConfiguration,
                                                   List<ParameterName> parameterNames, String schemaLocation);
}
