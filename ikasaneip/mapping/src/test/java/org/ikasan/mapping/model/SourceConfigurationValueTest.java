/*
 * $Id: SourceConfigurationValueTest.java 31896 2013-08-02 15:41:00Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/test/java/com/mizuho/cmi2/mappingConfiguration/model/SourceConfigurationValueTest.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.model;

import junit.framework.Assert;

import org.ikasan.mapping.model.SourceConfigurationValue;
import org.junit.Ignore;
import org.junit.Test;



/**
 * @author CMI2 Development Team
 *
 */
@Ignore
public class SourceConfigurationValueTest
{
    /**
     * Equals contract: x.equals(x)
     */
    @Test public void equals_is_reflexive()
    {
        SourceConfigurationValue sourceConfigurationValue = new SourceConfigurationValue();
        Assert.assertTrue(sourceConfigurationValue.equals(sourceConfigurationValue));
        Assert.assertTrue(sourceConfigurationValue.hashCode() == sourceConfigurationValue.hashCode());
    }

    /**
     * Equals contract: x.equals(y) iff y.equals(x)
     */
    @Test public void equals_is_symmetric()
    {
//        SourceConfigurationValue sourceConfigurationValue = new SourceConfigurationValue();
//        sourceConfigurationValue.setConfigurationContextId(new Long(1));
//        sourceConfigurationValue.setSourceSystemValue("sourceSystemValue");
//        sourceConfigurationValue.setTargetConfigurationValueId(new Long(1));
//        SourceConfigurationValue sourceConfigurationValueB = new SourceConfigurationValue();
//        sourceConfigurationValueB.setConfigurationContextId(new Long(1));
//        sourceConfigurationValueB.setSourceSystemValue("sourceSystemValue");
//        sourceConfigurationValueB.setTargetConfigurationValueId(new Long(1));
//        
//        Assert.assertTrue(sourceConfigurationValue.equals(sourceConfigurationValueB));
//        Assert.assertTrue(sourceConfigurationValue.hashCode() == sourceConfigurationValueB.hashCode());
//        Assert.assertTrue(sourceConfigurationValueB.equals(sourceConfigurationValue));
//        Assert.assertTrue(sourceConfigurationValueB.hashCode() == sourceConfigurationValue.hashCode());
//
//        sourceConfigurationValueB = new SourceConfigurationValue();
//        sourceConfigurationValueB.setConfigurationContextId(new Long(2));
//        sourceConfigurationValueB.setSourceSystemValue("sourceSystemValueB");
//        sourceConfigurationValueB.setTargetConfigurationValueId(new Long(2));
//
//        Assert.assertFalse(sourceConfigurationValue.equals(sourceConfigurationValueB));
//        Assert.assertFalse(sourceConfigurationValue.hashCode() == sourceConfigurationValueB.hashCode());
//        Assert.assertFalse(sourceConfigurationValueB.equals(sourceConfigurationValue));
//        Assert.assertFalse(sourceConfigurationValueB.hashCode() == sourceConfigurationValue.hashCode());
    }

    /**
     * Equals contract: multiple invocations of x.equals(y) will always be true, unless
     * properties used to determine equality change. 
     */
    @Test public void equals_is_consistent()
    {
//        SourceConfigurationValue sourceConfigurationValue = new SourceConfigurationValue();
//        SourceConfigurationValue sourceConfigurationValueB = new SourceConfigurationValue();
//
//        Assert.assertTrue(sourceConfigurationValue.equals(sourceConfigurationValueB));
//        Assert.assertTrue(sourceConfigurationValue.hashCode() == sourceConfigurationValueB.hashCode());
//        Assert.assertTrue(sourceConfigurationValueB.equals(sourceConfigurationValue));
//        Assert.assertTrue(sourceConfigurationValueB.hashCode() == sourceConfigurationValue.hashCode());
//
//        sourceConfigurationValue.setConfigurationContextId(new Long(1));
//        sourceConfigurationValue.setSourceSystemValue("sourceSystemValue");
//        sourceConfigurationValue.setTargetConfigurationValueId(new Long(1));
//
//        sourceConfigurationValueB.setConfigurationContextId(new Long(2));
//        sourceConfigurationValueB.setSourceSystemValue("sourceSystemValue");
//        sourceConfigurationValueB.setTargetConfigurationValueId(new Long(1));
//
//        Assert.assertFalse(sourceConfigurationValue.equals(sourceConfigurationValueB));
//        Assert.assertFalse(sourceConfigurationValue.hashCode() == sourceConfigurationValueB.hashCode());
//        Assert.assertFalse(sourceConfigurationValueB.equals(sourceConfigurationValue));
//        Assert.assertFalse(sourceConfigurationValueB.hashCode() == sourceConfigurationValue.hashCode());

        // Cannot test changing equality property since their setters are
        // purposefully coded to be private!
    }

    /**
     * Equals contract: a Book instance is never equal to null
     */
    @Test public void nulls_are_not_equal_to_any_history_instance()
    {
        SourceConfigurationValue sourceConfigurationValue = new SourceConfigurationValue();
        Assert.assertFalse(sourceConfigurationValue.equals(null));
    }

    /**
     * Equals contract: comparing a Book against an object of a different
     * type will always return false
     */
    @Test public void a_history_is_NOT_equal_to_an_object_of_different_type()
    {
        SourceConfigurationValue sourceConfigurationValue = new SourceConfigurationValue();
        Assert.assertFalse(sourceConfigurationValue.equals("Not a SourceConfigurationValue!"));
    }
}
