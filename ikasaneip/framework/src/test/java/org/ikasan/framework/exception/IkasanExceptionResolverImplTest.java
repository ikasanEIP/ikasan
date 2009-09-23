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
package org.ikasan.framework.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.ikasan.common.CommonExceptionType;
import org.ikasan.common.CommonRuntimeException;
//import org.ikasan.framework.FrameworkRuntimeException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class supports the <code>IkasanExceptionResolver</code> concrete
 * implementation class.
 * 
 * @author Ikasan Development Team
 */
public class IkasanExceptionResolverImplTest
{
    //
    // empty collection instances
    /** module Exception definitions */
    List<DefaultExceptionDefinition> moduleExceptionDefs;
    /** specific Exception definitions */
    List<DefaultExceptionDefinition> componentExceptionDefs;
    /** component specific Exception definitions */
    Map<String,List<DefaultExceptionDefinition>> componentExceptionDefsMap;
    
    //
    // dummy static test data


    /** action */
    private static IkasanExceptionAction action = 
        new IkasanExceptionActionImpl(IkasanExceptionActionType.ROLLBACK_RETRY,
                new Long(10), new Integer(10));

    /** exception class name */
    private static String exceptionClassName = 
        Exception.class.getName();

    /** commonRuntimeExceptionClassName class name */
    private static String commonRuntimeExceptionClassName = 
        CommonRuntimeException.class.getName();
    
    //
    // mutable test data
    
    /**
     * Setup runs before each test
     */
    @Before
    public void setUp()
    {
        //
        // set up module exception defs
        moduleExceptionDefs = new ArrayList<DefaultExceptionDefinition>();
        /** specific Exception definitions */
        componentExceptionDefs = new ArrayList<DefaultExceptionDefinition>();
        /** component specific Exception definitions */
        componentExceptionDefsMap = new HashMap<String,List<DefaultExceptionDefinition>>();
    }

    /**
     * Test the emergency stop definition to ensure we are always getting
     * the ROLLBACK_STOP enumeration.
     */
    @Test
    public void testEmergencyStop()
    {
        //
        // invoke
        IkasanExceptionResolution emergencyResolution = 
            IkasanExceptionResolutionImpl.getEmergencyResolution();

        //
        // check
        Assert.assertTrue(emergencyResolution.getAction().getType().equals(IkasanExceptionActionType.ROLLBACK_STOP));
    }

