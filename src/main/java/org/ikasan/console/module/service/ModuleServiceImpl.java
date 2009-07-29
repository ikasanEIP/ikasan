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
        SimpleModule module = null;

        // trade01EAI modules
        module = new SimpleModule("arts-repoTradeSrc");
        module.setDescription("TBA");
        modules.add(module);

        module = new SimpleModule("cityFios-fiosTradeTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("dias-positionSrc");
        module.setDescription("<strong>dias-positionSrc</strong> --> msgBroker-riskPosition --> risk-marsPositionTgt");
        modules.add(module);
        
        module = new SimpleModule("dias-valuationSrc");
        module.setDescription("<strong>dias-valuationSrc</strong> --> msgBroker-riskValuation --> risk-marsValuationTgt");
        modules.add(module);

        module = new SimpleModule("hongKongMurex-tradeSrc");
        module.setDescription("<strong>hongKongMurex-tradeSrc</strong> --> msgBroker-trade --> murex-tradeTgt");
        modules.add(module);
        
        module = new SimpleModule("jvbridge-tradeSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("jvbridge-tradeTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("mhtny-custodyWeb");
        module.setDescription("Gets SWIFT Messages and Returns ACKs");
        modules.add(module);
        
        module = new SimpleModule("msgBroker-riskPosition");
        module.setDescription("dias-positionSrc --> <strong>msgBroker-riskPosition<strong> --> risk-marsPositionTgt<br>" +
        		"murex-positionSrc --> <strong>msgBroker-riskPosition<strong> --> risk-marsPositionTgt");
        modules.add(module);

        module = new SimpleModule("msgBroker-riskValuation");
        module.setDescription("dias-valuationSrc --> <strong>msgBroker-riskValuation<strong> --> risk-marsValuationTgt");
        modules.add(module);
        
        module = new SimpleModule("msgBroker-trade");
        module.setDescription("murex-tradeSrcBridge --> <strong>msgBroker-trade<strong> --> stratus-tradeTgt<br>" +
                 "hongKongMurex-tradeSrc --> <strong>msgBroker-trade<strong> --> murex-tradeTgt");
        
        module = new SimpleModule("msgBroker-trax2");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("murex-positionSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("murex-tradeSrcBridge");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("murex-tradeTgtBridge");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("risk-marsPositionTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("risk-marsValuationTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("stratus-tradeTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("stratus-trax2Src");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("stratus-trax2Tgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("trax2-trax2Gateway");
        module.setDescription("TBA");
        modules.add(module);
        
        // static01EAI modules
        module = new SimpleModule("blbgTf-positionSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("dias-assetTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("finCal-calendarSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("intellimatch-assetTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("intellimatch-positionTgt");
        module.setDescription("TBA");
        modules.add(module);

        module = new SimpleModule("jvbridge-staticSrc");
        module.setDescription("<strong>jvbridge-staticSrc</strong> (cmfAsset &amp; cmfParty Messages) --> msgBroker-static --> risk-marsStaticTgt<br>" +
                "<strong>jvbridge-staticSrc</strong> (cmfAsset &amp; cmfParty Messages) --> msgBroker-static --> stratus-staticTgt");
        modules.add(module);
        
        module = new SimpleModule("jvbridge-staticTgt");
        module.setDescription("Receives error messages from all Static Data flows and sends them to Message Manager");
        modules.add(module);

        module = new SimpleModule("mace-staticCBSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("mars-exceptionSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("mars-exposureSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("mars-issuerSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("mars-positionSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("mars-staticTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("moodys-ratingSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("msgBroker-rating");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("msgBroker-risk");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("msgBroker-static");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("murex-staticTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("murex-staticTgtBridge");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("murex-staticCBTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("rdw-staticSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("risk-marsStaticTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("sAndP-assetRatingSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("sAndP-partyRating");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("stratus-positionTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("stratus-staticTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("xsp-assetTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        // static02EAI
        module = new SimpleModule("arts-assetTgt");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("arts-exceptionSrc");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("blbgDl-perSecurity");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("msgBroker-exception");
        module.setDescription("TBA");
        modules.add(module);
        
        module = new SimpleModule("rdw-asset");
        module.setDescription("Request securities from Bloomberg Data Lisence. Process cmfAsset reply and send to RDW.");
        modules.add(module);
        
        module = new SimpleModule("rdw-assetSrc");
        module.setDescription("Providing RDW approved securities and transforming them to common message format (cmfAsset).");
        modules.add(module);
        
        module = new SimpleModule("rdw-calendarTgt");
        module.setDescription("Provides FinCal calendar data to RDW. This includes holiday changes and annual refresh file.");
        modules.add(module);
        
        module = new SimpleModule("rdw-exception");
        module.setDescription("Handles exception notification/response between RDW and Message Manager.");
        modules.add(module);
        
        module = new SimpleModule("rdw-partySrc");
        module.setDescription("Providing RDW approved parties and transforming them to common message format (cmfParty).");
        modules.add(module);
        
        module = new SimpleModule("rdw-price");
        module.setDescription("Request prices from Bloomberg Data Lisence. Process cmfPrice reply and send to RDW.");
        modules.add(module);
        
        module = new SimpleModule("rdw-ratingTgt");
        module.setDescription("Provides party and security ratings from Moody's and S&P to RDW.");
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
