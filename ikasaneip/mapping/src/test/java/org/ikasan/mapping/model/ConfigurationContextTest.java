/*
 * $Id: ConfigurationContextTest.java 40152 2014-10-17 15:57:49Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/test/java/com/mizuho/cmi2/mappingConfiguration/model/ConfigurationContextTest.java $
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

import org.ikasan.mapping.model.MappingConfiguration;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author CMI2 Development Team
 *
 */
@Ignore
public class ConfigurationContextTest
{

    /**
     * Equals contract: x.equals(x)
     */
    @Test public void equals_is_reflexive()
    {
        MappingConfiguration configurationContext = new MappingConfiguration();
        Assert.assertTrue(configurationContext.equals(configurationContext));
        Assert.assertTrue(configurationContext.hashCode() == configurationContext.hashCode());
    }

    /**
     * Equals contract: x.equals(y) iff y.equals(x)
     */
    @Test public void equals_is_symmetric()
    {
//        MappingConfiguration configurationContextA = new MappingConfiguration();
//        configurationContextA.setConfigurationTypeId(new Long(1));
//        configurationContextA.setNumberOfParams(new Long(1));
//        configurationContextA.setSourceContextId(new Long(1));
//        configurationContextA.setTargetContextId(new Long(2));
//        MappingConfiguration configurationContextB = new MappingConfiguration();
//        configurationContextB.setConfigurationTypeId(new Long(1));
//        configurationContextB.setNumberOfParams(new Long(1));
//        configurationContextB.setSourceContextId(new Long(1));
//        configurationContextB.setTargetContextId(new Long(2));
//
//        Assert.assertTrue(configurationContextA.equals(configurationContextB));
//        Assert.assertTrue(configurationContextA.hashCode() == configurationContextB.hashCode());
//        Assert.assertTrue(configurationContextB.equals(configurationContextA));
//        Assert.assertTrue(configurationContextB.hashCode() == configurationContextA.hashCode());
//
//        configurationContextB = new MappingConfiguration();
//        configurationContextB.setConfigurationTypeId(new Long(2));
//        configurationContextB.setNumberOfParams(new Long(2));
//        configurationContextB.setSourceContextId(new Long(3));
//        configurationContextB.setTargetContextId(new Long(4));
//
//
//        Assert.assertFalse(configurationContextA.equals(configurationContextB));
//        Assert.assertFalse(configurationContextA.hashCode() == configurationContextB.hashCode());
//        Assert.assertFalse(configurationContextB.equals(configurationContextA));
//        Assert.assertFalse(configurationContextB.hashCode() == configurationContextA.hashCode());
    }

//    /**
//     * Equals contract: x.equals(y) && y.equals(x), then x.equals(z)
//     */
//    @Test public void equals_is_transitive()
//    {
//        MappingConfiguration configurationContextA = new MappingConfiguration();
//        configurationContextA.setConfigurationTypeId(new Long(1));
//        configurationContextA.setNumberOfParams(new Long(1));
//        configurationContextA.setSourceSystem("sourceSystem");
//        configurationContextA.setTargetSystem("targetSystem");
//        MappingConfiguration configurationContextB = new MappingConfiguration();
//        configurationContextB.setConfigurationTypeId(new Long(1));
//        configurationContextB.setNumberOfParams(new Long(1));
//        configurationContextB.setSourceSystem("sourceSystem");
//        configurationContextB.setTargetSystem("targetSystem");
//        MappingConfiguration configurationContextC = new MappingConfiguration();
//        configurationContextC.setConfigurationTypeId(new Long(1));
//        configurationContextC.setNumberOfParams(new Long(1));
//        configurationContextC.setSourceSystem("sourceSystem");
//        configurationContextC.setTargetSystem("targetSystem");
//
//        Assert.assertTrue(configurationContextA.equals(configurationContextB));
//        Assert.assertTrue(configurationContextA.hashCode() == configurationContextB.hashCode());
//        Assert.assertTrue(configurationContextB.equals(configurationContextC));
//        Assert.assertTrue(configurationContextB.hashCode() == configurationContextC.hashCode());
//        Assert.assertTrue(configurationContextA.equals(configurationContextC));
//        Assert.assertTrue(configurationContextA.hashCode() == configurationContextC.hashCode());
//
//        configurationContextA = new MappingConfiguration();
//        configurationContextA.setConfigurationTypeId(new Long(2));
//        configurationContextA.setNumberOfParams(new Long(2));
//        configurationContextA.setSourceSystem("sourceSystemB");
//        configurationContextA.setTargetSystem("targetSystemB");
//
//        Assert.assertFalse(configurationContextA.equals(configurationContextB));
//        Assert.assertFalse(configurationContextA.hashCode() == configurationContextB.hashCode());
//        Assert.assertTrue(configurationContextB.equals(configurationContextC));
//        Assert.assertTrue(configurationContextB.hashCode() == configurationContextC.hashCode());
//        Assert.assertFalse(configurationContextA.equals(configurationContextC));
//        Assert.assertFalse(configurationContextA.hashCode() == configurationContextC.hashCode());
//    }
}
