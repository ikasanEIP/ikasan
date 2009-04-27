/* 
 * $Id: ModuleFactoryBean.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/module/loader/ModuleFactoryBean.java $
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.framework.module.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.framework.module.Module;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * <code>FactoryBean</code> for loading a <code>Module</code> from various bean definition files available on the
 * classpath and on the file system.
 * 
 * The core module definition files are expected to be found on the classpath, whilst any environmentally dependent
 * beans are expected to be preloaded from files found on the filesystem, in the environmentalisation directory
 * 
 * @author Ikasan Development Team
 */
public class ModuleFactoryBean implements FactoryBean, ApplicationContextAware
{
    /** System file separator */
    protected static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /** The parent application context */
    private ApplicationContext parentContext;

    /** Logger for this class */
    private Logger logger = Logger.getLogger(ModuleFactoryBean.class);

    /** List of environment bean definitions */
    private List<String> environmentalisationBeanDefinitionFileNameList;

    /** List of module bean definitions */
    private List<String> moduleBeanDefinitionFileNameList;

    /** THe directory for the environmentalisation */
    private File environmentalisationDirectory;

    /** The name of the module bean */
    private String moduleBeanName;

    /** Module for which we are a factory */
    private Module module;

    /**
     * Constructor
     * 
     * @param environmentalisationBeanDefinitionFileNameList - List of Strings representing the relative names of bean
     *            definition files used for environmentalising the bean definitions, relative to the
     *            environmentalisationDirectory
     * @param moduleBeanDefinitionFileNameList - List of Strings representing the names of the bean definition files
     *            expected to be found on the classpath
     * @param environmentalisationDirectory - directory in which to find environmentalisation bean definitions
     * @param moduleBeanName - name of id of the module we wish to load from within the bean definition
     * 
     */
    public ModuleFactoryBean(List<String> environmentalisationBeanDefinitionFileNameList,
            List<String> moduleBeanDefinitionFileNameList, File environmentalisationDirectory, String moduleBeanName)
    {
        this.environmentalisationBeanDefinitionFileNameList = environmentalisationBeanDefinitionFileNameList;
        this.moduleBeanDefinitionFileNameList = moduleBeanDefinitionFileNameList;
        this.environmentalisationDirectory = environmentalisationDirectory;
        this.moduleBeanName = moduleBeanName;
    }

    /**
     * Instantiate this module
     * 
     * @param environmentalisationBeanDefFileNameList - List of Strings representing the relative names of bean
     *            definition files used for environmentalising the bean definitions, relative to the
     *            environmentalisationDirectory
     * @param moduleBeanDefFileNameList - List of Strings representing the names of the bean definition files
     *            expected to be found on the classpath
     * @param environmentalisationDir - directory in which to find environmentalisation bean definitions
     * @param beanName - name of id of the module we wish to load from within the bean definition
     */
    private void instantiateModule(List<String> environmentalisationBeanDefFileNameList,
            List<String> moduleBeanDefFileNameList, File environmentalisationDir, String beanName)
    {
        logger.info("instantiateModule called for moduleBeanName");
        if (!environmentalisationDir.exists())
        {
            throw new RuntimeException("environmentalisation directory cannot be found ["
                    + environmentalisationDir.getAbsolutePath() + "]");
        }
        ApplicationContext moduleEnvironmentalisation = null;
        List<String> environmentalisationBeanDefinitionPathList = new ArrayList<String>();
        for (String relativePath : environmentalisationBeanDefFileNameList)
        {
            String cleanRelativePath = relativePath.trim();
            File environmentalisationBeanDefinitionFile = new File(environmentalisationDir, cleanRelativePath);
            if (!environmentalisationBeanDefinitionFile.exists())
            {
                throw new RuntimeException("environmentalisation file cannot be found ["
                        + environmentalisationBeanDefinitionFile.getAbsolutePath() + "]");
            }
            environmentalisationBeanDefinitionPathList.add(FILE_SEPARATOR
                    + environmentalisationBeanDefinitionFile.getAbsolutePath());
        }
        String[] environmentalisationBeanDefinitionPath = new String[environmentalisationBeanDefinitionPathList.size()];
        environmentalisationBeanDefinitionPathList.toArray(environmentalisationBeanDefinitionPath);
        moduleEnvironmentalisation = new FileSystemXmlApplicationContext(environmentalisationBeanDefinitionPath,
            parentContext);
        List<String> moduleBeanDefinitionList = new ArrayList<String>();
        for (String moduleBeanDefinitionFileName : moduleBeanDefFileNameList)
        {
            moduleBeanDefinitionList.add(moduleBeanDefinitionFileName.trim());
        }
        String[] moduleBeanDefinitionPath = new String[moduleBeanDefinitionList.size()];
        moduleBeanDefinitionList.toArray(moduleBeanDefinitionPath);
        ApplicationContext moduleBeanDefintion = new ClassPathXmlApplicationContext(moduleBeanDefinitionPath,
            moduleEnvironmentalisation);
        module = (Module) moduleBeanDefintion.getBean(beanName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject()
    {
        if (module == null)
        {
            instantiateModule(environmentalisationBeanDefinitionFileNameList, moduleBeanDefinitionFileNameList,
                environmentalisationDirectory, moduleBeanName);
        }
        return module;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @SuppressWarnings("unchecked")
    public Class getObjectType()
    {
        return Module.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton()
    {
        return true;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.parentContext = applicationContext;
    }
}
