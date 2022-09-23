package org.ikasan.module;

import java.util.Map;

/**
 * Used to set the flow startup type and wiretap triggers on module at startup using configuration properties
 */
public class StartupModuleConfiguration {

    /**
     * Any flow that has not had its startup type saved previously will have its startup type set to this. This
     * will not overwrite any previously or manually saved startup type
     */
    private String defaultFlowStartupType;

    /**
     * Maps the flow name to its startup type - this will be used in preference to the defaultFlowStartupType
     * if the flow has not had its startup type saved previously it will be set to this mapped value. This
     * will not overwrite any previously or manually saved startup type.
     */
    private Map<String,String>flowNameToStartupTypeMap;

    /**
     * Use with CAUTION will delete all saved startup types from the database. Useful if you want to guarentee the
     * flows startup types are set as defined by the defaultFlowStartupType and flowNameToStartupType map above
     * without any manually saved startup types interfering with this
     *
     */
    private Boolean deleteAllPreviouslySavedStartupTypes;


    /**
     * Saves wiretap triggers if these have not been set before
     */
    private Map<String,WiretapTriggerConfiguration>wiretaps;

    /**
     * Use with CAUTION Deletes all previously saved wiretaps
     */
    private Boolean deleteAllPreviouslySavedWiretaps;


    public String getDefaultFlowStartupType() {
        return defaultFlowStartupType;
    }

    public void setDefaultFlowStartupType(String defaultFlowStartupType) {
        this.defaultFlowStartupType = defaultFlowStartupType;
    }

    public Map<String, String> getFlowNameToStartupTypeMap() {
        return flowNameToStartupTypeMap;
    }

    public void setFlowNameToStartupTypeMap(Map<String, String> flowNameToStartupTypeMap) {
        this.flowNameToStartupTypeMap = flowNameToStartupTypeMap;
    }

    public Boolean areStartupTypePropertiesPopulated(){
        return (defaultFlowStartupType != null && defaultFlowStartupType != "") ||
            (flowNameToStartupTypeMap != null && flowNameToStartupTypeMap.size() > 0);
    }

    public Boolean getDeleteAllPreviouslySavedStartupTypes() {
        return deleteAllPreviouslySavedStartupTypes;
    }

    public void setDeleteAllPreviouslySavedStartupTypes(Boolean deleteAllPreviouslySavedStartupTypes) {
        this.deleteAllPreviouslySavedStartupTypes = deleteAllPreviouslySavedStartupTypes;
    }

    @Override
    public String toString() {
        return "StartupModuleConfiguration{" +
            "defaultFlowStartupType='" + defaultFlowStartupType + '\'' +
            ", flowNameToStartupTypeMap=" + flowNameToStartupTypeMap +
            ", deleteAllPreviouslySavedStartupTypes=" + deleteAllPreviouslySavedStartupTypes +
            '}';
    }

    public Map<String, WiretapTriggerConfiguration> getWiretaps() {
        return wiretaps;
    }

    public void setWiretaps(Map<String, WiretapTriggerConfiguration> wiretaps) {
        this.wiretaps = wiretaps;
    }

    public Boolean getDeleteAllPreviouslySavedWiretaps() {
        return deleteAllPreviouslySavedWiretaps;
    }

    public void setDeleteAllPreviouslySavedWiretaps(Boolean deleteAllPreviouslySavedWiretaps) {
        this.deleteAllPreviouslySavedWiretaps = deleteAllPreviouslySavedWiretaps;
    }
}
