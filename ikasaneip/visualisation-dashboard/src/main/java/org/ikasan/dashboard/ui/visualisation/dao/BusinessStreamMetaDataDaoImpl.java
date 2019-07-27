package org.ikasan.dashboard.ui.visualisation.dao;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessStreamMetaDataDaoImpl implements BusinessStreamMetaDataDao
{
    private static final String SIMPLE_BOND = "/data/graph/bondFlowsGraph.json";
    private static final String COMPLEX_BOND = "/data/graph/bondFlowsGraphElaborate.json";
    private static final String REFERENCE_DATA = "/data/graph/referenceDataGraph.json";

    private Map<String, String> busnessStreamMetadata;

    public BusinessStreamMetaDataDaoImpl()
    {
        this.busnessStreamMetadata = new HashMap<>();

        try
        {
            this.busnessStreamMetadata.put("Simple Bond Business Stream", IOUtils.toString(loadDataFileStream(SIMPLE_BOND), "UTF-8"));
            this.busnessStreamMetadata.put("Complex Bond Business Stream", IOUtils.toString(loadDataFileStream(COMPLEX_BOND), "UTF-8"));
            this.busnessStreamMetadata.put("Reference Data Business Stream", IOUtils.toString(loadDataFileStream(REFERENCE_DATA), "UTF-8"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getAllBusinessStreamNames()
    {
        return new ArrayList<>(this.busnessStreamMetadata.keySet());
    }

    @Override
    public String getBusinessStreamMetaData(String businessStreamName)
    {
        return this.busnessStreamMetadata.get(businessStreamName);
    }

    protected InputStream loadDataFileStream(String fileName) throws IOException
    {
        return getClass().getResourceAsStream(fileName);
    }
}
