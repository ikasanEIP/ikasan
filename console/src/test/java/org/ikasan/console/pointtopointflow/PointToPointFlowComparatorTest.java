/*
 * $Id: PointToPointFlowTest.java 965 2009-10-09 15:57:39Z karianna $
 * $URL: https://ikasaneip.svn.sourceforge.net/svnroot/ikasaneip/trunk/ikasaneip/console/src/test/java/org/ikasan/console/pointtopointflow/PointToPointFlowTest.java $
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
package org.ikasan.console.pointtopointflow;

import org.ikasan.console.module.Module;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

/**
 * JUnit based test class for testing PointToPointFlowComparator
 * 
 * @author Ikasan Development Team
 */
public class PointToPointFlowComparatorTest
{
    /**
     * The context that the tests run in, allows for mocking actual concrete
     * classes
     */
    private Mockery context = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
	
	/** First fromModule for testing */
    private Module fromModule1;
    
    /** Second fromModule for testing */
    private Module fromModule2;
    
    /** First toModule for testing */
    private Module toModule1;
    
    /** Second toModule for testing */
    private Module toModule2;

    /** First point to point flow for comparison */
    private PointToPointFlow pointToPointFlow1 = new PointToPointFlow();
    
    /** Second point to point flow for comparison */
    private PointToPointFlow pointToPointFlow2 = new PointToPointFlow();

    /** First point to point flow profile for comparison */
    private PointToPointFlowProfile pointToPointFlowProfile1 = context.mock(PointToPointFlowProfile.class);
    
    /** Second point to point flow profile for comparison */
    private PointToPointFlowProfile pointToPointFlowProfile2 = context.mock(PointToPointFlowProfile.class);
    
    /** The comparator to use */
    private PointToPointFlowComparator comparator = new PointToPointFlowComparator();
    
	/** Comparators can never return this */
    private final static int UNDEFINED = -2;

    /** The comparison result */
    private int comparisonResult = UNDEFINED;
    
    /** Setup - For each test reset the various fields and states */
	@Before
    public void setUp()
    {
    	comparisonResult = UNDEFINED;
    	pointToPointFlow1.setPointToPointFlowProfile(pointToPointFlowProfile1);
    	pointToPointFlow2.setPointToPointFlowProfile(pointToPointFlowProfile2);
    	fromModule1 = null;
    	fromModule2 = null;
    	toModule1 = null;
    	toModule2 = null;
    }
    
