/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * (C) Copyright Mizuho Securities USA
 * ====================================================================
 *
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

/**
 * @author jeffmitchell
 *
 */
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
}
