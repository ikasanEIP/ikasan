/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
    private static final Logger logger = Logger.getLogger(ModuleFactoryBean.class);

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
            this.parentContext);
        List<String> moduleBeanDefinitionList = new ArrayList<String>();
        for (String moduleBeanDefinitionFileName : moduleBeanDefFileNameList)
        {
            moduleBeanDefinitionList.add(moduleBeanDefinitionFileName.trim());
        }
        String[] moduleBeanDefinitionPath = new String[moduleBeanDefinitionList.size()];
        moduleBeanDefinitionList.toArray(moduleBeanDefinitionPath);
        ApplicationContext moduleBeanDefintion = new ClassPathXmlApplicationContext(moduleBeanDefinitionPath,
            moduleEnvironmentalisation);
        this.module = (Module) moduleBeanDefintion.getBean(beanName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject()
    {
        if (this.module == null)
        {
            instantiateModule(this.environmentalisationBeanDefinitionFileNameList, this.moduleBeanDefinitionFileNameList,
                this.environmentalisationDirectory, this.moduleBeanName);
        }
        return this.module;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<?> getObjectType()
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
