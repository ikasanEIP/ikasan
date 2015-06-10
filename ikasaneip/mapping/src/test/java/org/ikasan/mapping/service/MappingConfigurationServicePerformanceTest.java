/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.mapping.service;

import java.util.List;

import javax.annotation.Resource;

import org.ikasan.mapping.dao.HibernateMappingConfigurationDao;
import org.ikasan.mapping.keyQueryProcessor.KeyLocationQueryProcessorFactory;
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.model.TargetConfigurationValue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * @author Ikasan Development Team
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
