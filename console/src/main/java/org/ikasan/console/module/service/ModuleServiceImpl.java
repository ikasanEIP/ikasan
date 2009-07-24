/* 
 * $Id$
 * $URL$
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
package org.ikasan.console.module.service;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.framework.module.Module;
import org.ikasan.framework.module.SimpleModule;
import org.ikasan.framework.module.service.ModuleService;

/**
 * Console implementation of <code>ModuleService</code>, until security is
 * implemented this 'dumbed' down version of
 * 
 * @see org.ikasan.framework.module.service.ModuleServiceImpl will be used.
 * 
 * @author Ikasan Development Team
 */
public class ModuleServiceImpl implements ModuleService
{
    /**
     * Constructor
     */
    public ModuleServiceImpl()
    {
        // Do Nothing
    }

    /**
     * Get a list of modules, in this case return null
     * 
     * @see org.ikasan.framework.module.service.ModuleService#getModules()
     */
    public List<Module> getModules()
    {
        List<Module> modules = new ArrayList<Module>();
        Module module = null;
        // trade01EAI modules
        module = new SimpleModule("dias-positionSrc");
        modules.add(module);
        module = new SimpleModule("dias-valuationSrc");
        modules.add(module);
        module = new SimpleModule("hongKongMurex-tradeSrc");
        modules.add(module);
        module = new SimpleModule("jvbridge-tradeTgt");
        modules.add(module);
        module = new SimpleModule("mhtny-custodyWeb");
        modules.add(module);
        module = new SimpleModule("msgBroker-riskPosition");
        modules.add(module);
        module = new SimpleModule("msgBroker-riskValuation");
        modules.add(module);
        module = new SimpleModule("msgBroker-trade");
        modules.add(module);
        module = new SimpleModule("msgBroker-trax2");
        modules.add(module);
        module = new SimpleModule("murex-positionSrc");
        modules.add(module);
        module = new SimpleModule("murex-tradeSrcBridge");
        modules.add(module);
        module = new SimpleModule("murex-tradeTgtBridge");
        modules.add(module);
        module = new SimpleModule("risk-marsPositionTgt");
        modules.add(module);
        module = new SimpleModule("risk-marsValuationTgt");
        modules.add(module);
        module = new SimpleModule("stratus-tradeTgt");
        modules.add(module);
        module = new SimpleModule("stratus-trax2Src");
        modules.add(module);
        module = new SimpleModule("stratus-trax2Tgt");
        modules.add(module);
        module = new SimpleModule("trax2-trax2Gateway");
        // static01EAI modules
        module = new SimpleModule("blbgTf-positionSrc");
        modules.add(module);
        module = new SimpleModule("dias-assetTgt");
        modules.add(module);
        module = new SimpleModule("finCal-calendarSrc");
        modules.add(module);
        module = new SimpleModule("intellimatch-assetTgt");
        modules.add(module);
        module = new SimpleModule("intellimatch-positionTgt");
        modules.add(module);
        module = new SimpleModule("jvbridge-staticSrc");
        modules.add(module);
        module = new SimpleModule("jvbridge-staticTgt");
        modules.add(module);
        module = new SimpleModule("mace-staticCBSrc");
        modules.add(module);
        module = new SimpleModule("mars-exceptionSrc");
        modules.add(module);
        module = new SimpleModule("mars-exposureSrc");
        modules.add(module);
        module = new SimpleModule("mars-issuerSrc");
        modules.add(module);
        module = new SimpleModule("mars-positionSrc");
        modules.add(module);
        module = new SimpleModule("mars-staticTgt");
        modules.add(module);
        module = new SimpleModule("moodys-ratingSrc");
        modules.add(module);
        module = new SimpleModule("msgBroker-rating");
        modules.add(module);
        module = new SimpleModule("msgBroker-risk");
        modules.add(module);
        module = new SimpleModule("msgBroker-static");
        modules.add(module);
        module = new SimpleModule("murex-staticTgt");
        modules.add(module);
        module = new SimpleModule("murex-staticTgtBridge");
        modules.add(module);
        module = new SimpleModule("murex-staticCBTgt");
        modules.add(module);
        module = new SimpleModule("rdw-staticSrc");
        modules.add(module);
        module = new SimpleModule("risk-marsStaticTgt");
        modules.add(module);
        module = new SimpleModule("sAndP-assetRatingSrc");
        modules.add(module);
        module = new SimpleModule("sAndP-partyRating");
        modules.add(module);
        module = new SimpleModule("stratus-positionTgt");
        modules.add(module);
        module = new SimpleModule("stratus-staticTgt");
        modules.add(module);
        module = new SimpleModule("xsp-assetTgt");
        modules.add(module);
        
        // static02EAI
        module = new SimpleModule("arts-assetTgt");
        modules.add(module);
        module = new SimpleModule("arts-exceptionSrc");
        modules.add(module);
        module = new SimpleModule("blbgDl-perSecurity");
        modules.add(module);
        module = new SimpleModule("msgBroker-exception");
        modules.add(module);
        module = new SimpleModule("rdw-asset");
        modules.add(module);
        module = new SimpleModule("rdw-assetSrc");
        modules.add(module);
        module = new SimpleModule("rdw-calendarTgt");
        modules.add(module);
        module = new SimpleModule("rdw-exception");
        modules.add(module);
        module = new SimpleModule("rdw-partySrc");
        modules.add(module);
        module = new SimpleModule("rdw-price");
        modules.add(module);
        module = new SimpleModule("rdw-ratingTgt");
        modules.add(module);
        return modules;
    }

    /**
     * Get the module given a module name, in this case return null
     * 
     * @see org.ikasan.framework.module.service.ModuleService#getModule(java.lang
     *      .String) Suppress warning because we are deliberately not using the
     *      parameter
     */
    public Module getModule(@SuppressWarnings("unused") String moduleName)
    {
        return null;
    }

    /**
     * Stop the initiator
     * 
     * @see org.ikasan.framework.module.service.ModuleService#stopInitiator(java.
     *      lang.String, java.lang.String, java.lang.String) Suppress warning
     *      because we are deliberately not using the parameters
     */
    public void stopInitiator(@SuppressWarnings("unused") String moduleName, @SuppressWarnings("unused") String initiatorName,
            @SuppressWarnings("unused") String actor)
    {
        // Do Nothing
    }

    /**
     * Start the initiator
     * 
     * @see org.ikasan.framework.module.service.ModuleService#startInitiator(java
     *      .lang.String, java.lang.String, java.lang.String) Suppress warning
     *      because we are deliberately not using the parameters
     */
    public void startInitiator(@SuppressWarnings("unused") String moduleName, @SuppressWarnings("unused") String initiatorName,
            @SuppressWarnings("unused") String actor)
    {
        // Do Nothing
    }
}
