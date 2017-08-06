package org.ikasan.mapping;

import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.util.MappingConfigurationValidator;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 17/05/2017.
 */
public class MappingConfigurationValidatorTest
{
    @Test
    public void test_validation_fails_not_grouped_mapping()
    {
        MappingConfiguration mc = new MappingConfiguration();

        Set<SourceConfigurationValue> values = new HashSet<SourceConfigurationValue>();

        SourceConfigurationValue value = new SourceConfigurationValue();
        value.setSourceSystemValue("name");

        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("name");

        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name");

        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name 1");

        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name 2");

        values.add(value);

        mc.setSourceConfigurationValues(values);

        MappingConfigurationValidator validator = new MappingConfigurationValidator();

        boolean result = validator.validate(mc);

        Assert.assertEquals("Validation should fail", false, result);
        Assert.assertNotEquals("Error message equals", "No errors", validator.getErrorMessage());
    }

    @Test
    public void test_validation_passes_not_grouped_mapping()
    {
        MappingConfiguration mc = new MappingConfiguration();

        Set<SourceConfigurationValue> values = new HashSet<SourceConfigurationValue>();

        SourceConfigurationValue value = new SourceConfigurationValue();
        value.setSourceSystemValue("name");

        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("name 1");

        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name");

        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name 1");

        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name 2");

        values.add(value);

        mc.setSourceConfigurationValues(values);

        MappingConfigurationValidator validator = new MappingConfigurationValidator();

        boolean result = validator.validate(mc);

        Assert.assertEquals("Validation should pass", true, result);
        Assert.assertEquals("Error message equals", "No errors", validator.getErrorMessage());
    }

    @Test
    public void test_validation_fails__grouped_mapping()
    {
        MappingConfiguration mc = new MappingConfiguration();
        mc.setNumberOfParams(2);

        Set<SourceConfigurationValue> values = new HashSet<SourceConfigurationValue>();

        SourceConfigurationValue value = new SourceConfigurationValue();
        value.setSourceSystemValue("name");
        value.setSourceConfigGroupId(1l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name");
        value.setSourceConfigGroupId(1l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("name");
        value.setSourceConfigGroupId(2l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name");
        value.setSourceConfigGroupId(2l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("name 1");
        value.setSourceConfigGroupId(3l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name 2");
        value.setSourceConfigGroupId(3l);
        values.add(value);

        mc.setSourceConfigurationValues(values);

        MappingConfigurationValidator validator = new MappingConfigurationValidator();

        boolean result = validator.validate(mc);

        Assert.assertEquals("Validation should fail", false, result);
        Assert.assertNotEquals("Error message equals", "No errors", validator.getErrorMessage());
    }

    @Test
    public void test_validation_passes_grouped_mapping()
    {
        MappingConfiguration mc = new MappingConfiguration();
        mc.setNumberOfParams(2);

        Set<SourceConfigurationValue> values = new HashSet<SourceConfigurationValue>();

        SourceConfigurationValue value = new SourceConfigurationValue();
        value.setSourceSystemValue("name 3");
        value.setSourceConfigGroupId(1l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name 3");
        value.setSourceConfigGroupId(1l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("name");
        value.setSourceConfigGroupId(2l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name");
        value.setSourceConfigGroupId(2l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("name 1");
        value.setSourceConfigGroupId(3l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name 2");
        value.setSourceConfigGroupId(3l);
        values.add(value);

        mc.setSourceConfigurationValues(values);

        MappingConfigurationValidator validator = new MappingConfigurationValidator();

        boolean result = validator.validate(mc);

        Assert.assertEquals("Validation should pass", true, result);
        Assert.assertEquals("Error message equals", "No errors", validator.getErrorMessage());
    }

    @Test
    public void test_validation_fails_grouped_mapping_with_named_Parameters()
    {
        MappingConfiguration mc = new MappingConfiguration();
        mc.setNumberOfParams(2);

        Set<SourceConfigurationValue> values = new HashSet<SourceConfigurationValue>();

        SourceConfigurationValue value = new SourceConfigurationValue();
        value.setSourceSystemValue("name");
        value.setSourceConfigGroupId(1l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name");
        value.setSourceConfigGroupId(1l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("name");
        value.setSourceConfigGroupId(2l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name");
        value.setSourceConfigGroupId(2l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("name 1");
        value.setSourceConfigGroupId(3l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name 2");
        value.setSourceConfigGroupId(3l);
        values.add(value);

        mc.setSourceConfigurationValues(values);

        MappingConfigurationValidator validator = new MappingConfigurationValidator();

        boolean result = validator.validate(mc);

        Assert.assertEquals("Validation should fail", false, result);
        Assert.assertNotEquals("Error message equals", "No errors", validator.getErrorMessage());
    }

    @Test
    public void test_validation_passes_grouped_mapping_with_named_parameters()
    {
        MappingConfiguration mc = new MappingConfiguration();
        mc.setNumberOfParams(2);

        Set<SourceConfigurationValue> values = new HashSet<SourceConfigurationValue>();

        SourceConfigurationValue value = new SourceConfigurationValue();
        value.setSourceSystemValue("name 3");
        value.setName("name1");
        value.setSourceConfigGroupId(1l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name 3");
        value.setName("name2");
        value.setSourceConfigGroupId(1l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name 3");
        value.setName("name1");
        value.setSourceConfigGroupId(2l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("name 3");
        value.setName("name2");
        value.setSourceConfigGroupId(2l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("name 1");
        value.setName("name1");
        value.setSourceConfigGroupId(3l);
        values.add(value);

        value = new SourceConfigurationValue();
        value.setSourceSystemValue("another name 2");
        value.setName("name2");
        value.setSourceConfigGroupId(3l);
        values.add(value);

        mc.setSourceConfigurationValues(values);

        MappingConfigurationValidator validator = new MappingConfigurationValidator();

        boolean result = validator.validate(mc);

        Assert.assertEquals("Validation should pass", true, result);
        Assert.assertEquals("Error message equals", "No errors", validator.getErrorMessage());
    }
}
