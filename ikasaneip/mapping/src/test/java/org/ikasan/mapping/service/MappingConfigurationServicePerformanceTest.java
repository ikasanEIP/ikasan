/*
O * $Id: MappingConfigurationServicePerformanceTest.java 40152 2014-10-17 15:57:49Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/test/java/com/mizuho/cmi2/mappingConfiguration/service/MappingConfigurationServicePerformanceTest.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2012 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.ikasan.mapping.dao.HibernateMappingConfigurationDao;
import org.ikasan.mapping.keyQueryProcessor.KeyLocationQueryProcessorFactory;
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.model.TargetConfigurationValue;
import org.ikasan.mapping.service.MappingConfigurationService;
import org.ikasan.mapping.service.MappingConfigurationServiceException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * Unit test for {@link HibernateStateModelDao}
 * 
 * @author CMI2 Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/mapping-conf.xml",
        "/hsqldb-config.xml",
        "/substitute-components.xml",
        "/mock-components.xml"
})
@Ignore
public class MappingConfigurationServicePerformanceTest
{
    /** Object being tested */
    @Resource private MappingConfigurationService xaMappingConfigurationService;
    @Resource private HibernateMappingConfigurationDao xaMappingConfigurationDao;
    @Resource private KeyLocationQueryProcessorFactory keyLocationQueryProcessorFactory; 

    public static final String CLEAN_JGB_RAW_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><PTF><SOH><VERS>1.00</VERS>" +
            "<MTYP>POSTTRD</MTYP><DATE>20130701</DATE><TIME>08:15:38</TIME><DLR>MZHO</DLR><USER>mzhojgbeu</USER>" +
            "<PGRP>JGB</PGRP><TRDDT>20130701</TRDDT></SOH><SPTM attr=\"1\"><SOH><VERS>1.00</VERS><MTYP>TRDCONF</MTYP>" +
                    "<DATE>20130701</DATE><TIME>08:15:37</TIME><TSTMSG>YES</TSTMSG><DLR>MZHO</DLR><USER>mzhojgbeu</USER>" +
                    "<PGRP>JGB</PGRP><TRDTYP>OUTRIGHT</TRDTYP><TSRC>TW</TSRC><TRDDT>20130701</TRDDT><TNUM>1</TNUM></SOH><SLSPRSN>azehra</SLSPRSN>" +
                    "<CUST>mstewartc</CUST><CUSTNAME>Michael Stewart</CUSTNAME><UCNTRY>UK</UCNTRY><COMPANY>Mizuho Trust Bk1</COMPANY>" +
                    "<COACR>CBKUAE</COACR><ODDMARK>NO</ODDMARK><LOCC>LDN</LOCC><LOCD>LDN</LOCD><CLRCD>BOJ-NET</CLRCD><STYPE>REGJNOTE</STYPE>" +
                    "<WI>NO</WI><TOSHO>01440069</TOSHO><ISIN>JP1201441D44</ISIN><CPN>1.50</CPN><AUCDT>20130418</AUCDT><MATDT>20330320</MATDT>" +
                    "<ROPN>0</ROPN><ISMN>240</ISMN><OTR>2</OTR><DTDDT>20130320</DTDDT><ISSDT>20130422</ISSDT><FCDT>20130920</FCDT>" +
                    "<ANNCDT>20130411</ANNCDT><CNTRY>JP</CNTRY><CPNFQ>2</CPNFQ><DYCTBAS>ACT/365</DYCTBAS><RVAL>100.0</RVAL><DECPLCS>3</DECPLCS>" +
                    "<DECRND>0.0010</DECRND><QID>TBSI01JP1201441D44</QID><SDESC>J20-144 1.500 20/03/33</SDESC><QTYP>YIELD</QTYP>" +
                    "<PRICE>6.2560</PRICE><YIELD>100.0</YIELD><BYIELD>26.7780</BYIELD><YTYP>BOND</YTYP><QNTY>19000000</QNTY>" +
                    "<TRANS>BUY</TRANS><STLDT>20130705</STLDT><GTRDDT>20130701</GTRDDT><GTRDTM>08:15:24</GTRDTM><SECACCR>0.4397260</SECACCR>" +
                    "<TOTACCR>83547.0</TOTACCR><PRIN>1188640.0</PRIN><NET>1272187.0</NET><CURR>JPY</CURR><FXRATE>0.0</FXRATE><NREQ>1</NREQ>" +
                    "<MTKT>1</MTKT><CUSTPRC>NO</CUSTPRC><TRADER>mstewartd</TRADER><CNTCTTRDR>Michael Stewart</CNTCTTRDR><COMPQT>1.7370</COMPQT>" +
                    "<COMPSZ>0.0</COMPSZ><CMPB>1.7610</CMPB><CMPA>1.7370</CMPA><CMPM>1.7490</CMPM><COMPMV>0.0</COMPMV><VIEWERACK>NO</VIEWERACK>" +
                    "<EOM/></SPTM><EOM/></PTF>";

    public static final String CLEAN_JGB_RAW_XML_EMPTY_SALESPERSON = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><PTF><SOH><VERS>1.00</VERS>" +
            "<MTYP>POSTTRD</MTYP><DATE>20130701</DATE><TIME>08:15:38</TIME><DLR>MZHO</DLR><USER>mzhojgbeu</USER>" +
            "<PGRP>JGB</PGRP><TRDDT>20130701</TRDDT></SOH><SPTM attr=\"1\"><SOH><VERS>1.00</VERS><MTYP>TRDCONF</MTYP>" +
                    "<DATE>20130701</DATE><TIME>08:15:37</TIME><TSTMSG>YES</TSTMSG><DLR>MZHO</DLR><USER>mzhojgbeu</USER>" +
                    "<PGRP>JGB</PGRP><TRDTYP>OUTRIGHT</TRDTYP><TSRC>TW</TSRC><TRDDT>20130701</TRDDT><TNUM>1</TNUM></SOH><SLSPRSN></SLSPRSN>" +
                    "<CUST>mstewartc</CUST><CUSTNAME>Michael Stewart</CUSTNAME><UCNTRY>UK</UCNTRY><COMPANY>Mizuho Trust Bk1</COMPANY>" +
                    "<COACR>CBKUAE</COACR><ODDMARK>NO</ODDMARK><LOCC>LDN</LOCC><LOCD>LDN</LOCD><CLRCD>BOJ-NET</CLRCD><STYPE>REGJNOTE</STYPE>" +
                    "<WI>NO</WI><TOSHO>01440069</TOSHO><ISIN>JP1201441D44</ISIN><CPN>1.50</CPN><AUCDT>20130418</AUCDT><MATDT>20330320</MATDT>" +
                    "<ROPN>0</ROPN><ISMN>240</ISMN><OTR>2</OTR><DTDDT>20130320</DTDDT><ISSDT>20130422</ISSDT><FCDT>20130920</FCDT>" +
                    "<ANNCDT>20130411</ANNCDT><CNTRY>JP</CNTRY><CPNFQ>2</CPNFQ><DYCTBAS>ACT/365</DYCTBAS><RVAL>100.0</RVAL><DECPLCS>3</DECPLCS>" +
                    "<DECRND>0.0010</DECRND><QID>TBSI01JP1201441D44</QID><SDESC>J20-144 1.500 20/03/33</SDESC><QTYP>YIELD</QTYP>" +
                    "<PRICE>6.2560</PRICE><YIELD>100.0</YIELD><BYIELD>26.7780</BYIELD><YTYP>BOND</YTYP><QNTY>19000000</QNTY>" +
                    "<TRANS>BUY</TRANS><STLDT>20130705</STLDT><GTRDDT>20130701</GTRDDT><GTRDTM>08:15:24</GTRDTM><SECACCR>0.4397260</SECACCR>" +
                    "<TOTACCR>83547.0</TOTACCR><PRIN>1188640.0</PRIN><NET>1272187.0</NET><CURR>JPY</CURR><FXRATE>0.0</FXRATE><NREQ>1</NREQ>" +
                    "<MTKT>1</MTKT><CUSTPRC>NO</CUSTPRC><TRADER>mstewartd</TRADER><CNTCTTRDR>Michael Stewart</CNTCTTRDR><COMPQT>1.7370</COMPQT>" +
                    "<COMPSZ>0.0</COMPSZ><CMPB>1.7610</CMPB><CMPA>1.7370</CMPA><CMPM>1.7490</CMPM><COMPMV>0.0</COMPMV><VIEWERACK>NO</VIEWERACK>" +
                    "<EOM/></SPTM><EOM/></PTF>";

    public static final String CLEAN_JGB_RAW_XML_NO_SALESPERSON = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><PTF><SOH><VERS>1.00</VERS>" +
            "<MTYP>POSTTRD</MTYP><DATE>20130701</DATE><TIME>08:15:38</TIME><DLR>MZHO</DLR><USER>mzhojgbeu</USER>" +
            "<PGRP>JGB</PGRP><TRDDT>20130701</TRDDT></SOH><SPTM attr=\"1\"><SOH><VERS>1.00</VERS><MTYP>TRDCONF</MTYP>" +
                    "<DATE>20130701</DATE><TIME>08:15:37</TIME><TSTMSG>YES</TSTMSG><DLR>MZHO</DLR><USER>mzhojgbeu</USER>" +
                    "<PGRP>JGB</PGRP><TRDTYP>OUTRIGHT</TRDTYP><TSRC>TW</TSRC><TRDDT>20130701</TRDDT><TNUM>1</TNUM></SOH>" +
                    "<CUST>mstewartc</CUST><CUSTNAME>Michael Stewart</CUSTNAME><UCNTRY>UK</UCNTRY><COMPANY>Mizuho Trust Bk1</COMPANY>" +
                    "<COACR>CBKUAE</COACR><ODDMARK>NO</ODDMARK><LOCC>LDN</LOCC><LOCD>LDN</LOCD><CLRCD>BOJ-NET</CLRCD><STYPE>REGJNOTE</STYPE>" +
                    "<WI>NO</WI><TOSHO>01440069</TOSHO><ISIN>JP1201441D44</ISIN><CPN>1.50</CPN><AUCDT>20130418</AUCDT><MATDT>20330320</MATDT>" +
                    "<ROPN>0</ROPN><ISMN>240</ISMN><OTR>2</OTR><DTDDT>20130320</DTDDT><ISSDT>20130422</ISSDT><FCDT>20130920</FCDT>" +
                    "<ANNCDT>20130411</ANNCDT><CNTRY>JP</CNTRY><CPNFQ>2</CPNFQ><DYCTBAS>ACT/365</DYCTBAS><RVAL>100.0</RVAL><DECPLCS>3</DECPLCS>" +
                    "<DECRND>0.0010</DECRND><QID>TBSI01JP1201441D44</QID><SDESC>J20-144 1.500 20/03/33</SDESC><QTYP>YIELD</QTYP>" +
                    "<PRICE>6.2560</PRICE><YIELD>100.0</YIELD><BYIELD>26.7780</BYIELD><YTYP>BOND</YTYP><QNTY>19000000</QNTY>" +
                    "<TRANS>BUY</TRANS><STLDT>20130705</STLDT><GTRDDT>20130701</GTRDDT><GTRDTM>08:15:24</GTRDTM><SECACCR>0.4397260</SECACCR>" +
                    "<TOTACCR>83547.0</TOTACCR><PRIN>1188640.0</PRIN><NET>1272187.0</NET><CURR>JPY</CURR><FXRATE>0.0</FXRATE><NREQ>1</NREQ>" +
                    "<MTKT>1</MTKT><CUSTPRC>NO</CUSTPRC><TRADER>mstewartd</TRADER><CNTCTTRDR>Michael Stewart</CNTCTTRDR><COMPQT>1.7370</COMPQT>" +
                    "<COMPSZ>0.0</COMPSZ><CMPB>1.7610</CMPB><CMPA>1.7370</CMPA><CMPM>1.7490</CMPM><COMPMV>0.0</COMPMV><VIEWERACK>NO</VIEWERACK>" +
                    "<EOM/></SPTM><EOM/></PTF>";

    
    /**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     */
    @Before public void setup()
    {
//        for(int i=15001; i<20000; i++)
//        {
//            Long configurationServiceClientId = this.addConfigurationServiceClient("client" + i, 
//                "com.mizuho.cmi2.mappingConfiguration.keyQueryProcessor.impl.XPathKeyLocationQueryProcessor");
//            Long dealerToDealerId = this.addConfigurationType("Dealer and Product to Account" + i);
//    
//            Long contextId1 = this.addConfigurationContext("Tradeweb" + i, "Tradeweb" + i);
//            Long contextId2 = this.addConfigurationContext("Bloomberg" + i, "Bloomberg" + i);
//    
//            List<String> keyLocationQueries1 = new ArrayList<String>();
//            keyLocationQueries1.add("/PTF/SPTM/SLSPRSN");
//            keyLocationQueries1.add("/PTF/SPTM/CUST");
//            keyLocationQueries1.add("/PTF/SPTM/TRADER");
//            
//            
//            Long mappingConfigurationId1 = this.addMappingConfiguration(contextId1, contextId2, new Long(3), 
//                dealerToDealerId, configurationServiceClientId, "description context 1", keyLocationQueries1);
//    
//            Long targetId1 = this.addTargetSystemConfiguration("CORRECT ANSWER");
//            Long targetId2 = this.addTargetSystemConfiguration("BNPPAR");
//            Long targetId3 = this.addTargetSystemConfiguration("BLAH");
//            Long targetId4 = this.addTargetSystemConfiguration("BLAH BLAH");
//    
//            this.addSourceSystemConfiguration("azehra", mappingConfigurationId1, targetId1);
//            this.addSourceSystemConfiguration("mstewartc", mappingConfigurationId1, targetId1);
//            this.addSourceSystemConfiguration("mstewartd", mappingConfigurationId1, targetId1);
//            this.addSourceSystemConfiguration("MBS", mappingConfigurationId1, targetId1);
//    
//            this.addSourceSystemConfiguration("BNPP", mappingConfigurationId1, targetId2);
//            this.addSourceSystemConfiguration("TRSY", mappingConfigurationId1, targetId2);
//            this.addSourceSystemConfiguration("AGCY", mappingConfigurationId1, targetId2);
//            this.addSourceSystemConfiguration("MBS", mappingConfigurationId1, targetId2);
//            
//            this.addSourceSystemConfiguration("BNPP", mappingConfigurationId1, targetId3);
//            this.addSourceSystemConfiguration("TRSY", mappingConfigurationId1, targetId3);
//            this.addSourceSystemConfiguration("AGCY", mappingConfigurationId1, targetId3);
//            this.addSourceSystemConfiguration("MBS", mappingConfigurationId1, targetId3);
//            
//            this.addSourceSystemConfiguration("BNPP", mappingConfigurationId1, targetId4);
//            this.addSourceSystemConfiguration("TRSY", mappingConfigurationId1, targetId4);
//            this.addSourceSystemConfiguration("AGCY", mappingConfigurationId1, targetId4);
//            this.addSourceSystemConfiguration("MBS", mappingConfigurationId1, targetId4);
//        }
    }




    /**
     * Helper method to add the configuration type to the database.
     * 
     * @param id
     * @param name
     */
    private Long addConfigurationServiceClient(String name, String keyLocationQueryProcessorType)
    {
        ConfigurationServiceClient configurationServiceClient = new ConfigurationServiceClient();
        configurationServiceClient.setName(name);
        configurationServiceClient.setKeyLocationQueryProcessorType(keyLocationQueryProcessorType);

        return this.xaMappingConfigurationService.saveConfigurationServiceClient(configurationServiceClient);
    }

    /**
     * Helper method to add the configuration type to the database.
     * 
     * @param id
     * @param name
     */
    private Long addConfigurationType(String name)
    {
        ConfigurationType configurationType = new ConfigurationType();
        configurationType.setName(name);

        return this.xaMappingConfigurationService.saveConfigurationType(configurationType);
    }

    /**
     * Helper method to add the configuration type to the database.
     * 
     * @param id
     * @param name
     */
    private Long addConfigurationContext(String name, String description)
    {
        ConfigurationContext configurationContext = new ConfigurationContext();
        configurationContext.setName(name);
        configurationContext.setDescription(description);

        return this.xaMappingConfigurationService.saveConfigurationConext(configurationContext);
    }

    /**
     * Helper method to add a configuration context to the database.
     * 
     * @param sourceContext
     * @param targetContext
     * @param numberOfParams
     * @param configurationTypeId
     */
    private Long addMappingConfiguration(Long sourceContextId, Long targetContextId, Long numberOfParams, Long configurationTypeId,
            Long configurationServiceClientId, String description, List<String> keyLocationQueries)
    {

        return this.xaMappingConfigurationService.addMappingConfiguration(sourceContextId, targetContextId, numberOfParams, configurationTypeId, 
            configurationServiceClientId, keyLocationQueries, description);
    }

    /**
     * Helper method to add a source system value to the database.
     * 
     * @param sourceSystemValue
     * @param configurationContextId
     * @param targetConfigurationValueId
     */
    private Long addSourceSystemConfiguration(String sourceSystemValue, Long mappingConfigurationId, TargetConfigurationValue targetConfigurationValue)
    {
        SourceConfigurationValue sourceConfigurationValue = new SourceConfigurationValue();
        sourceConfigurationValue.setMappingConfigurationId(mappingConfigurationId);
        sourceConfigurationValue.setSourceSystemValue(sourceSystemValue);
        sourceConfigurationValue.setTargetConfigurationValue(targetConfigurationValue);

        return this.xaMappingConfigurationDao.storeSourceConfigurationValue(sourceConfigurationValue);
    }

    /**
     * Helper method to add a target system value to the database.
     * 
     * @param targetSystemValue
     */
    private Long addTargetSystemConfiguration(String targetSystemValue)
    {
        TargetConfigurationValue targetConfigurationValue = new TargetConfigurationValue();
        targetConfigurationValue.setTargetSystemValue(targetSystemValue);

        return this.xaMappingConfigurationDao.storeTargetConfigurationValue(targetConfigurationValue);
    }
}