    /**
     * Test the compare method with NULL PointToPointFlows
     */
    @Test
    public void testCompareWithNullFlows()
    {
        // Test both are null
        comparisonResult = comparator.compare(null, null);
        
        // Verify
        Assert.assertEquals(0, comparisonResult);

        // Test first flow is null
        comparisonResult = comparator.compare(null, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(-1, comparisonResult);

        // Test 2nd flow is null
        comparisonResult = comparator.compare(pointToPointFlow1, null);
        
        // Verify
        Assert.assertEquals(1, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows that have NULL Profiles
     */
    @Test
    public void testCompareWithNullProfiles()
    {
        // Setup
        this.setModules(new Module(), new Module(), new Module(), new Module());
        pointToPointFlowProfile1 = null;
        pointToPointFlowProfile2 = null;
    	pointToPointFlow1.setPointToPointFlowProfile(pointToPointFlowProfile1);
    	pointToPointFlow2.setPointToPointFlowProfile(pointToPointFlowProfile2);
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(0, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows that have NULL Profiles
     */
    @Test
    public void testCompareWithNullFirstProfile()
    {
        // Setup
        this.setModules(new Module(), new Module(), new Module(), new Module());
        pointToPointFlowProfile1 = null;
    	pointToPointFlow1.setPointToPointFlowProfile(pointToPointFlowProfile1);
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(-1, comparisonResult);
    }

    /**
     * Test the compare method with the second PointToPointFlow having a NULL Profile
     */
    @Test
    public void testCompareWithNullSecondProfile()
    {
        // Setup
        this.setModules(new Module(), new Module(), new Module(), new Module());
        pointToPointFlowProfile2 = null;
    	pointToPointFlow2.setPointToPointFlowProfile(pointToPointFlowProfile2);
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(1, comparisonResult);
    }

    /**
     * Test the compare method with the PointToPointFlowProfiles being different
     */
    @Test
    public void testCompareWithDifferentProfiles()
    {
        // Setup
        this.setModules(new Module(), new Module(), new Module(), new Module());

    	// Expectations
    	context.checking(new Expectations()
        {
            {
                one(pointToPointFlowProfile1).getId();
                will(returnValue(new Long(1)));
                one(pointToPointFlowProfile2).getId();
                will(returnValue(new Long(2)));
            }
        });
    	
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(0, comparisonResult);
    }
    
    /**
     * Test the compare method with PointToPointFlows that have all modules set to NULL
     */
    @Test
    public void testCompareWithAllModulesNull()
    {
        // Setup
        this.setModules(new Module(), new Module(), new Module(), new Module());

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(0, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows with fromModule1 NULL
     */
    @Test
    public void testCompareWithNullFromModule1()
    {
        // Setup
        this.setModules(null, new Module(), new Module(), new Module());

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        

        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(-1, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows with fromModule2 NULL
     */
    @Test
    public void testCompareWithNullFromModule2()
    {
        // Setup
        this.setModules(new Module(), null, new Module(), new Module());

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        

        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(1, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows with toModule1 NULL
     */
    @Test
    public void testCompareWithNullToModule1()
    {
	    // Setup
	    this.setModules(new Module(), new Module(), null, new Module());
	
	    // Expectations
	    this.pointToPointFlowProfileIdExpectations();        
	    
	    // Test
	    comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
	    
	    // Verify	    
	    Assert.assertEquals(1, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows with toModule2 NULL
     */
    @Test
    public void testCompareWithNullToModule2()
    {
	    // Setup
    	Module fromModule1 = new Module();
    	fromModule1.setId(1);
    	Module fromModule2 = new Module();
    	fromModule2.setId(4);
    	Module toModule1 = new Module();
    	toModule1.setId(7);
    	
	    this.setModules(fromModule1, toModule1, fromModule2, null);
	
	    // Expectations
	    this.pointToPointFlowProfileIdExpectations();        
	    
	    // Test
	    comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
	    
	    // Verify
	    Assert.assertEquals(-1, comparisonResult);
    }
    
    /**
     * Test the compare method with PointToPointFlows with both from modules NULL
     */
    @Test
    public void testCompareWithNullFromModules()
    {
    	// Setup
    	Module toModule1 = new Module();
    	toModule1.setId(1);
    	this.setModules(null, toModule1, null, new Module());
    	
        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);

        // Verify        
        Assert.assertEquals(0, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows with both to modules NULL
     */
    @Test
    public void testCompareWithNullToModules()
    {
        // Setup
    	Module fromModule1 = new Module();
    	fromModule1.setId(1);
    	this.setModules(fromModule1, null, new Module(), null);

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(0, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows with Src Module (NULL, value) vs Tgt Module (value, NULL) 
     */
    @Test
    public void testCompareSrcAndTgt()
    {
        // Setup
    	Module toModule1 = new Module();
    	toModule1.setId(1);
    	Module fromModule2 = new Module();
    	fromModule2.setId(4);
    	this.setModules(null, toModule1, fromModule2, null);

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(-1, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows with Tgt Module (value, NULL) vs Src Module (NULL, value)  
     */
    @Test
    public void testCompareTgtAndSrc()
    {
        // Setup
    	Module fromModule2 = new Module();
    	fromModule2.setId(4);
    	Module toModule1 = new Module();
    	toModule1.setId(1);
    	this.setModules(fromModule2, null, null, toModule1);

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(1, comparisonResult);
    }
    
    /**
     * Test the compare method with PointToPointFlows with Src Module (NULL, value) vs Bridging Module (value, value) 
     */
    @Test
    public void testCompareSrcAndBridging()
    {
        // Setup
    	Module toModule1 = new Module();
    	toModule1.setId(1);
    	Module fromModule2 = new Module();
    	fromModule2.setId(4);
    	Module toModule2 = new Module();
    	toModule1.setId(7);
    	this.setModules(null, toModule1, fromModule2, toModule2);

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(-1, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows with Bridging Module (value, value) vs Src Module (NULL, value)  
     */
    @Test
    public void testCompareBridgingAndSrc()
    {
        // Setup
    	Module toModule1 = new Module();
    	toModule1.setId(1);
    	Module fromModule2 = new Module();
    	fromModule2.setId(4);
    	Module toModule2 = new Module();
    	toModule1.setId(7);
    	this.setModules(fromModule2, toModule2, null, toModule1);

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(1, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows with Tgt Module (value, NULL) vs Bridging Module (value, value)  
     */
    @Test
    public void testCompareTgtAndBridging()
    {
        // Setup
    	Module fromModule1 = new Module();
    	fromModule1.setId(2);
    	Module toModule1 = new Module();
    	toModule1.setId(5);
    	Module fromModule2 = new Module();
    	fromModule2.setId(8);
    	this.setModules(fromModule2, null, fromModule1, toModule1);

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(1, comparisonResult);
    }
    
    /**
     * Test the compare method with PointToPointFlows with a Bridging Module (value, value) vs Tgt Module (value, NULL).
     */
    @Test
    public void testCompareBridgingAndTgt()
    {
        // Setup
    	Module fromModule1 = new Module();
    	fromModule1.setId(2);
    	Module toModule1 = new Module();
    	toModule1.setId(5);
    	Module fromModule2 = new Module();
    	fromModule2.setId(8);
    	this.setModules(fromModule1, toModule1, fromModule2, null);

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(-1, comparisonResult);
    }
    
    /**
     * Test the compare method with PointToPointFlows with a Bridging Module (value, value) vs Bridging Module (value, value).
     * The Bridging modules are related, e.g. (1, 2) (2, 3) 
     */
    @Test
    public void testCompareBridgingAndBridging1()
    {
        // Setup
    	Module fromModule1 = new Module();
    	fromModule1.setId(2);
    	Module toModule1 = new Module();
    	toModule1.setId(5);
    	Module fromModule2 = new Module();
    	fromModule2.setId(5);
    	Module toModule2 = new Module();
    	toModule2.setId(10);
    	this.setModules(fromModule1, toModule1, fromModule2, toModule2);

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(-1, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows with a Bridging Module (value, value) vs Bridging Module (value, value).
     * The Bridging modules are related, e.g. (2, 3) (1, 2) 
     */
    @Test
    public void testCompareBridgingAndBridging2()
    {
        // Setup
    	Module fromModule1 = new Module();
    	fromModule1.setId(2);
    	Module toModule1 = new Module();
    	toModule1.setId(5);
    	Module fromModule2 = new Module();
    	fromModule2.setId(1);
    	Module toModule2 = new Module();
    	toModule2.setId(2);
    	this.setModules(fromModule1, toModule1, fromModule2, toModule2);

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(1, comparisonResult);
    }

    /**
     * Test the compare method with PointToPointFlows with a Bridging Module (value, value) vs Bridging Module (value, value).
     * The Bridging modules are unrelated, e.g. (2, 3) (1, 4) 
     */
    @Test
    public void testCompareBridgingAndBridgingUnrelated()
    {
        // Setup
    	Module fromModule1 = new Module();
    	fromModule1.setId(2);
    	Module toModule1 = new Module();
    	toModule1.setId(5);
    	Module fromModule2 = new Module();
    	fromModule2.setId(1);
    	Module toModule2 = new Module();
    	toModule2.setId(4);
    	this.setModules(fromModule1, toModule1, fromModule2, toModule2);

        // Expectations
        this.pointToPointFlowProfileIdExpectations();        
        
        // Test
        comparisonResult = comparator.compare(pointToPointFlow1, pointToPointFlow2);
        
        // Verify
        Assert.assertEquals(0, comparisonResult);
    }
    
    /**
     * Set the modules on their respective point to point flows
     * 
     * @param fromModule1 - fromModule to set on the first PointToPointFlow
     * @param fromModule2 - fromModule to set on the second PointToPointFlow
     * @param toModule1 - toModule to set on the first PointToPointFlow
     * @param toModule2 - toModule to set on the second PointToPointFlow
     */
    private void setModules(Module fromModule1, Module fromModule2, Module toModule1, Module toModule2)
    {
        this.fromModule1 = fromModule1;
        this.fromModule2 = fromModule2;
        this.toModule1 = toModule1;
        this.toModule2 = toModule2;
    	
        this.pointToPointFlow1.setFromModule(this.fromModule1);
        this.pointToPointFlow1.setToModule(this.fromModule2);
        this.pointToPointFlow2.setFromModule(this.toModule1);
        this.pointToPointFlow2.setToModule(this.toModule2);
    }

    /**
     * Helper class for PointToPointFlowProfileExpectations
     */
    private void pointToPointFlowProfileIdExpectations()
    {
	    context.checking(new Expectations()
	    {
	        {
	            one(pointToPointFlowProfile1).getId();
	            will(returnValue(new Long(1)));
	            one(pointToPointFlowProfile2).getId();
	            will(returnValue(new Long(1)));
	        }
	    });
    }
    
}
