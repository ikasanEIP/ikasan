package org.ikasan.dashboard.notification.model;

import org.ikasan.spec.metadata.BusinessStreamMetaData;

import java.util.List;

public class BusinessStreamExclusions {
    private BusinessStreamMetaData businessStreamMetaData;
    private List<BusinessStreamExclusion> businessStreamExclusions;

    public BusinessStreamExclusions(BusinessStreamMetaData businessStreamMetaData, List<BusinessStreamExclusion> businessStreamExclusions) {
        this.businessStreamMetaData = businessStreamMetaData;
        this.businessStreamExclusions = businessStreamExclusions;
    }

    public BusinessStreamMetaData getBusinessStreamMetaData() {
        return businessStreamMetaData;
    }

    public List<BusinessStreamExclusion> getBusinessStreamExclusions() {
        return businessStreamExclusions;
    }
}