    /**
     * Test successful lookup of an exception against the Component exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testComponentExceptionResolverSeed_E_Match_E()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        String componentName = "myCompName";
        
        // set-up - create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, exceptionClassName, null);
        componentExceptionDefs.add(exceptionDef);
        componentExceptionDefsMap.put(componentName, componentExceptionDefs);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs, componentExceptionDefsMap);

        // set-up - create the exception to be matched
        Throwable t = new Exception("known test exception");
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(componentName, t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }

    /**
     * Test successful lookup of an exception against the Component exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testComponentExceptionResolverSeed_E_T_Match_E_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        String componentName = "myCompName";
        
        // set-up - create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        componentExceptionDefs.add(exceptionDef);
        componentExceptionDefsMap.put(componentName, componentExceptionDefs);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs, componentExceptionDefsMap);

        // set-up - create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception", 
                CommonExceptionType.FAILED_XML_VALIDATION);
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(componentName, t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }

    /**
     * Test successful lookup of an exception against the Component exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testComponentExceptionResolverSeed_E_Match_E_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        String componentName = "myCompName";
        
        // set-up - create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, null);
        componentExceptionDefs.add(exceptionDef);
        componentExceptionDefsMap.put(componentName, componentExceptionDefs);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs, componentExceptionDefsMap);

        // set-up - create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception", 
                CommonExceptionType.FAILED_XML_VALIDATION);
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(componentName, t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }

    /**
     * Test successful lookup of an exception + type against the Component exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testComponentExceptionResolverSeed_E_T_Match_E()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        String componentName = "myCompName";
        
        // set-up - create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        componentExceptionDefs.add(exceptionDef);
        componentExceptionDefsMap.put(componentName, componentExceptionDefs);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs, componentExceptionDefsMap);

        // set-up - create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception");
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(componentName, t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }
    
    /**
     * Test successful lookup of an exception + type against the Component exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test(expected = IkasanExceptionResolutionNotFoundException.class)
    public void testComponentExceptionResolverSeed_E_T_Nomatch_E_Nomatch_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        String componentName = "myCompName";
        
        // set-up - create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        componentExceptionDefs.add(exceptionDef);
        componentExceptionDefsMap.put(componentName, componentExceptionDefs);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs, componentExceptionDefsMap);

        // set-up - create the exception to be matched
        Throwable t = new Exception("known test exception");
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(componentName, t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }

    /**
     * Test lookup of an exception + type where exception only exists 
     * (ie. no type defined) against the Component exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testComponentExceptionResolverSeed_E_T_Match_E_Nomatch_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        String componentName = "myCompName";
        
        // create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        componentExceptionDefs.add(exceptionDef);
        componentExceptionDefsMap.put(componentName, componentExceptionDefs);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs, componentExceptionDefsMap);

        // create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception", 
                CommonExceptionType.ENVELOPE_INSTANTIATION_FAILED);
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(componentName, t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }
    
    /**
     * Test lookup of an exception + type where exception only exists 
     * (ie. no type defined) against the Component exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test(expected = IkasanExceptionResolutionNotFoundException.class)
    public void testComponentExceptionResolverSeed_E_T_Nomatch_E_Match_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        String componentName = "myCompName";
        
        // create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        componentExceptionDefs.add(exceptionDef);
        componentExceptionDefsMap.put(componentName, componentExceptionDefs);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs, componentExceptionDefsMap);

        // create the exception to be matched
//        Throwable t = new FrameworkRuntimeException("known test exception", 
//                CommonExceptionType.FAILED_XML_VALIDATION);
        Throwable t = new RuntimeException("known test exception");
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(componentName, t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }
    
    /**
     * Test lookup of an exception + type where neither exists 
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test(expected = IkasanExceptionResolutionNotFoundException.class)
    public void testComponentExceptionResolverSeed_E_T_Nomatch_E()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        String componentName = "myCompName";
        
        // create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, exceptionClassName, null);
        componentExceptionDefs.add(exceptionDef);
        componentExceptionDefsMap.put(componentName, componentExceptionDefs);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs, componentExceptionDefsMap);

        // create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception", 
                CommonExceptionType.FAILED_XML_VALIDATION);
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(componentName, t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }

    /**
     * Test successful lookup of an exception against the module exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testModuleExceptionResolverSeed_E_Match_E()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        
        // set-up - create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, exceptionClassName, null);
        moduleExceptionDefs.add(exceptionDef);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs);

        // set-up - create the exception to be matched
        Throwable t = new Exception("known test exception");
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }

    /**
     * Test successful lookup of an exception against the module exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testModuleExceptionResolverSeed_E_T_Match_E_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        
        // set-up - create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        moduleExceptionDefs.add(exceptionDef);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs);

        // set-up - create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception", 
                CommonExceptionType.FAILED_XML_VALIDATION);
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }

    /**
     * Test successful lookup of an exception against the module exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testModuleExceptionResolverSeed_E_Match_E_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        
        // set-up - create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, null);
        moduleExceptionDefs.add(exceptionDef);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs);

        // set-up - create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception", 
                CommonExceptionType.FAILED_XML_VALIDATION);
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }

    /**
     * Test successful lookup of an exception + type against the module exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testModuleExceptionResolverSeed_E_T_Match_E()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        
        // set-up - create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        moduleExceptionDefs.add(exceptionDef);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs);

        // set-up - create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception");
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }
    
    /**
     * Test successful lookup of an exception + type against the module exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test(expected = IkasanExceptionResolutionNotFoundException.class)
    public void testModuleExceptionResolverSeed_E_T_Nomatch_E_Nomatch_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String id = "matchedExceptionId";
        
        // set-up - create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        moduleExceptionDefs.add(exceptionDef);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs);

        // set-up - create the exception to be matched
        Throwable t = new Exception("known test exception");
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }

    /**
     * Test lookup of an exception + type where exception only exists 
     * (ie. no type defined) against the module exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testModuleExceptionResolverSeed_E_T_Match_E_Nomatch_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        String id = "matchedExceptionId";
        
        //
        // set-up
        
        // create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        moduleExceptionDefs.add(exceptionDef);

        // create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs);

        // create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception", 
                CommonExceptionType.ENVELOPE_INSTANTIATION_FAILED);
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }
    
    /**
     * Test lookup of an exception + type where exception only exists 
     * (ie. no type defined) against the module exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test(expected = IkasanExceptionResolutionNotFoundException.class)
    public void testModuleExceptionResolverSeed_E_T_Nomatch_E_Match_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        String id = "matchedExceptionId";
        
        //
        // set-up
        
        // create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        moduleExceptionDefs.add(exceptionDef);

        // create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs);

        // create the exception to be matched
//        Throwable t = new FrameworkRuntimeException("known test exception", 
//                CommonExceptionType.FAILED_XML_VALIDATION);
        Throwable t = new RuntimeException("known test exception");
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }
    
    /**
     * Test lookup of an exception + type where neither exists 
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test(expected = IkasanExceptionResolutionNotFoundException.class)
    public void testModuleExceptionResolverSeed_E_T_Nomatch_E()
        throws IkasanExceptionResolutionNotFoundException
    {
        String id = "matchedExceptionId";
        
        //
        // set-up
        
        // create resolution
        IkasanExceptionResolution myResolution = new IkasanExceptionResolutionImpl(id, action);

        // associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myResolution, exceptionClassName, null);
        moduleExceptionDefs.add(exceptionDef);

        // create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs);

        // create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception", 
                CommonExceptionType.FAILED_XML_VALIDATION);
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(id));
    }

    /**
     * Test successful lookup of an exception against the Component exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testComponentOverridingModuleExceptionResolverSeed_E_T_Match_E_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String componentId = "componentMatchedExceptionId";
        String moduleId = "moduleMatchedExceptionId";
        String componentName = "myCompName";
        
        // set-up - create resolution
        IkasanExceptionResolution myModuleResolution = new IkasanExceptionResolutionImpl(moduleId, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myModuleResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        moduleExceptionDefs.add(exceptionDef);
        
        // set-up - create resolution
        IkasanExceptionResolution myComponentResolution = new IkasanExceptionResolutionImpl(componentId, action);

        // set-up - associated within an exception def
        exceptionDef = new DefaultExceptionDefinition(myComponentResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        componentExceptionDefs.add(exceptionDef);
        componentExceptionDefsMap.put(componentName, componentExceptionDefs);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs, componentExceptionDefsMap);

        // set-up - create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception", 
                CommonExceptionType.FAILED_XML_VALIDATION);
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(componentName, t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(componentId));
    }
    
    /**
     * Test successful lookup of an exception against the Component exception defs.
     * @throws IkasanExceptionResolutionNotFoundException 
     */
    @Test
    public void testComponentNotOverridingModuleException_DueToDifferent_T_ResolverSeed_E_T_Match_E_T()
        throws IkasanExceptionResolutionNotFoundException
    {
        //
        // set-up
        String componentId = "componentMatchedExceptionId";
        String moduleId = "moduleMatchedExceptionId";
        String componentName = "myCompName";
        
        // set-up - create resolution
        IkasanExceptionResolution myModuleResolution = new IkasanExceptionResolutionImpl(moduleId, action);

        // set-up - associated within an exception def
        DefaultExceptionDefinition exceptionDef = new DefaultExceptionDefinition(myModuleResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.FAILED_XML_VALIDATION);
        moduleExceptionDefs.add(exceptionDef);
        
        // set-up - create resolution
        IkasanExceptionResolution myComponentResolution = new IkasanExceptionResolutionImpl(componentId, action);

        // set-up - associated within an exception def
        exceptionDef = new DefaultExceptionDefinition(myComponentResolution, 
                commonRuntimeExceptionClassName, CommonExceptionType.ENVELOPE_INSTANTIATION_FAILED);
        componentExceptionDefs.add(exceptionDef);
        componentExceptionDefsMap.put(componentName, componentExceptionDefs);

        // set-up - create a resolver seeded with the exceptionDefs
        IkasanExceptionResolver ier = new IkasanExceptionResolverImpl(moduleExceptionDefs, componentExceptionDefsMap);

        // set-up - create the exception to be matched
        Throwable t = new CommonRuntimeException("known test exception", 
                CommonExceptionType.FAILED_XML_VALIDATION);
        
        //
        // invoke
        IkasanExceptionResolution resolution = ier.resolve(componentName, t);

        //
        // check result
        Assert.assertTrue(resolution.getId().equals(moduleId));
    }
    
    /**
     * Teardown after each test
     */
    @After
    public void tearDown()
    {
        moduleExceptionDefs = null; 
        componentExceptionDefs = null;
        componentExceptionDefsMap = null;
    }

    /**
     * The suite is this class
     * 
     * @return JUnit Test class
     */
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(IkasanExceptionResolverImplTest.class);
    }
}
