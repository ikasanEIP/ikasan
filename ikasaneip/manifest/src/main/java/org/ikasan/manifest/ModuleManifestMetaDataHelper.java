package org.ikasan.manifest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.configurationService.metadata.ConfigurationMetaDataImpl;
import org.ikasan.configurationService.metadata.ConfigurationParameterMetaDataImpl;
import org.ikasan.manifest.model.*;
import org.ikasan.spec.metadata.*;
import org.ikasan.topology.metadata.model.*;

public class ModuleManifestMetaDataHelper {
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();

        SimpleModule m = new SimpleModule();
        m.addAbstractTypeMapping(ModuleManifestMetaData.class, ModuleManifestMetaDataImpl.class);
        m.addAbstractTypeMapping(ConfigurationMetaData.class, ConfigurationMetaDataImpl.class);
        m.addAbstractTypeMapping(ConfigurationParameterMetaData.class, ConfigurationParameterMetaDataImpl.class);
        m.addAbstractTypeMapping(DependencyManagementMetaData.class, DependencyManagementMetaDataImpl.class);
        m.addAbstractTypeMapping(DependencyMetaData.class, DependencyMetaDataImpl.class);
        m.addAbstractTypeMapping(RepositoryMetaData.class, RepositoryMetaDataImpl.class);
        m.addAbstractTypeMapping(ModuleMetaData.class, ModuleMetaDataImpl.class);
        m.addAbstractTypeMapping(FlowMetaData.class, FlowMetaDataImpl.class);
        m.addAbstractTypeMapping(FlowElementMetaData.class, FlowElementMetaDataImpl.class);
        m.addAbstractTypeMapping(Transition.class, TransitionImpl.class);
        m.addAbstractTypeMapping(DecoratorMetaData.class, DecoratorMetaDataImpl.class);
        m.addAbstractTypeMapping(ConstructorMetaData.class, ConstructorMetaDataImpl.class);
        m.addAbstractTypeMapping(BeanDefinitionMetaData.class, BeanDefinitionMetaDataImpl.class);
        m.addAbstractTypeMapping(ParameterizedType.class, ParameterizedTypeImpl.class);
        m.addAbstractTypeMapping(TypeParameter.class, TypeParameterImpl.class);

        MAPPER.registerModule(m);
    }

    /**
     * Serializes the provided ModuleManifestMetaData object into a JSON string.
     *
     * @param moduleManifestMetaData The ModuleManifestMetaData object to be serialized.
     * @return A JSON string representation of the ModuleManifestMetaData.
     */
    public static String serialiseModuleManifest(ModuleManifestMetaData moduleManifestMetaData) {
        String result;

        try
        {
            //JSON file to Java object
            result = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(moduleManifestMetaData);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred serialising module manifest meta data object!", e);
        }

        return result;
    }

    /**
     * Deserializes the provided JSON string into a ModuleManifestMetaData object.
     *
     * @param moduleManifest The JSON string representing the module manifest to be deserialized.
     * @return The deserialized ModuleManifestMetaData object.
     * @throws RuntimeException If an exception occurs during deserialization.
     */
    public static ModuleManifestMetaData deserialiseModuleManifest(String moduleManifest) {
        ModuleManifestMetaDataImpl result;

        try
        {
            //JSON file to Java object
            result = MAPPER.readValue(moduleManifest, ModuleManifestMetaDataImpl.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception has occurred creating module manifest meta data object!", e);
        }

        return result;
    }
}
