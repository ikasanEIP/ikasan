package org.ikasan.module.builder.service;

import org.ikasan.module.builder.model.component.BeanComponent;
import org.ikasan.module.builder.model.component.ComponentConfigurationMetaData;
import org.ikasan.module.builder.model.component.ComponentTypeParameter;
import org.ikasan.spec.metadata.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModuleManifestMetaDataLocallyDeclaredBeansModelAdapter {


    /**
     * Adapt ModuleManifestMetaData to a list of Component objects based on the given module base package.
     *
     * @param moduleManifestMetaData The ModuleManifestMetaData to adapt.
     * @param moduleBasePackage The base package of the module to filter components by.
     * @return A list of Component objects filtered by the module base package.
     */
    public List<BeanComponent> adapt(ModuleManifestMetaData moduleManifestMetaData, String moduleBasePackage)
    {
        Map<String, ConstructorMetaData> constructorMetaDataMap
            = this.getConstructorMetaDataMap(moduleManifestMetaData.getConstructorMetaData());

        List<BeanComponent> results = new ArrayList<>();
        for (BeanDefinitionMetaData beanDefinitionMetaData : moduleManifestMetaData.getBeanDefinitionMetaData()) {
            if(beanDefinitionMetaData.getBeanResource().startsWith(moduleBasePackage)
                && !beanDefinitionMetaData.getBeanResource().toLowerCase().contains("Test".toLowerCase())
                && !beanDefinitionMetaData.getBeanClass().equals("org.ikasan.module.SimpleModule")
                && !beanDefinitionMetaData.getBeanClass().equals("org.ikasan.module.SimpleModule.VisitingInvokerFlow")) {
                if(moduleManifestMetaData.getModuleMetaData().getFlows().stream()
                    .flatMap(flowMetaData -> flowMetaData.getFlowElements().stream())
                    .filter(flowElementMetaData -> flowElementMetaData.getComponentName().replace(" ", "")
                        .toLowerCase().equals(beanDefinitionMetaData.getBeanName().toLowerCase())).findFirst().isPresent()) {
                    continue;
                }

                BeanComponent component = new BeanComponent();
                component.setName(beanDefinitionMetaData.getBeanName());
                component.setLocal(beanDefinitionMetaData.getBeanClass().startsWith(moduleBasePackage));
                component.setImplementingClass(beanDefinitionMetaData.getBeanClass());
                component.setClassName(beanDefinitionMetaData.getBeanClass().substring(
                    beanDefinitionMetaData.getBeanClass().lastIndexOf(".") + 1
                        ,  beanDefinitionMetaData.getBeanClass().length()));

                // Deal with the possibility of inner classes!
                if(component.getClassName().contains("$")) {
                    component.setClassName(component.getClassName()
                        .substring(component.getClassName().indexOf("$")+1));
                    component.setImplementingClass(beanDefinitionMetaData.getBeanClass()
                        .substring(0, beanDefinitionMetaData.getBeanClass().lastIndexOf(".")+1)
                        + component.getClassName());
                }

                component.setClassPackage(beanDefinitionMetaData.getBeanClass().substring(0,
                    beanDefinitionMetaData.getBeanClass().lastIndexOf(".")));
                if (constructorMetaDataMap.containsKey(beanDefinitionMetaData.getBeanName())) {
                    component.setConstructorMetaData
                        (List.of(constructorMetaDataMap.get(beanDefinitionMetaData.getBeanName())));
                }


                if(!results.contains(component)) {
                    results.add(component);
                }
            }
        }
        return results;
    }

    /**
     * Retrieves a mapping of component names to ConstructorMetaData objects.
     *
     * @param constructorMetaDataList a list of ConstructorMetaData objects
     * @return a map where the key is the component name and the value is the corresponding ConstructorMetaData object
     */
    private Map<String, ConstructorMetaData> getConstructorMetaDataMap(List<ConstructorMetaData> constructorMetaDataList) {
        return constructorMetaDataList.stream()
            .collect(Collectors.toMap(ConstructorMetaData::getComponentName, Function.identity(), (first, second) -> first));
    }
}
