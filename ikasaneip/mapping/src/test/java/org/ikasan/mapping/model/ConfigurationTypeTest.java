/*
 * $Id: ConfigurationTypeTest.java 31896 2013-08-02 15:41:00Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/test/java/com/mizuho/cmi2/mappingConfiguration/model/ConfigurationTypeTest.java $
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

import org.ikasan.mapping.model.ConfigurationType;
import org.junit.Ignore;
import org.junit.Test;



/**
 * @author CMI2 Development Team
 *
 */
@Ignore
public class ConfigurationTypeTest
{

    /**
     * Equals contract: x.equals(x)
     */
    @Test public void equals_is_reflexive()
    {
        ConfigurationType configurationType = new ConfigurationType();
        Assert.assertTrue(configurationType.equals(configurationType));
        Assert.assertTrue(configurationType.hashCode() == configurationType.hashCode());
    }

    /**
     * Equals contract: x.equals(y) iff y.equals(x)
     */
    @Test public void equals_is_symmetric()
    {
        ConfigurationType configurationTypeA = new ConfigurationType();
        configurationTypeA.setName("name");
        ConfigurationType configurationTypeB = new ConfigurationType();
        configurationTypeB.setName("name");

        Assert.assertTrue(configurationTypeA.equals(configurationTypeB));
        Assert.assertTrue(configurationTypeA.hashCode() == configurationTypeB.hashCode());
        Assert.assertTrue(configurationTypeB.equals(configurationTypeA));
        Assert.assertTrue(configurationTypeB.hashCode() == configurationTypeA.hashCode());

        configurationTypeB = new ConfigurationType();
        configurationTypeB.setName("other_name");


        Assert.assertFalse(configurationTypeA.equals(configurationTypeB));
        Assert.assertFalse(configurationTypeA.hashCode() == configurationTypeB.hashCode());
        Assert.assertFalse(configurationTypeB.equals(configurationTypeA));
        Assert.assertFalse(configurationTypeB.hashCode() == configurationTypeA.hashCode());
    }

    /**
     * Equals contract: x.equals(y) && y.equals(x), then x.equals(z)
     */
    @Test public void equals_is_transitive()
    {
        ConfigurationType configurationTypeA = new ConfigurationType();
        configurationTypeA.setName("name");
        ConfigurationType configurationTypeB = new ConfigurationType();
        configurationTypeB.setName("name");
        ConfigurationType configurationTypeC = new ConfigurationType();
        configurationTypeC.setName("name");

        Assert.assertTrue(configurationTypeA.equals(configurationTypeB));
        Assert.assertTrue(configurationTypeA.hashCode() == configurationTypeB.hashCode());
        Assert.assertTrue(configurationTypeB.equals(configurationTypeC));
        Assert.assertTrue(configurationTypeB.hashCode() == configurationTypeC.hashCode());
        Assert.assertTrue(configurationTypeA.equals(configurationTypeC));
        Assert.assertTrue(configurationTypeA.hashCode() == configurationTypeC.hashCode());

        configurationTypeA = new ConfigurationType();
        configurationTypeA.setName("other_name");

        Assert.assertFalse(configurationTypeA.equals(configurationTypeB));
        Assert.assertFalse(configurationTypeA.hashCode() == configurationTypeB.hashCode());
        Assert.assertTrue(configurationTypeB.equals(configurationTypeC));
        Assert.assertTrue(configurationTypeB.hashCode() == configurationTypeC.hashCode());
        Assert.assertFalse(configurationTypeA.equals(configurationTypeC));
        Assert.assertFalse(configurationTypeA.hashCode() == configurationTypeC.hashCode());
    }
}
