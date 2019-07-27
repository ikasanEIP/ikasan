package org.ikasan.dashboard.ui.visualisation.dao;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleMetaDataDaoImpl implements ModuleMetaDataDao
{
    private static final String BLBGTOMS_MHITRADE = "/data/graph/blbgToms-mhiTrade.json";
    private static final String ESPEED_TRADE = "/data/graph/espeed-trade.json";
    private static final String ION_JGB_TRADE = "/data/graph/ion-jgbTrade.json";
    private static final String R2R_MHEU_ELECTRONIC = "/data/graph/r2r-mheu2Mhi-electronicTrade.json";
    private static final String TRADEWEB_TRADE = "/data/graph/tradeweb-trade-sa.json";
    private static final String TT_TRADE = "/data/graph/tt-trade.json";

    private Map<String, String> moduleMetadata;

    public ModuleMetaDataDaoImpl()
    {
        this.moduleMetadata = new HashMap<>();

        init();
    }

    private void init()
    {
        try
        {
            this.moduleMetadata.put("blbgToms-mhiTrade", IOUtils.toString(loadDataFileStream(BLBGTOMS_MHITRADE), "UTF-8"));
            this.moduleMetadata.put("espeed-trade", IOUtils.toString(loadDataFileStream(ESPEED_TRADE), "UTF-8"));
            this.moduleMetadata.put("ion-jgbTrade", IOUtils.toString(loadDataFileStream(ION_JGB_TRADE), "UTF-8"));
            this.moduleMetadata.put("r2r-mheu2Mhi-electronicTrade", IOUtils.toString(loadDataFileStream(R2R_MHEU_ELECTRONIC), "UTF-8"));
            this.moduleMetadata.put("tradeweb-trade", IOUtils.toString(loadDataFileStream(TRADEWEB_TRADE), "UTF-8"));
            this.moduleMetadata.put("tt-trade", IOUtils.toString(loadDataFileStream(TT_TRADE), "UTF-8"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public List<String> getAllModuleName()
    {
        return new ArrayList<>(this.moduleMetadata.keySet());
    }

    @Override
    public String getModuleMetaData(String moduleName)
    {
        return this.moduleMetadata.get(moduleName);
    }

    protected InputStream loadDataFileStream(String fileName) throws IOException
    {
        return getClass().getResourceAsStream(fileName);
    }
}
