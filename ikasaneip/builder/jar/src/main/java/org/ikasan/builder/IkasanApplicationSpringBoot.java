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
package org.ikasan.builder;

import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.module.ModuleInitialisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication
public class IkasanApplicationSpringBoot implements IkasanApplication
{
    /** logger */
    private static Logger logger = LoggerFactory.getLogger(IkasanApplicationSpringBoot.class);

    ApplicationContext context;

    public IkasanApplicationSpringBoot(String[] args)
    {
        this.context = SpringApplication.run(IkasanApplicationSpringBoot.class, args);
    }

    IkasanApplicationSpringBoot()
    {
    }

    public BuilderFactory getBuilderFactory()
    {
        return this.context.getBean(BuilderFactory.class);
    }

    public void run(Module module)
    {
        ModuleInitialisationService service = this.context.getBean(ModuleInitialisationService.class);
        service.register(module);
        logger.info("Module [" + module.getName() + "] successfully bootstrapped.");
    }

    // TODO - add close or shutdown per module ?
    public void close()
    {
        SpringApplication.exit(this.context, new ExitCodeGenerator(){
            @Override public int getExitCode()
            {
                return 0;
            }
        });
    }

    @Override
    public Module getModule(String moduleName)
    {
        ModuleContainer moduleContainer = this.context.getBean(ModuleContainer.class);
        return moduleContainer.getModule(moduleName);
    }

    @Override
    public List<Module> getModules()
    {
        ModuleContainer moduleContainer = this.context.getBean(ModuleContainer.class);
        return moduleContainer.getModules();
    }

    @Override
    public <COMPONENT> COMPONENT getBean(Class className)
    {
        return (COMPONENT) context.getBean(className);
    }

    @Override
    public <COMPONENT> COMPONENT getBean(String name, Class className)
    {
        return (COMPONENT) context.getBean(name, className);
    }

}