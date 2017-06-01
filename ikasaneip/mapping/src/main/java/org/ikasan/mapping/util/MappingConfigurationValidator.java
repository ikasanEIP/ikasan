package org.ikasan.mapping.util;

import org.apache.log4j.Logger;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.SourceConfigurationValue;

import java.util.*;

/**
 * Created by Ikasan Development Team on 17/05/2017.
 */
public class MappingConfigurationValidator
{
    private Logger logger = Logger.getLogger(MappingConfigurationValidator.class);

    private HashMap<String, Integer> validationMap
            = null;
    private HashMap<String, List<SourceConfigurationValue>> referenceMap
            = null;

    private StringBuffer errorString = null;

    public boolean validate(MappingConfiguration mappingConfiguration)
    {
        validationMap = new HashMap<String, Integer>();
        referenceMap = new HashMap<String, List<SourceConfigurationValue>>();

        logger.info("mappingConfiguration.getNumberOfParams() = " + mappingConfiguration.getNumberOfParams());

        if(mappingConfiguration.getNumberOfParams() == 1)
        {
            this.initialiseOneSourceParameterMapping(mappingConfiguration);
        }
        else
        {
            this.initialiseMultipleSourceParameterMapping(mappingConfiguration);
        }

        return this.performValidation();
    }

    public String getErrorMessage()
    {
        if(errorString == null || errorString.length() == 0)
        {
            return "No errors";
        }
        else
        {
            return errorString.toString();
        }
    }

    private boolean performValidation()
    {
        boolean isValid = true;

        this.errorString = new StringBuffer();

        for(String key: this.validationMap.keySet())
        {
            Integer count = this.validationMap.get(key);

            if(count > 1)
            {
                isValid = false;

                List<SourceConfigurationValue> values = this.referenceMap.get(key);

                this.errorString.append("[");

                for(SourceConfigurationValue value: values)
                {
                    this.errorString.append(value.getSourceSystemValue());

                    if(values.lastIndexOf(value) + 1 != values.size())
                    {
                        this.errorString.append(", ");
                    }
                }

                this.errorString.append("]\r\n\r\n");
            }
        }

        return isValid;
    }

    private void initialiseOneSourceParameterMapping(MappingConfiguration mappingConfiguration)
    {
        for(SourceConfigurationValue value: mappingConfiguration.getSourceConfigurationValues())
        {
            Integer count = validationMap.get(value.getSourceSystemValue());

            if (count == null)
            {
                count = 1;
                validationMap.put(value.getSourceSystemValue(), count);
            }
            else
            {
                validationMap.put(value.getSourceSystemValue(), ++count);
            }

            ArrayList<SourceConfigurationValue> valueList = new ArrayList<SourceConfigurationValue>();
            valueList.add(value);

            referenceMap.put(value.getSourceSystemValue(), valueList);
        }


    }

    private void initialiseMultipleSourceParameterMapping(MappingConfiguration mappingConfiguration)
    {
        HashMap<Long, List<SourceConfigurationValue>> groupedSourceValues
                = this.groupSourceConfigurationValues(mappingConfiguration.getSourceConfigurationValues());

        for(List<SourceConfigurationValue> values: groupedSourceValues.values())
        {
            Collections.sort(values);
            StringBuffer valueString = new StringBuffer();

            for(SourceConfigurationValue value: values)
            {
                valueString.append(value.getSourceSystemValue());

                if(value.getName() != null)
                {
                    valueString.append(value.getName());
                }
            }

            Integer count = validationMap.get(valueString.toString());

            if (count == null)
            {
                count = 1;
                validationMap.put(valueString.toString(), count);
            }
            else
            {
                validationMap.put(valueString.toString(), ++count);
            }

            referenceMap.put(valueString.toString(), values);
        }
    }

    private HashMap<Long, List<SourceConfigurationValue>> groupSourceConfigurationValues(Set<SourceConfigurationValue> sourceConfigurationValues)
    {
        HashMap<Long, List<SourceConfigurationValue>> groupedSourceValues = new HashMap<Long, List<SourceConfigurationValue>>();

        for (SourceConfigurationValue value: sourceConfigurationValues)
        {
            List<SourceConfigurationValue> groupedValues = groupedSourceValues.get(value.getSourceConfigGroupId());

            if(groupedValues == null)
            {
                groupedValues = new ArrayList<SourceConfigurationValue>();

                groupedValues.add(value);
                groupedSourceValues.put(value.getSourceConfigGroupId(), groupedValues);
            }
            else
            {
                groupedValues.add(value);
                groupedSourceValues.put(value.getSourceConfigGroupId(), groupedValues);
            }
        }

        return groupedSourceValues;
    }
}
