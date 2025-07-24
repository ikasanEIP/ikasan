package org.ikasan.spec.metadata;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.StartupControl;

import java.util.Map;
import java.util.List;

/**
 * Interface for providing serialization and deserialization functionality for ModuleManifestMetaData objects.
 * @param <T> Type used for serialization and deserialization.
 */
public interface ModuleManifestMetaDataProvider<T>
{

    /**
     * Describes the module manifest based on the provided Module and StartupControl map.
     *
     * @param module The Module to describe.
     * @param startUpControlMap The map of StartupControl for the components.
     * @return The metadata representing the module manifest.
     */
    ModuleManifestMetaData describeModuleManifest(Module<Flow> module, Map<String, StartupControl> startUpControlMap);

//    /**
//     * Extracts a list of ParameterizedType objects representing the parameterized types from the provided ModuleManifestMetaData.
//     *
//     * @param moduleManifestMetaData The ModuleManifestMetaData to extract parameterized types from.
//     * @return A list of ParameterizedType objects representing the parameterized types extracted from the provided ModuleManifestMetaData.
//     */
//    List<ParameterizedType> extractParameterizedTypes(ModuleManifestMetaData moduleManifestMetaData);

    /**
     * Serialize the provided ModuleManifestMetaData object.
     *
     * @param moduleManifestMetaData The ModuleManifestMetaData object to be serialized.
     * @return The serialized representation of the ModuleManifestMetaData object.
     */
    T serialiseModuleManifest(ModuleManifestMetaData moduleManifestMetaData);


    /**
     * Deserialize the provided module manifest into ModuleManifestMetaData object.
     *
     * @param moduleManifest The serialized module manifest to be deserialized.
     * @return The deserialized ModuleManifestMetaData object.
     */
    ModuleManifestMetaData deserialiseModuleManifest(T moduleManifest);
}
