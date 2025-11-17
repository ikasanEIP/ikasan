package org.ikasan.manifest;

import org.hibernate.Hibernate;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.configurationService.metadata.JsonConfigurationMetaDataExtractor;
import org.ikasan.manifest.model.*;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.metadata.*;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.StartupControl;
import org.ikasan.topology.metadata.JsonModuleMetaDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.info.BuildProperties;
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
import org.springframework.security.access.method.P;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonModuleManifestMetaDataProvider implements ModuleManifestMetaDataProvider<String>, ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(JsonModuleManifestMetaDataProvider.class);
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
            this.populateModulePomMetaData(moduleManifestMetaData);
            this.populateScheduledConsumerMetaData(module, moduleManifestMetaData);
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
            if (genericInterface instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.ParameterizedType parameterizedType
                    = (java.lang.reflect.ParameterizedType) genericInterface;

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

    /**
     * Populates the list of ConstructorMetaData for a given ModuleManifestMetaData object based on the FlowElementMetaData
     * of each FlowMetaData within the module manifest metadata.
     *
     * @param moduleManifestMetaData the ModuleManifestMetaDataImpl object to populate the constructor metadata for
     * @throws ClassNotFoundException If the implementing class specified in FlowElementMetaData cannot be found
     */
    private void populateConstructorDetails(ModuleManifestMetaDataImpl moduleManifestMetaData) throws ClassNotFoundException {
        List<ConstructorMetaData> constructorMetaData = new ArrayList<>();
        for (FlowMetaData flowMetaData : moduleManifestMetaData.getModuleMetaData().getFlows()) {
            for (FlowElementMetaData flowElementMetaData : flowMetaData.getFlowElements()) {
                constructorMetaData.addAll(this.inspectConstructors(flowElementMetaData));
            }
        }
        moduleManifestMetaData.setConstructorMetaData(constructorMetaData);
    }

    /**
     * Inspects the constructors of a given FlowElementMetaData and returns a list of ConstructorMetaData.
     *
     * @param flowElementMetaData the FlowElementMetaData to inspect constructors for
     * @return a list of ConstructorMetaData representing the constructor information
     * @throws ClassNotFoundException if the implementing class specified in FlowElementMetaData cannot be found
     */
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
                if(parameters[i].getType().getName().contains("[L") &&
                    parameters[i].getType().getName().contains(";")) {
                    // Dealing with ... types in java for example String... is represented as
                    // [Ljava.lang.String; when type is described using reflection.
                    typeParameter.setType(parameters[i].getType().getName()
                        .replace("[L", "").replace(";", "") + "...");
                }
                else {
                    typeParameter.setType(parameters[i].getType().getName());
                }
                typeParameters.add(typeParameter);
            }
            constructorMetaData.setConstructorArguments(typeParameters);
            constructorDescriptions.add(constructorMetaData);
        }

        return constructorDescriptions;
    }

    /**
     * Populates the bean definition metadata based on the beans registered in the application context.
     *
     * @param moduleManifestMetaData the ModuleManifestMetaData object to populate the bean definition metadata for
     * @throws IOException if an I/O error occurs while processing the bean definitions
     */
    private void populateBeanDefinitionMetaData(ModuleManifestMetaData moduleManifestMetaData) throws Exception {
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
                    beanDefinitionMetaData.setBeanClass(this.getBeanClassName(beanName));
                    beanDefinitionMetaData.setBeanResource(((GenericBeanDefinition) beanDefinition).getResource().getURL().getPath());
                    beanDefinitionMetaDataList.add(beanDefinitionMetaData);
                }
            } else if (beanDefinition.getClass().getName().contains("ConfigurationClassBeanDefinition") &&
                    abstractBeanDefinition.getSource() instanceof MethodMetadata &&
                    ((MethodMetadata) abstractBeanDefinition.getSource()).getDeclaringClassName().startsWith("com.ikasan.sample.spring.boot")) {
                BeanDefinitionMetaDataImpl beanDefinitionMetaData = new BeanDefinitionMetaDataImpl();
                beanDefinitionMetaData.setBeanName(beanName);
                beanDefinitionMetaData.setType("CONFIGURATION_CLASS_BEAN_DEFINITION");
                beanDefinitionMetaData.setBeanClass(this.getBeanClassName(beanName));
                beanDefinitionMetaData.setBeanResource(((MethodMetadata) abstractBeanDefinition.getSource()).getDeclaringClassName());
                beanDefinitionMetaDataList.add(beanDefinitionMetaData);
            }
        }

        moduleManifestMetaData.setBeanDefinitionMetaData(beanDefinitionMetaDataList);
    }

    /**
     * Retrieves the class name of a bean registered in the application context, considering proxying.
     *
     * @param beanName the name of the bean to retrieve the class name for
     * @return the class name of the bean, considering proxying if applicable
     */
    private String getBeanClassName(String beanName) throws Exception {
        Object bean = applicationContext.getBean(beanName);
        if ( AopUtils.isJdkDynamicProxy(bean) || AopUtils.isAopProxy(bean)
            || AopUtils.isCglibProxy(bean)) {
            return AopProxyUtils.ultimateTargetClass(bean).getName();
        }
        else if(Proxy.isProxyClass(bean.getClass())) {
            return Hibernate.unproxy(bean).getClass().getName();
        }
        else {
            return bean.getClass().getName();
        }
    }

    /**
     * Populates the imported resource metadata for the given ModuleManifestMetaData object based on the annotations
     * @Import and @ImportResource
     *
     * @param moduleManifestMetaData the ModuleManifestMetaData object for which to populate imported resource metadata
     * @throws IOException if an I/O error occurs while processing the imported resource metadata
     */
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

    /**
     * Populates the POM metadata of a module with information such as group ID, artifact ID, and version.
     *
     * @param moduleManifestMetaData the ModuleManifestMetaData object to populate the POM metadata for
     */
    private void populateModulePomMetaData(ModuleManifestMetaData moduleManifestMetaData) {
        BuildProperties buildProperties = (BuildProperties) applicationContext.getBean("buildProperties");

        ModulePomMetaData modulePomMetaData = new ModulePomMetaDataImpl();
        modulePomMetaData.setPomArtefactId(buildProperties.getArtifact());
        modulePomMetaData.setPomGroupId(buildProperties.getGroup());
        modulePomMetaData.setVersion(buildProperties.getVersion());

        moduleManifestMetaData.setModulePomMetaData(modulePomMetaData);
    }

    /**
     * Populates scheduled consumer meta data for a given module and module manifest meta data.
     *
     * @param module the module containing flows to extract scheduled consumer meta data from
     * @param moduleManifestMetaData the module manifest meta data object to populate scheduled consumer meta data
     * @throws ClassNotFoundException if the implementing class specified in FlowElementMetaData cannot be found
     */
    private void populateScheduledConsumerMetaData(Module<Flow> module, ModuleManifestMetaDataImpl moduleManifestMetaData)
        throws Exception {
        List<ScheduledConsumerMetaData> scheduledConsumerMetaDataList = new ArrayList<>();
        for (FlowMetaData flowMetaData : moduleManifestMetaData.getModuleMetaData().getFlows()) {
            if(flowMetaData.getConsumer().getImplementingClass()
                .equals("org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer")) {
                ScheduledConsumerMetaData scheduledConsumerMetaData = new ScheduledConsumerMetaDataImpl();
                scheduledConsumerMetaData.setName(flowMetaData.getConsumer().getComponentName());
                scheduledConsumerMetaData.setFlow(flowMetaData.getName());
                Flow flow = module.getFlows().stream()
                    .filter(f -> f.getName().equals(flowMetaData.getName())).findFirst().get();
                FlowElement flowElement = flow.getFlowElement(flowMetaData.getConsumer().getComponentName());

                if ( AopUtils.isJdkDynamicProxy(flowElement.getFlowComponent())
                    || AopUtils.isAopProxy(flowElement.getFlowComponent())
                    || AopUtils.isCglibProxy(flowElement.getFlowComponent()))
                {
                    ScheduledConsumer target = (ScheduledConsumer) ((Advised)flowElement.getFlowComponent())
                        .getTargetSource().getTarget();
                    scheduledConsumerMetaData.setMessageProviderClass(target.getMessageProvider().getClass().getName());
                }
                else
                {
                    scheduledConsumerMetaData.setMessageProviderClass(flowElement.getFlowComponent().getClass().getName());
                }

                scheduledConsumerMetaDataList.add(scheduledConsumerMetaData);
            }
        }
        moduleManifestMetaData.setScheduledConsumerMetaData(scheduledConsumerMetaDataList);
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
