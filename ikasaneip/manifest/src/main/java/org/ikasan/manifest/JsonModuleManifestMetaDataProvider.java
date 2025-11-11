package org.ikasan.manifest;

import org.ikasan.configurationService.metadata.JsonConfigurationMetaDataExtractor;
import org.ikasan.manifest.model.*;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonModuleManifestMetaDataProvider implements ModuleManifestMetaDataProvider<String>, ApplicationContextAware {
    private org.ikasan.topology.metadata.JsonModuleMetaDataProvider jsonModuleMetaDataProvider;
    private JsonConfigurationMetaDataExtractor jsonConfigurationMetaDataExtractor;
    private ApplicationContext applicationContext;


    /**
     * Constructor for JsonModuleManifestMetaDataProvider class.
     *
     * @param jsonModuleMetaDataProvider         instance of JsonModuleMetaDataProvider to use for metadata extraction
     * @param jsonConfigurationMetaDataExtractor instance of JsonConfigurationMetaDataExtractor to use for metadata extraction
     * @throws IllegalArgumentException if jsonModuleMetaDataProvider or jsonConfigurationMetaDataExtractor is null
     */
    public JsonModuleManifestMetaDataProvider(JsonModuleMetaDataProvider jsonModuleMetaDataProvider,
                                              JsonConfigurationMetaDataExtractor jsonConfigurationMetaDataExtractor) {
        this.jsonModuleMetaDataProvider = jsonModuleMetaDataProvider;
        if (this.jsonModuleMetaDataProvider == null) {
            throw new IllegalArgumentException("jsonModuleMetaDataProvider cannot be null!");
        }
        this.jsonConfigurationMetaDataExtractor = jsonConfigurationMetaDataExtractor;
        if (this.jsonConfigurationMetaDataExtractor == null) {
            throw new IllegalArgumentException("configurationMetaDataProvider cannot be null!");
        }
    }

    @Override
    public ModuleManifestMetaData describeModuleManifest(Module<Flow> module, Map<String, StartupControl> startUpControlMap) {
        ModuleManifestMetaDataImpl moduleManifestMetaData = new ModuleManifestMetaDataImpl();
        try {
            moduleManifestMetaData.setConfigurationMetaData(this.jsonConfigurationMetaDataExtractor.getComponentsConfiguration(module));
            moduleManifestMetaData.setModuleMetaData(this.jsonModuleMetaDataProvider.deserialiseModule
                    (this.jsonModuleMetaDataProvider.describeModule(module, startUpControlMap)));

            List<DependencyHelper.MavenCoordinates> mavenCoordinates = DependencyHelper.getRuntimeDependencies();
            List<DependencyMetaData> dependencyMetaDataList = mavenCoordinates.stream()
                    .map(mavenCoordinate -> {
                        DependencyMetaData dependencyMetaData = new DependencyMetaDataImpl();
                        dependencyMetaData.setArtefact(mavenCoordinate.getArtifactId());
                        dependencyMetaData.setGroup(mavenCoordinate.getGroupId());
                        dependencyMetaData.setVersion(mavenCoordinate.getVersion());
                        return dependencyMetaData;
                    })
                    .collect(Collectors.toList());

            DependencyManagementMetaData dependencyManagementMetaData = new DependencyManagementMetaDataImpl();
            dependencyManagementMetaData.setDependencies(dependencyMetaDataList);

            moduleManifestMetaData.setDependencyManagement(dependencyManagementMetaData);
            this.populateParameterizedTypes(moduleManifestMetaData);
            this.populateConstructorDetails(moduleManifestMetaData);
            this.populateBeanDefinitionMetaData(moduleManifestMetaData);
            this.populateImportedResourceMetadata(moduleManifestMetaData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return moduleManifestMetaData;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Populates the list of ParameterizedType metadata for a given ModuleManifestMetaData object based on the FlowElementMetaData
     * of each FlowMetaData within the module manifest metadata.
     *
     * @param moduleManifestMetaData the ModuleManifestMetaDataImpl object to populate the parameterized types for
     * @throws ClassNotFoundException if the implementing class specified in FlowElementMetaData cannot be found
     */
    private void populateParameterizedTypes(ModuleManifestMetaDataImpl moduleManifestMetaData) throws ClassNotFoundException {
        List<org.ikasan.spec.metadata.ParameterizedType> parameterizedTypes = new ArrayList<>();
        for (FlowMetaData flowMetaData : moduleManifestMetaData.getModuleMetaData().getFlows()) {
            for (FlowElementMetaData flowElementMetaData : flowMetaData.getFlowElements()) {
                org.ikasan.spec.metadata.ParameterizedType type = this.getParameterizedType(flowElementMetaData);

                if (type != null) {
                    parameterizedTypes.add(type);
                }
            }
        }
        moduleManifestMetaData.setParameterizedTypes(parameterizedTypes);
    }

    /**
     * Retrieves the ParameterizedType metadata based on the FlowElementMetaData provided.
     *
     * @param flowElementMetaData the FlowElementMetaData to extract ParameterizedType from
     * @return an instance of org.ikasan.spec.metadata.ParameterizedType representing the parameterized type information
     * @throws ClassNotFoundException if the implementing class specified in FlowElementMetaData cannot be found
     */
    private org.ikasan.spec.metadata.ParameterizedType getParameterizedType(FlowElementMetaData flowElementMetaData) throws ClassNotFoundException {
        Class<?> targetClass = Class.forName(flowElementMetaData.getImplementingClass());
        Type[] genericInterfaces = targetClass.getGenericInterfaces();

        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;

                Type rawType = parameterizedType.getRawType();
                rawType.getTypeName();

                if (rawType.getTypeName().equals(flowElementMetaData.getComponentType())) {
                    org.ikasan.spec.metadata.ParameterizedType parameterizedTypeMetadata = new ParameterizedTypeImpl();
                    parameterizedTypeMetadata.setImplementingClassName(flowElementMetaData.getImplementingClass());
                    List<TypeParameter> typeParameters = new ArrayList<>();
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    for (int i = 0; i < actualTypeArguments.length; i++) {
                        Type argType = actualTypeArguments[i];
                        TypeParameter typeParameter = new TypeParameterImpl();
                        typeParameter.setName(((Class<?>) rawType).getTypeParameters()[i].getName());
                        typeParameter.setType(argType.getTypeName());

                        typeParameters.add(typeParameter);
                    }

                    parameterizedTypeMetadata.setTypeParameters(typeParameters);
                    return parameterizedTypeMetadata;
                }
            }
        }

        return null;
    }

    private void populateConstructorDetails(ModuleManifestMetaDataImpl moduleManifestMetaData) throws ClassNotFoundException {
        List<ConstructorMetaData> constructorMetaData = new ArrayList<>();
        for (FlowMetaData flowMetaData : moduleManifestMetaData.getModuleMetaData().getFlows()) {
            for (FlowElementMetaData flowElementMetaData : flowMetaData.getFlowElements()) {
                constructorMetaData.addAll(this.inspectConstructors(flowElementMetaData));
            }
        }
        moduleManifestMetaData.setConstructorMetaData(constructorMetaData);
    }

    private List<ConstructorMetaData> inspectConstructors(FlowElementMetaData flowElementMetaData) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(flowElementMetaData.getImplementingClass());
        List<ConstructorMetaData> constructorDescriptions = new ArrayList<>();

        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            ConstructorMetaDataImpl constructorMetaData = new ConstructorMetaDataImpl();
            constructorMetaData.setClassName(flowElementMetaData.getImplementingClass());
            constructorMetaData.setComponentName(flowElementMetaData.getComponentName());
            List<TypeParameter> typeParameters = new ArrayList<>();
            Parameter[] parameters = constructor.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                TypeParameterImpl typeParameter = new TypeParameterImpl();
                typeParameter.setName(parameters[i].getName());
                typeParameter.setType(parameters[i].getType().getName());
                typeParameters.add(typeParameter);
            }
            constructorMetaData.setConstructorArguments(typeParameters);
            constructorDescriptions.add(constructorMetaData);
        }

        return constructorDescriptions;
    }

    private void populateBeanDefinitionMetaData(ModuleManifestMetaData moduleManifestMetaData) throws IOException {
        List<BeanDefinitionMetaData> beanDefinitionMetaDataList = new ArrayList<>();
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            AbstractBeanDefinition abstractBeanDefinition = (AbstractBeanDefinition) beanDefinition;
            if (beanDefinition instanceof GenericBeanDefinition) {
                if (((GenericBeanDefinition) beanDefinition).getResource() != null &&
                        !((GenericBeanDefinition) beanDefinition).getResource().getURL().getProtocol().equals("jar") &&
                        ((GenericBeanDefinition) beanDefinition).getResource().getURL().getPath().endsWith(".xml")) {
                    BeanDefinitionMetaDataImpl beanDefinitionMetaData = new BeanDefinitionMetaDataImpl();
                    beanDefinitionMetaData.setBeanName(beanName);
                    beanDefinitionMetaData.setType("XML_BEAN_DEFINITION");
                    beanDefinitionMetaData.setBeanClass(beanDefinition.getBeanClassName());
                    beanDefinitionMetaData.setBeanResource(((GenericBeanDefinition) beanDefinition).getResource().getURL().getPath());
                    beanDefinitionMetaDataList.add(beanDefinitionMetaData);
                }
            } else if (beanDefinition.getClass().getName().contains("ConfigurationClassBeanDefinition") &&
                    abstractBeanDefinition.getSource() instanceof MethodMetadata &&
                    ((MethodMetadata) abstractBeanDefinition.getSource()).getDeclaringClassName().startsWith("com.ikasan.sample.spring.boot")) {
                BeanDefinitionMetaDataImpl beanDefinitionMetaData = new BeanDefinitionMetaDataImpl();
                Object bean = applicationContext.getBean(beanName);
                beanDefinitionMetaData.setBeanName(beanName);
                beanDefinitionMetaData.setType("CONFIGURATION_CLASS_BEAN_DEFINITION");
                beanDefinitionMetaData.setBeanClass(bean.getClass().getName());
                beanDefinitionMetaData.setBeanResource(((MethodMetadata) abstractBeanDefinition.getSource()).getDeclaringClassName());
                beanDefinitionMetaDataList.add(beanDefinitionMetaData);
            }
        }

        moduleManifestMetaData.setBeanDefinitionMetaData(beanDefinitionMetaDataList);
    }

    private void populateImportedResourceMetadata(ModuleManifestMetaData moduleManifestMetaData) throws IOException {
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        List<ImportedResourceMetaData> importedResourceMetaDataList = new ArrayList<>();

        Map<String, Object> migrationBeans = applicationContext.getBeansWithAnnotation(Configuration.class);
        for (Map.Entry<String, Object> entry : migrationBeans.entrySet()) {
            String key = entry.getKey();

            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(ClassUtils.getUserClass(applicationContext.getType(key)).getName());
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

            // Check for and extract values from @Import
            if (annotationMetadata.hasAnnotation(Import.class.getName())) {
                // getAnnotationAttributes returns a map of attribute names to their values
                MultiValueMap<String, Object> attributes = annotationMetadata.getAllAnnotationAttributes(Import.class.getName());
                Class[] importedClasses = (Class[]) attributes.getFirst("value");

                if (importedClasses != null) {
                    for (Class importedClass : importedClasses) {
                        ImportedResourceMetaData importedResourceMetaData = new ImportedResourceMetaDataImpl();
                        importedResourceMetaData.setResourceType(ImportedResourceMetaData.IMPORTED_CONFIGURATION_CLASS);
                        importedResourceMetaData.setSource(ClassUtils.getUserClass(applicationContext.getType(key)).getName());
                        importedResourceMetaData.setResource(importedClass.getName());

                        importedResourceMetaDataList.add(importedResourceMetaData);
                    }
                }
            }

            // Check for and extract values from @ImportResource
            if (annotationMetadata.hasAnnotation(ImportResource.class.getName())) {
                MultiValueMap<String, Object> attributes = annotationMetadata.getAllAnnotationAttributes(ImportResource.class.getName());
                String[] importedResources = (String[]) attributes.getFirst("value");

                if (importedResources != null) {
                    for (String resource : importedResources) {
                        ImportedResourceMetaData importedResourceMetaData = new ImportedResourceMetaDataImpl();
                        importedResourceMetaData.setResourceType(ImportedResourceMetaData.IMPORTED_XML_RESOURCE);
                        importedResourceMetaData.setSource(ClassUtils.getUserClass(applicationContext.getType(key)).getName());
                        importedResourceMetaData.setResource(resource);

                        importedResourceMetaDataList.add(importedResourceMetaData);
                    }
                }
            }

            moduleManifestMetaData.setImportedResourceMetaData(importedResourceMetaDataList);
        }
    }

    @Override
    public String serialiseModuleManifest(ModuleManifestMetaData moduleManifestMetaData) {
        return ModuleManifestMetaDataHelper.serialiseModuleManifest(moduleManifestMetaData);
    }

    @Override
    public ModuleManifestMetaData deserialiseModuleManifest(String moduleManifest) {
        return ModuleManifestMetaDataHelper.deserialiseModuleManifest(moduleManifest);
    }
}
