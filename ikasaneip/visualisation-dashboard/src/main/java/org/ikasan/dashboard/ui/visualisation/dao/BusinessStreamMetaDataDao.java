package org.ikasan.dashboard.ui.visualisation.dao;

import java.util.List;

public interface BusinessStreamMetaDataDao
{
    public List<String> getAllBusinessStreamNames();

    public String getBusinessStreamMetaData(String businessStreamName);
}
