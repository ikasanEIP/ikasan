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
package org.ikasan.bootstrap.console.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.flow.Flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/module")
public class ModuleController
{
    private ModuleContainer moduleContainer;
    
    private ModuleService moduleService;
    
    @Autowired
    public void setModuleService(ModuleService moduleService)
    {
        this.moduleService = moduleService;
    }
    
    @Autowired
    public void setModuleContainer(ModuleContainer moduleContainer)
    {
        this.moduleContainer = moduleContainer;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String getModules(Model model) 
    {
        List<Module> modules = this.moduleContainer.getModules();
        Map<String,Map<String,String>> moduleMap = new HashMap<String, Map<String,String>>();
        for(Module<Flow> module:modules)
        {
            Map<String,String> flows = new HashMap<String,String>();
            for(Flow flow:module.getFlows())
            {
                flows.put(flow.getName(), flow.getState());
            }
            moduleMap.put(module.getName(), flows);
        }
        
        model.addAttribute("moduleMap", moduleMap);
        return "moduleList";
    }

    @RequestMapping(value="/stop/{moduleName}/{flowName}", method = RequestMethod.GET)
    public String stopFlow(@PathVariable String moduleName, @PathVariable String flowName, Model model) 
    {
        List<Module> modules = this.moduleContainer.getModules();
        for(Module<Flow> module:modules)
        {
            if(module.getName().equals(moduleName))
            {
                for(Flow flow:module.getFlows())
                {
                    if(flow.getName().equals(flowName))
                    {
                        flow.stop();
                    }
                }
            }
        }
        
        // refresh modules status and return list
        getModules(model);
        return "moduleList";
    }

    @RequestMapping(value="/start/{moduleName}/{flowName}", method = RequestMethod.GET)
    public String startFlow(@PathVariable String moduleName, @PathVariable String flowName, Model model) 
    {
        List<Module> modules = this.moduleContainer.getModules();
        for(Module<Flow> module:modules)
        {
            if(module.getName().equals(moduleName))
            {
                for(Flow flow:module.getFlows())
                {
                    if(flow.getName().equals(flowName))
                    {
                        flow.start();
                    }
                }
            }
        }
        
        // refresh modules status and return list
        getModules(model);
        return "moduleList";
    }
}
