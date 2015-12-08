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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ikasan.mapping.dao.HibernateMappingConfigurationDao;
import org.ikasan.mapping.dao.MappingConfigurationDao;
import org.ikasan.mapping.keyQueryProcessor.KeyLocationQueryProcessorException;
import org.ikasan.mapping.keyQueryProcessor.KeyLocationQueryProcessorFactory;
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.KeyLocationQuery;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.model.TargetConfigurationValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Ikasan Development Team
 * 
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{ "/mapping-conf.xml", "/hsqldb-config.xml", "/substitute-components.xml",
		"/mock-components.xml" })
public class MappingConfigurationServiceTest
{
	Mockery mockery = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
			setThreadingPolicy(new Synchroniser());
		}
	};

	/** Object being tested */
	@Resource
	private MappingConfigurationService xaMappingConfigurationService;
	@Resource
	private MappingConfigurationDao xaMappingConfigurationDao;
	@Resource
	private KeyLocationQueryProcessorFactory keyLocationQueryProcessorFactory;

	private final MappingConfigurationDao mockMappingConfigurationDao = this.mockery
			.mock(HibernateMappingConfigurationDao.class,
					"mockMappingConfigurationDao");

	public static final String CLEAN_JGB_RAW_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><PTF><SOH><VERS>1.00</VERS>"
			+ "<MTYP>POSTTRD</MTYP><DATE>20130701</DATE><TIME>08:15:38</TIME><DLR>MZHO</DLR><USER>mzhojgbeu</USER>"
			+ "<PGRP>JGB</PGRP><TRDDT>20130701</TRDDT></SOH><SPTM attr=\"1\"><SOH><VERS>1.00</VERS><MTYP>TRDCONF</MTYP>"
			+ "<DATE>20130701</DATE><TIME>08:15:37</TIME><TSTMSG>YES</TSTMSG><DLR>MZHO</DLR><USER>mzhojgbeu</USER>"
			+ "<PGRP>JGB</PGRP><TRDTYP>OUTRIGHT</TRDTYP><TSRC>TW</TSRC><TRDDT>20130701</TRDDT><TNUM>1</TNUM></SOH><SLSPRSN>azehra</SLSPRSN>"
			+ "<CUST>mstewartc</CUST><CUSTNAME>Michael Stewart</CUSTNAME><UCNTRY>UK</UCNTRY><COMPANY>acme Trust Bk1</COMPANY>"
			+ "<COACR>CBKUAE</COACR><ODDMARK>NO</ODDMARK><LOCC>LDN</LOCC><LOCD>LDN</LOCD><CLRCD>BOJ-NET</CLRCD><STYPE>REGJNOTE</STYPE>"
			+ "<WI>NO</WI><TOSHO>01440069</TOSHO><ISIN>JP1201441D44</ISIN><CPN>1.50</CPN><AUCDT>20130418</AUCDT><MATDT>20330320</MATDT>"
			+ "<ROPN>0</ROPN><ISMN>240</ISMN><OTR>2</OTR><DTDDT>20130320</DTDDT><ISSDT>20130422</ISSDT><FCDT>20130920</FCDT>"
			+ "<ANNCDT>20130411</ANNCDT><CNTRY>JP</CNTRY><CPNFQ>2</CPNFQ><DYCTBAS>ACT/365</DYCTBAS><RVAL>100.0</RVAL><DECPLCS>3</DECPLCS>"
			+ "<DECRND>0.0010</DECRND><QID>TBSI01JP1201441D44</QID><SDESC>J20-144 1.500 20/03/33</SDESC><QTYP>YIELD</QTYP>"
			+ "<PRICE>6.2560</PRICE><YIELD>100.0</YIELD><BYIELD>26.7780</BYIELD><YTYP>BOND</YTYP><QNTY>19000000</QNTY>"
			+ "<TRANS>BUY</TRANS><STLDT>20130705</STLDT><GTRDDT>20130701</GTRDDT><GTRDTM>08:15:24</GTRDTM><SECACCR>0.4397260</SECACCR>"
			+ "<TOTACCR>83547.0</TOTACCR><PRIN>1188640.0</PRIN><NET>1272187.0</NET><CURR>JPY</CURR><FXRATE>0.0</FXRATE><NREQ>1</NREQ>"
			+ "<MTKT>1</MTKT><CUSTPRC>NO</CUSTPRC><TRADER>mstewartd</TRADER><CNTCTTRDR>Michael Stewart</CNTCTTRDR><COMPQT>1.7370</COMPQT>"
			+ "<COMPSZ>0.0</COMPSZ><CMPB>1.7610</CMPB><CMPA>1.7370</CMPA><CMPM>1.7490</CMPM><COMPMV>0.0</COMPMV><VIEWERACK>NO</VIEWERACK>"
			+ "<EOM/></SPTM><EOM/></PTF>";

	public static final String CLEAN_JGB_RAW_XML_EMPTY_SALESPERSON = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><PTF><SOH><VERS>1.00</VERS>"
			+ "<MTYP>POSTTRD</MTYP><DATE>20130701</DATE><TIME>08:15:38</TIME><DLR>MZHO</DLR><USER>mzhojgbeu</USER>"
			+ "<PGRP>JGB</PGRP><TRDDT>20130701</TRDDT></SOH><SPTM attr=\"1\"><SOH><VERS>1.00</VERS><MTYP>TRDCONF</MTYP>"
			+ "<DATE>20130701</DATE><TIME>08:15:37</TIME><TSTMSG>YES</TSTMSG><DLR>MZHO</DLR><USER>mzhojgbeu</USER>"
			+ "<PGRP>JGB</PGRP><TRDTYP>OUTRIGHT</TRDTYP><TSRC>TW</TSRC><TRDDT>20130701</TRDDT><TNUM>1</TNUM></SOH><SLSPRSN></SLSPRSN>"
			+ "<CUST>mstewartc</CUST><CUSTNAME>Michael Stewart</CUSTNAME><UCNTRY>UK</UCNTRY><COMPANY>acme Trust Bk1</COMPANY>"
			+ "<COACR>CBKUAE</COACR><ODDMARK>NO</ODDMARK><LOCC>LDN</LOCC><LOCD>LDN</LOCD><CLRCD>BOJ-NET</CLRCD><STYPE>REGJNOTE</STYPE>"
			+ "<WI>NO</WI><TOSHO>01440069</TOSHO><ISIN>JP1201441D44</ISIN><CPN>1.50</CPN><AUCDT>20130418</AUCDT><MATDT>20330320</MATDT>"
			+ "<ROPN>0</ROPN><ISMN>240</ISMN><OTR>2</OTR><DTDDT>20130320</DTDDT><ISSDT>20130422</ISSDT><FCDT>20130920</FCDT>"
			+ "<ANNCDT>20130411</ANNCDT><CNTRY>JP</CNTRY><CPNFQ>2</CPNFQ><DYCTBAS>ACT/365</DYCTBAS><RVAL>100.0</RVAL><DECPLCS>3</DECPLCS>"
			+ "<DECRND>0.0010</DECRND><QID>TBSI01JP1201441D44</QID><SDESC>J20-144 1.500 20/03/33</SDESC><QTYP>YIELD</QTYP>"
			+ "<PRICE>6.2560</PRICE><YIELD>100.0</YIELD><BYIELD>26.7780</BYIELD><YTYP>BOND</YTYP><QNTY>19000000</QNTY>"
			+ "<TRANS>BUY</TRANS><STLDT>20130705</STLDT><GTRDDT>20130701</GTRDDT><GTRDTM>08:15:24</GTRDTM><SECACCR>0.4397260</SECACCR>"
			+ "<TOTACCR>83547.0</TOTACCR><PRIN>1188640.0</PRIN><NET>1272187.0</NET><CURR>JPY</CURR><FXRATE>0.0</FXRATE><NREQ>1</NREQ>"
			+ "<MTKT>1</MTKT><CUSTPRC>NO</CUSTPRC><TRADER>mstewartd</TRADER><CNTCTTRDR>Michael Stewart</CNTCTTRDR><COMPQT>1.7370</COMPQT>"
			+ "<COMPSZ>0.0</COMPSZ><CMPB>1.7610</CMPB><CMPA>1.7370</CMPA><CMPM>1.7490</CMPM><COMPMV>0.0</COMPMV><VIEWERACK>NO</VIEWERACK>"
			+ "<EOM/></SPTM><EOM/></PTF>";

	public static final String CLEAN_JGB_RAW_XML_NO_SALESPERSON = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><PTF><SOH><VERS>1.00</VERS>"
			+ "<MTYP>POSTTRD</MTYP><DATE>20130701</DATE><TIME>08:15:38</TIME><DLR>MZHO</DLR><USER>mzhojgbeu</USER>"
			+ "<PGRP>JGB</PGRP><TRDDT>20130701</TRDDT></SOH><SPTM attr=\"1\"><SOH><VERS>1.00</VERS><MTYP>TRDCONF</MTYP>"
			+ "<DATE>20130701</DATE><TIME>08:15:37</TIME><TSTMSG>YES</TSTMSG><DLR>MZHO</DLR><USER>mzhojgbeu</USER>"
			+ "<PGRP>JGB</PGRP><TRDTYP>OUTRIGHT</TRDTYP><TSRC>TW</TSRC><TRDDT>20130701</TRDDT><TNUM>1</TNUM></SOH>"
			+ "<CUST>mstewartc</CUST><CUSTNAME>Michael Stewart</CUSTNAME><UCNTRY>UK</UCNTRY><COMPANY>acme Trust Bk1</COMPANY>"
			+ "<COACR>CBKUAE</COACR><ODDMARK>NO</ODDMARK><LOCC>LDN</LOCC><LOCD>LDN</LOCD><CLRCD>BOJ-NET</CLRCD><STYPE>REGJNOTE</STYPE>"
			+ "<WI>NO</WI><TOSHO>01440069</TOSHO><ISIN>JP1201441D44</ISIN><CPN>1.50</CPN><AUCDT>20130418</AUCDT><MATDT>20330320</MATDT>"
			+ "<ROPN>0</ROPN><ISMN>240</ISMN><OTR>2</OTR><DTDDT>20130320</DTDDT><ISSDT>20130422</ISSDT><FCDT>20130920</FCDT>"
			+ "<ANNCDT>20130411</ANNCDT><CNTRY>JP</CNTRY><CPNFQ>2</CPNFQ><DYCTBAS>ACT/365</DYCTBAS><RVAL>100.0</RVAL><DECPLCS>3</DECPLCS>"
			+ "<DECRND>0.0010</DECRND><QID>TBSI01JP1201441D44</QID><SDESC>J20-144 1.500 20/03/33</SDESC><QTYP>YIELD</QTYP>"
			+ "<PRICE>6.2560</PRICE><YIELD>100.0</YIELD><BYIELD>26.7780</BYIELD><YTYP>BOND</YTYP><QNTY>19000000</QNTY>"
			+ "<TRANS>BUY</TRANS><STLDT>20130705</STLDT><GTRDDT>20130701</GTRDDT><GTRDTM>08:15:24</GTRDTM><SECACCR>0.4397260</SECACCR>"
			+ "<TOTACCR>83547.0</TOTACCR><PRIN>1188640.0</PRIN><NET>1272187.0</NET><CURR>JPY</CURR><FXRATE>0.0</FXRATE><NREQ>1</NREQ>"
			+ "<MTKT>1</MTKT><CUSTPRC>NO</CUSTPRC><TRADER>mstewartd</TRADER><CNTCTTRDR>Michael Stewart</CNTCTTRDR><COMPQT>1.7370</COMPQT>"
			+ "<COMPSZ>0.0</COMPSZ><CMPB>1.7610</CMPB><CMPA>1.7370</CMPA><CMPM>1.7490</CMPM><COMPMV>0.0</COMPMV><VIEWERACK>NO</VIEWERACK>"
			+ "<EOM/></SPTM><EOM/></PTF>";

	/**
	 * Before each test case, inject a mock {@link HibernateTemplate} to dao
	 * implementation being tested
	 */
	@Before
	public void setup()
	{
		Long configurationServiceClientId = this
				.addConfigurationServiceClient("CMI2",
						"org.ikasan.mapping.keyQueryProcessor.impl.XPathKeyLocationQueryProcessor");
		Long dealerToDealerId = this
				.addConfigurationType("Dealer and Product to Account");
		Long salesPersonToSalesPersonId = this
				.addConfigurationType("Salesperson to Salesperson Mapping");
		Long productTypeToTradeBookId = this
				.addConfigurationType("Product Type to Tradebook Mapping");
		Long ignoreMappingId = this.addConfigurationType("Ignore Mapping");

		Long contextId1 = this.addConfigurationContext("Tradeweb", "Tradeweb");
		Long contextId2 = this
				.addConfigurationContext("Bloomberg", "Bloomberg");

		List<String> keyLocationQueries1 = new ArrayList<String>();
		keyLocationQueries1.add("some xpath");
		keyLocationQueries1.add("another xpath");
		List<String> keyLocationQueries2 = new ArrayList<String>();
		keyLocationQueries2.add("/PTF/SPTM/SLSPRSN");
		List<String> keyLocationQueries3 = new ArrayList<String>();
		keyLocationQueries3.add("some xpath");
		List<String> keyLocationQueries4 = new ArrayList<String>();
		keyLocationQueries1.add("some xpath");
		keyLocationQueries1.add("another xpath");
		keyLocationQueries1.add("another xpath");
		keyLocationQueries1.add("another xpath");

		Long mappingConfigurationId1 = this.addMappingConfiguration(contextId1,
				contextId2, new Long(2), dealerToDealerId,
				configurationServiceClientId, "description context 1",
				keyLocationQueries1);
		Long mappingConfigurationId2 = this.addMappingConfiguration(contextId1,
				contextId2, new Long(1), salesPersonToSalesPersonId,
				configurationServiceClientId, "description context 2",
				keyLocationQueries2);
		Long mappingConfigurationId3 = this.addMappingConfiguration(contextId1,
				contextId2, new Long(1), productTypeToTradeBookId,
				configurationServiceClientId, "description context 2",
				keyLocationQueries3);
		Long mappingConfigurationId4 = this.addMappingConfiguration(contextId1,
				contextId2, new Long(4), ignoreMappingId,
				configurationServiceClientId, "description",
				keyLocationQueries4);

		TargetConfigurationValue targetId1 = this
				.addTargetSystemConfiguration("BARCLON");
		TargetConfigurationValue targetId2 = this
				.addTargetSystemConfiguration("BNPPAR");
		TargetConfigurationValue targetId3 = this
				.addTargetSystemConfiguration("ZEKRAA");
		TargetConfigurationValue targetId4 = this
				.addTargetSystemConfiguration("VIDAUISA");
		TargetConfigurationValue targetId5 = this
				.addTargetSystemConfiguration("BEN");
		TargetConfigurationValue targetId6 = this
				.addTargetSystemConfiguration("IMONDIV");
		TargetConfigurationValue targetId7 = this
				.addTargetSystemConfiguration("Single Name");
		TargetConfigurationValue targetId8 = this
				.addTargetSystemConfiguration("CMS");
		TargetConfigurationValue targetId9 = this
				.addTargetSystemConfiguration("CMS");
		TargetConfigurationValue targetId10 = this
				.addTargetSystemConfiguration("Fixed-Float");
		TargetConfigurationValue targetId11 = this
				.addTargetSystemConfiguration("Basis");
		TargetConfigurationValue targetId12 = this
				.addTargetSystemConfiguration("Floater");
		TargetConfigurationValue targetId13 = this
				.addTargetSystemConfiguration("Snowball");
		TargetConfigurationValue targetId14 = this
				.addTargetSystemConfiguration("singleValueResult");
		TargetConfigurationValue targetId15 = this
				.addTargetSystemConfiguration("singleValueResult1");
		TargetConfigurationValue targetId16 = this
				.addTargetSystemConfiguration("singleValueResult2");
		
		// Add same value twice to create exception
		this.addSourceSystemConfiguration("reverse",
				mappingConfigurationId3, targetId1);
		this.addSourceSystemConfiguration("singleValueTwice",
				mappingConfigurationId4, targetId15);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId15);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId15);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId15);
		
		this.addSourceSystemConfiguration("singleValueTwice",
				mappingConfigurationId4, targetId16);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId16);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId16);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId16);

		this.addSourceSystemConfiguration("singleValue",
				mappingConfigurationId4, targetId14);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId14);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId14);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId14);

		this.addSourceSystemConfiguration("1424", mappingConfigurationId4,
				targetId13);
		this.addSourceSystemConfiguration("1424", mappingConfigurationId4,
				targetId13);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId13);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId13);

		this.addSourceSystemConfiguration("BARX", mappingConfigurationId4,
				targetId1);
		this.addSourceSystemConfiguration("TRSY", mappingConfigurationId4,
				targetId1);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId1);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId1);

		this.addSourceSystemConfiguration("1074", mappingConfigurationId4,
				targetId7);
		this.addSourceSystemConfiguration("1074", mappingConfigurationId4,
				targetId7);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId7);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId7);

		this.addSourceSystemConfiguration("1254", mappingConfigurationId4,
				targetId10);
		this.addSourceSystemConfiguration("1254", mappingConfigurationId4,
				targetId10);
		this.addSourceSystemConfiguration("Libor", mappingConfigurationId4,
				targetId10);
		this.addSourceSystemConfiguration("Libor", mappingConfigurationId4,
				targetId10);

		this.addSourceSystemConfiguration("1254", mappingConfigurationId4,
				targetId11);
		this.addSourceSystemConfiguration("1254", mappingConfigurationId4,
				targetId11);
		this.addSourceSystemConfiguration("Libor", mappingConfigurationId4,
				targetId11);
		this.addSourceSystemConfiguration("Libor/Libor",
				mappingConfigurationId4, targetId11);

		this.addSourceSystemConfiguration("1208", mappingConfigurationId4,
				targetId12);
		this.addSourceSystemConfiguration("1208", mappingConfigurationId4,
				targetId12);
		this.addSourceSystemConfiguration("Libor", mappingConfigurationId4,
				targetId12);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId12);

		this.addSourceSystemConfiguration("1208", mappingConfigurationId4,
				targetId8);
		this.addSourceSystemConfiguration("1208", mappingConfigurationId4,
				targetId8);
		this.addSourceSystemConfiguration("CMS", mappingConfigurationId4,
				targetId8);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId8);

		this.addSourceSystemConfiguration("1208", mappingConfigurationId4,
				targetId9);
		this.addSourceSystemConfiguration("1208", mappingConfigurationId4,
				targetId9);
		this.addSourceSystemConfiguration("CMS Spread",
				mappingConfigurationId4, targetId9);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId9);

		this.addSourceSystemConfiguration("Myself", mappingConfigurationId4,
				targetId2);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId2);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId2);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId2);

		this.addSourceSystemConfiguration("On My Own", mappingConfigurationId4,
				targetId3);
		this.addSourceSystemConfiguration("Value2", mappingConfigurationId4,
				targetId3);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId3);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId3);

		this.addSourceSystemConfiguration("On My Own", mappingConfigurationId4,
				targetId4);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId4);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId4);
		this.addSourceSystemConfiguration("", mappingConfigurationId4,
				targetId4);

		this.addSourceSystemConfiguration("BARX", mappingConfigurationId1,
				targetId1);
		this.addSourceSystemConfiguration("TRSY", mappingConfigurationId1,
				targetId1);
		this.addSourceSystemConfiguration("AGCY", mappingConfigurationId1,
				targetId1);
		this.addSourceSystemConfiguration("MBS", mappingConfigurationId1,
				targetId1);

		this.addSourceSystemConfiguration("BNPP", mappingConfigurationId1,
				targetId2);
		this.addSourceSystemConfiguration("TRSY", mappingConfigurationId1,
				targetId2);
		this.addSourceSystemConfiguration("AGCY", mappingConfigurationId1,
				targetId2);
		this.addSourceSystemConfiguration("MBS", mappingConfigurationId1,
				targetId2);

		this.addSourceSystemConfiguration("azehra", mappingConfigurationId2,
				targetId3);
		this.addSourceSystemConfiguration("isabelv", mappingConfigurationId2,
				targetId4);
		this.addSourceSystemConfiguration("briordan2", mappingConfigurationId2,
				targetId5);
		this.addSourceSystemConfiguration("vimondi", mappingConfigurationId2,
				targetId6);

		TargetConfigurationValue targetId70 = this
				.addTargetSystemConfiguration("YENGOVT");
		TargetConfigurationValue targetId80 = this
				.addTargetSystemConfiguration("YENTBFB");

		this.addSourceSystemConfiguration("false", mappingConfigurationId3,
				targetId70);
		this.addSourceSystemConfiguration("true", mappingConfigurationId3,
				targetId80);
	}

	@Test
	@DirtiesContext
	public void test_success_paramater_mapping_with_ignores()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1254");
		sourceSystemValues.add("1254");
		sourceSystemValues.add("Libor");
		sourceSystemValues.add("Libor");

		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Fixed-Float", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1254");
		sourceSystemValues.add("1254");
		sourceSystemValues.add("Libor");
		sourceSystemValues.add("Libor/Libor");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Basis", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("Libor");
		sourceSystemValues.add("");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Floater", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("Libor");
		sourceSystemValues.add("ignore");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Floater", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("CMS Spread");
		sourceSystemValues.add("");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("CMS", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("CMS Spread");
		sourceSystemValues.add("ignore");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("CMS", result);
		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("CMS");
		sourceSystemValues.add("ignore");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("CMS", result);

		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("CMS");
		sourceSystemValues.add("");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("CMS", result);

		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1424");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("");
		sourceSystemValues.add("");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Snowball", result);
		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1424");
		sourceSystemValues.add("1424");
		sourceSystemValues.add(null);
		sourceSystemValues.add(null);

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Snowball", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1424");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add("ignore");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Snowball", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1424");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add("");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Snowball", result);
		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1424");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add(null);

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Snowball", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("singleValue");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add("");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("singleValueResult", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("singleValue");
		sourceSystemValues.add("");
		sourceSystemValues.add("");
		sourceSystemValues.add("");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("singleValueResult", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("singleValue");
		sourceSystemValues.add("blah1");
		sourceSystemValues.add("blah2");
		sourceSystemValues.add("blah3");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("singleValueResult", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("");
		sourceSystemValues.add("");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals(null, result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("");
		sourceSystemValues.add("CMS Spread");
		sourceSystemValues.add("");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals(null, result);
		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add("ignore");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals(null, result);
		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("");
		sourceSystemValues.add("");
		sourceSystemValues.add("CMS Spread");
		sourceSystemValues.add("");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals(null, result);

	}
	
	@Test
	@DirtiesContext
	public void test_reverse_mapping()
	{
		String result = this.xaMappingConfigurationService
				.getReverseMapping("CMI2",
						"Product Type to Tradebook Mapping", "Tradeweb", "Bloomberg",
						"BARCLON");

		Assert.assertEquals("reverse", result);
	}

	@Test
	@DirtiesContext
	public void test_success_4_paramater_mapping_with_ignores_1_params()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("Myself");
		sourceSystemValues.add("blah3");
		sourceSystemValues.add("blah");
		sourceSystemValues.add("blah1");

		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("BNPPAR", result);
	}

	@Test
	@DirtiesContext
	public void test_success_4_paramater_mapping_with_ignores_3_params()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("On My Own");
		sourceSystemValues.add("Value2");
		sourceSystemValues.add("Value3");
		sourceSystemValues.add("blah1");

		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("ZEKRAA", result);
	}
	
	@DirtiesContext
	public void test_success_4_paramater_mapping_with_ignores()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("On My Own");
		sourceSystemValues.add("ignore1");
		sourceSystemValues.add("ignore2");
		sourceSystemValues.add("ignore3");

		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("VIDAUISA", result);
	}

	@DirtiesContext
	public void test_success_4_paramater_mapping_with_ignores2()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("On My Own");
		sourceSystemValues.add("Value2");
		sourceSystemValues.add("ignore2");
		sourceSystemValues.add("ignore3");

		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("ZEKRAA", result);
	}

	@Test(expected = IllegalArgumentException.class)
	@DirtiesContext
	public void test_null_keyLocationQueryProcessorFactory()
			throws KeyLocationQueryProcessorException
	{
		new MappingConfigurationServiceImpl(this.xaMappingConfigurationDao,
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	@DirtiesContext
	public void test_null_dao() throws KeyLocationQueryProcessorException
	{
		new MappingConfigurationServiceImpl(null,
				this.keyLocationQueryProcessorFactory);
	}

	@Test
	@DirtiesContext
	public void test_success_no_results()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("no_results");

		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValue("CMI2",
						"Salesperson to Salesperson Mapping", "Tradeweb",
						"Bloomberg", sourceSystemValues);

		Assert.assertEquals(null, result);
	}

	@Test
	@DirtiesContext
	public void test_success_get_mapping_configuration()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("briordan2");

		MappingConfiguration result = this.xaMappingConfigurationService
				.getMappingConfiguration("CMI2",
						"Salesperson to Salesperson Mapping", "Tradeweb",
						"Bloomberg");

		Assert.assertNotNull(result);
	}

	@Test
	@DirtiesContext
	public void test_success_get_mapping_configuration_no_result()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("briordan2");

		MappingConfiguration result = this.xaMappingConfigurationService
				.getMappingConfiguration("BAD CLIENT",
						"Salesperson to Salesperson Mapping", "Tradeweb",
						"Bloomberg");

		Assert.assertNull(result);
	}

	@Test
	@DirtiesContext
	public void test_success_1_paramater_mapping()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("briordan2");

		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValue("CMI2",
						"Salesperson to Salesperson Mapping", "Tradeweb",
						"Bloomberg", sourceSystemValues);

		Assert.assertEquals("BEN", result);
	}

	@Test
	@DirtiesContext
	public void test_success_1_paramater_mapping_not_passing_list_for_source_system_values()
	{
		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValue("CMI2",
						"Salesperson to Salesperson Mapping", "Tradeweb",
						"Bloomberg", "briordan2");

		Assert.assertEquals("BEN", result);
	}

	@Test
	@DirtiesContext
	public void test_success_2_paramater_mapping()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("BARX");
		sourceSystemValues.add("TRSY");

		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValue("CMI2",
						"Dealer and Product to Account", "Tradeweb",
						"Bloomberg", sourceSystemValues);

		Assert.assertEquals("BARCLON", result);
	}
	
	@Test(expected=RuntimeException.class)
	@DirtiesContext
	public void test_exception_duplicateMapping()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("singleValueTwice");
		sourceSystemValues.add("sdsdf");
		sourceSystemValues.add("sfsdf");
		sourceSystemValues.add("sfsdfsd");

		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg", sourceSystemValues);
	}

	@Test
	@DirtiesContext
	public void test_success_2_paramater_mapping_2()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("true");

		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValue("CMI2",
						"Product Type to Tradebook Mapping", "Tradeweb",
						"Bloomberg", sourceSystemValues);

		Assert.assertEquals("YENTBFB", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("false");

		result = this.xaMappingConfigurationService
				.getTargetConfigurationValue("CMI2",
						"Product Type to Tradebook Mapping", "Tradeweb",
						"Bloomberg", sourceSystemValues);

		Assert.assertEquals("YENGOVT", result);
	}

	@Test
	@DirtiesContext
	public void test_get_all_configuration_context_success()
	{
		List<ConfigurationContext> result = this.xaMappingConfigurationService
				.getAllConfigurationContexts();

		Assert.assertEquals(2, result.size());
	}

	@Test
	@DirtiesContext
	public void test_get_all_configuration_types_success()
	{
		List<ConfigurationType> result = this.xaMappingConfigurationService
				.getAllConfigurationTypes();

		Assert.assertEquals(4, result.size());
	}

	@Test
	@DirtiesContext
	public void test_get_all_configuration_service_clients_success()
	{
		List<ConfigurationServiceClient> result = this.xaMappingConfigurationService
				.getAllConfigurationServiceClients();

		Assert.assertEquals(1, result.size());
	}

	@Test
	@DirtiesContext
	public void test_get_target_system_value_with_payload_and_client_name_success()
			throws MappingConfigurationServiceException
	{
		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValue("CMI2",
						"Salesperson to Salesperson Mapping", "Tradeweb",
						"Bloomberg", CLEAN_JGB_RAW_XML.getBytes());

		Assert.assertEquals("ZEKRAA", result);
	}

	@Test(expected = MappingConfigurationServiceException.class)
	@DirtiesContext
	public void test_get_target_system_value_with_paylaod_and_client_name_empty_xml_node_fail()
			throws MappingConfigurationServiceException
	{
		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValue("CMI2",
						"Salesperson to Salesperson Mapping", "Tradeweb",
						"Bloomberg",
						CLEAN_JGB_RAW_XML_EMPTY_SALESPERSON.getBytes());
	}

	@Test(expected = MappingConfigurationServiceException.class)
	@DirtiesContext
	public void test_get_target_system_value_with_paylaod_and_client_name_no_xml_node_fail()
			throws MappingConfigurationServiceException
	{
		String result = this.xaMappingConfigurationService
				.getTargetConfigurationValue("CMI2",
						"Salesperson to Salesperson Mapping", "Tradeweb",
						"Bloomberg",
						CLEAN_JGB_RAW_XML_NO_SALESPERSON.getBytes());
	}

	@Test
	@DirtiesContext
	public void test_save_target_configuration_value()
			throws MappingConfigurationServiceException
	{
		List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
				.getAllMappingConfigurations();

		for (MappingConfiguration mappingConfiguration : mappingConfigurations)
		{
			List<SourceConfigurationValue> sourceConfigurationValues = this.xaMappingConfigurationDao
					.getSourceConfigurationValueByMappingConfigurationId(mappingConfiguration
							.getId());

			for (SourceConfigurationValue sourceConfigurationValue : sourceConfigurationValues)
			{
				TargetConfigurationValue targetValue = this.xaMappingConfigurationDao
						.getTargetConfigurationValueById(sourceConfigurationValue
								.getTargetConfigurationValue().getId());

				targetValue.setTargetSystemValue("new value");

				this.xaMappingConfigurationDao
						.storeTargetConfigurationValue(targetValue);

				TargetConfigurationValue targetValueStored = this.xaMappingConfigurationDao
						.getTargetConfigurationValueById(targetValue.getId());

				Assert.assertEquals(targetValue.getTargetSystemValue(),
						targetValueStored.getTargetSystemValue());
				Assert.assertEquals(targetValue.getUpdatedDateTime().getTime(),
						targetValueStored.getUpdatedDateTime().getTime());
			}
		}
	}

	@Test
	@DirtiesContext
	public void test_update_source_configuration_value()
			throws MappingConfigurationServiceException
	{
		List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
				.getAllMappingConfigurations();

		for (MappingConfiguration mappingConfiguration : mappingConfigurations)
		{
			List<SourceConfigurationValue> sourceConfigurationValues = this.xaMappingConfigurationDao
					.getSourceConfigurationValueByMappingConfigurationId(mappingConfiguration
							.getId());

			for (SourceConfigurationValue sourceConfigurationValue : sourceConfigurationValues)
			{
				sourceConfigurationValue.setSourceSystemValue("new value");

				this.xaMappingConfigurationDao
						.storeSourceConfigurationValue(sourceConfigurationValue);
			}
		}

		for (MappingConfiguration mappingConfiguration : mappingConfigurations)
		{
			List<SourceConfigurationValue> sourceConfigurationValues = this.xaMappingConfigurationDao
					.getSourceConfigurationValueByMappingConfigurationId(mappingConfiguration
							.getId());

			for (SourceConfigurationValue sourceConfigurationValue : sourceConfigurationValues)
			{
				Assert.assertEquals("new value",
						sourceConfigurationValue.getSourceSystemValue());
			}
		}
	}

	@Test
	@DirtiesContext
	public void test_get_mapping_configuration_by_id()
			throws MappingConfigurationServiceException
	{
		List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
				.getAllMappingConfigurations();

		for (MappingConfiguration mappingConfiguration : mappingConfigurations)
		{
			MappingConfiguration mappingConfigurationSearched = this.xaMappingConfigurationService
					.getMappingConfigurationById(mappingConfiguration.getId());

			Assert.assertEquals(mappingConfiguration.getId(),
					mappingConfigurationSearched.getId());
		}
	}

	@Test
	@DirtiesContext
	public void test_get_mapping_configuration_by_configuration_service_client_id()
			throws MappingConfigurationServiceException
	{
		List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
				.getAllMappingConfigurations();

		for (MappingConfiguration mappingConfiguration : mappingConfigurations)
		{
			List<MappingConfiguration> mappingConfigurationsSearched = this.xaMappingConfigurationService
					.getMappingConfigurationsByConfigurationServiceClientId(mappingConfiguration
							.getConfigurationServiceClient().getId());

			ArrayList<Long> ids = new ArrayList<Long>();

			for (MappingConfiguration mappingConfiguration1 : mappingConfigurationsSearched)
			{
				ids.add(mappingConfiguration1.getId());
			}

			Assert.assertTrue(ids.contains(mappingConfiguration.getId()));
		}
	}

	@Test
	@DirtiesContext
	public void test_get_mapping_configuration_by_configuration_type_id()
			throws MappingConfigurationServiceException
	{
		List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
				.getAllMappingConfigurations();

		for (MappingConfiguration mappingConfiguration : mappingConfigurations)
		{
			List<MappingConfiguration> mappingConfigurationsSearched = this.xaMappingConfigurationService
					.getMappingConfigurationsByConfigurationTypeId(mappingConfiguration
							.getConfigurationType().getId());

			ArrayList<Long> ids = new ArrayList<Long>();

			for (MappingConfiguration mappingConfiguration1 : mappingConfigurationsSearched)
			{
				ids.add(mappingConfiguration1.getId());
			}

			Assert.assertTrue(ids.contains(mappingConfiguration.getId()));
		}
	}

	@Test
	@DirtiesContext
	public void test_get_mapping_configuration_by_source_context_id()
			throws MappingConfigurationServiceException
	{
		List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
				.getAllMappingConfigurations();

		for (MappingConfiguration mappingConfiguration : mappingConfigurations)
		{
			List<MappingConfiguration> mappingConfigurationsSearched = this.xaMappingConfigurationService
					.getMappingConfigurationsBySourceContextId(mappingConfiguration
							.getSourceContext().getId());

			ArrayList<Long> ids = new ArrayList<Long>();

			for (MappingConfiguration mappingConfiguration1 : mappingConfigurationsSearched)
			{
				ids.add(mappingConfiguration1.getId());
			}

			Assert.assertTrue(ids.contains(mappingConfiguration.getId()));
		}
	}

	@Test
	@DirtiesContext
	public void test_get_mapping_configuration_by_target_context_id()
			throws MappingConfigurationServiceException
	{
		List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
				.getAllMappingConfigurations();

		for (MappingConfiguration mappingConfiguration : mappingConfigurations)
		{
			List<MappingConfiguration> mappingConfigurationsSearched = this.xaMappingConfigurationService
					.getMappingConfigurationsByTargetContextId(mappingConfiguration
							.getTargetContext().getId());

			ArrayList<Long> ids = new ArrayList<Long>();

			for (MappingConfiguration mappingConfiguration1 : mappingConfigurationsSearched)
			{
				ids.add(mappingConfiguration1.getId());
			}

			Assert.assertTrue(ids.contains(mappingConfiguration.getId()));
		}
	}

	@Test
	@DirtiesContext
	public void test_get_configuration_context_by_context_id()
			throws MappingConfigurationServiceException
	{
		List<ConfigurationContext> configurationContexts = this.xaMappingConfigurationDao
				.getAllConfigurationContexts();

		for (ConfigurationContext configurationContext : configurationContexts)
		{
			ConfigurationContext configurationContextSearched = this.xaMappingConfigurationService
					.getConfigurationContextById(configurationContext.getId());

			Assert.assertEquals(configurationContext,
					configurationContextSearched);
		}
	}

	@Test
	@DirtiesContext
	public void test_get_configuration_service_client_by_id()
			throws MappingConfigurationServiceException
	{
		List<ConfigurationServiceClient> configurationServiceClients = this.xaMappingConfigurationDao
				.getAllConfigurationServiceClients();

		for (ConfigurationServiceClient configurationServiceClient : configurationServiceClients)
		{
			ConfigurationServiceClient configurationServiceClientSearched = this.xaMappingConfigurationService
					.getConfigurationServiceClientById(configurationServiceClient
							.getId());

			Assert.assertEquals(configurationServiceClient,
					configurationServiceClientSearched);
		}
	}

	@Test
	@DirtiesContext
	public void test_get_configuration_type_by_id()
			throws MappingConfigurationServiceException
	{
		List<ConfigurationType> configurationTypes = this.xaMappingConfigurationDao
				.getAllConfigurationTypes();

		for (ConfigurationType configurationType : configurationTypes)
		{
			ConfigurationType configurationTypeSearched = this.xaMappingConfigurationService
					.getConfigurationTypeById(configurationType.getId());

			Assert.assertEquals(configurationType, configurationTypeSearched);
		}
	}

	@Test
	@DirtiesContext
	public void test_get_configuration_types_by_client_name()
			throws MappingConfigurationServiceException
	{
		List<ConfigurationType> configurationTypes = this.xaMappingConfigurationService
				.getConfigurationTypesByClientName("CMI2");

		Assert.assertNotNull(configurationTypes);
	}

	@Test
	@DirtiesContext
	public void test_get_source_configuration_contexts_by_client_name_and_type()
			throws MappingConfigurationServiceException
	{
		List<ConfigurationContext> configurationContexts = this.xaMappingConfigurationService
				.getSourceConfigurationContextsByClientNameAndType("CMI2",
						"Salesperson to Salesperson Mapping");

		Assert.assertNotNull(configurationContexts);

		configurationContexts = this.xaMappingConfigurationService
				.getSourceConfigurationContextsByClientNameAndType("CMI2", null);

		Assert.assertNotNull(configurationContexts);

		configurationContexts = this.xaMappingConfigurationService
				.getSourceConfigurationContextsByClientNameAndType("blah",
						"Salesperson to Salesperson Mapping");

		Assert.assertNotNull(configurationContexts);

		Assert.assertEquals(configurationContexts.size(), 0);
	}

	@Test
	@DirtiesContext
	public void test_get_target_configuration_contexts_by_client_name_type_and_source_context()
			throws MappingConfigurationServiceException
	{
		List<ConfigurationContext> configurationContexts = this.xaMappingConfigurationService
				.getTargetConfigurationContextByClientNameTypeAndSourceContext(
						"CMI2", "Salesperson to Salesperson Mapping",
						"Tradeweb");

		Assert.assertNotNull(configurationContexts);

		configurationContexts = this.xaMappingConfigurationService
				.getTargetConfigurationContextByClientNameTypeAndSourceContext(
						"CMI2", "Salesperson to Salesperson Mapping", null);

		Assert.assertNotNull(configurationContexts);

		configurationContexts = this.xaMappingConfigurationService
				.getTargetConfigurationContextByClientNameTypeAndSourceContext(
						"CMI2", null, null);

		Assert.assertNotNull(configurationContexts);
	}

	@Test
	@DirtiesContext
	public void test_get_key_location_queries_by_mapping_configuration_id()
			throws MappingConfigurationServiceException
	{
		List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
				.getAllMappingConfigurations();

		for (MappingConfiguration mappingConfiguration : mappingConfigurations)
		{
			List<KeyLocationQuery> keyLocationQueries = this.xaMappingConfigurationService
					.getKeyLocationQueriesByMappingConfigurationId(mappingConfiguration
							.getId());

			for (KeyLocationQuery keyLocationQuery : keyLocationQueries)
			{
				Assert.assertEquals(mappingConfiguration.getId(),
						keyLocationQuery.getMappingConfigurationId());
			}
		}
	}

	@Test
	@DirtiesContext
	public void test_get_source_configuration_value_by_mapping_configuration_id()
			throws MappingConfigurationServiceException
	{
		List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
				.getAllMappingConfigurations();

		for (MappingConfiguration mappingConfiguration : mappingConfigurations)
		{
			List<SourceConfigurationValue> sourceConfigutationValues = this.xaMappingConfigurationService
					.getSourceConfigurationValueByMappingConfigurationId(mappingConfiguration
							.getId());

			for (SourceConfigurationValue sourceConfigutationValue : sourceConfigutationValues)
			{
				Assert.assertEquals(mappingConfiguration.getId(),
						sourceConfigutationValue.getMappingConfigurationId());
			}
		}
	}

	@Test
	@DirtiesContext
	public void test_get_target_configuration_value_by_mapping_configuration_id()
			throws MappingConfigurationServiceException
	{
		List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
				.getAllMappingConfigurations();

		for (MappingConfiguration mappingConfiguration : mappingConfigurations)
		{
			List<SourceConfigurationValue> sourceConfigutationValues = this.xaMappingConfigurationService
					.getSourceConfigurationValueByMappingConfigurationId(mappingConfiguration
							.getId());

			for (SourceConfigurationValue sourceConfigutationValue : sourceConfigutationValues)
			{
				TargetConfigurationValue targetConfigurationValue = this.xaMappingConfigurationService
						.getTargetConfigurationValueById(sourceConfigutationValue
								.getTargetConfigurationValue().getId());
				Assert.assertEquals(targetConfigurationValue.getId(),
						sourceConfigutationValue.getTargetConfigurationValue()
								.getId());
			}
		}
	}

	@Test(expected = MappingConfigurationServiceException.class)
	@DirtiesContext
	public void test_resolution_of_target_configuration_value_returns_null_string_fail()
			throws MappingConfigurationServiceException
	{
		final List<String> keyLocationQueries = new ArrayList<String>();
		keyLocationQueries.add("/PTF/SPTM/SLSPRSN");
		// expectations
		mockery.checking(new Expectations()
		{
			{
				one(mockMappingConfigurationDao).getKeyLocationQuery(
						with(any(String.class)), with(any(String.class)),
						with(any(String.class)), with(any(String.class)));
				will(returnValue(keyLocationQueries));
				one(mockMappingConfigurationDao).getTargetConfigurationValue(
						with(any(String.class)), with(any(String.class)),
						with(any(String.class)), with(any(String.class)),
						with(any(ArrayList.class)));
				will(returnValue(null));
			}
		});

		MappingConfigurationService serviceToTest = new MappingConfigurationServiceImpl(
				mockMappingConfigurationDao, keyLocationQueryProcessorFactory);
		serviceToTest.getTargetConfigurationValue("CMI2",
				"Salesperson to Salesperson Mapping", "Tradeweb", "Bloomberg",
				CLEAN_JGB_RAW_XML.getBytes());

		mockery.assertIsSatisfied();
	}

	@Test(expected = MappingConfigurationServiceException.class)
	@DirtiesContext
	public void test_resolution_of_target_configuration_value_returns_empty_string_fail()
			throws MappingConfigurationServiceException
	{
		final List<String> keyLocationQueries = new ArrayList<String>();
		keyLocationQueries.add("/PTF/SPTM/SLSPRSN");
		// expectations
		mockery.checking(new Expectations()
		{
			{
				one(mockMappingConfigurationDao).getKeyLocationQuery(
						with(any(String.class)), with(any(String.class)),
						with(any(String.class)), with(any(String.class)));
				will(returnValue(keyLocationQueries));
				one(mockMappingConfigurationDao).getTargetConfigurationValue(
						with(any(String.class)), with(any(String.class)),
						with(any(String.class)), with(any(String.class)),
						with(any(ArrayList.class)));
				will(returnValue(""));
			}
		});

		MappingConfigurationService serviceToTest = new MappingConfigurationServiceImpl(
				mockMappingConfigurationDao, keyLocationQueryProcessorFactory);
		serviceToTest.getTargetConfigurationValue("CMI2",
				"Salesperson to Salesperson Mapping", "Tradeweb", "Bloomberg",
				CLEAN_JGB_RAW_XML.getBytes());

		mockery.assertIsSatisfied();
	}

	/**
	 * Helper method to add the configuration type to the database.
	 * 
	 * @param id
	 * @param name
	 */
	private Long addConfigurationServiceClient(String name,
			String keyLocationQueryProcessorType)
	{
		ConfigurationServiceClient configurationServiceClient = new ConfigurationServiceClient();
		configurationServiceClient.setName(name);
		configurationServiceClient
				.setKeyLocationQueryProcessorType(keyLocationQueryProcessorType);

		return this.xaMappingConfigurationService
				.saveConfigurationServiceClient(configurationServiceClient);
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

		return this.xaMappingConfigurationService
				.saveConfigurationType(configurationType);
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

		return this.xaMappingConfigurationService
				.saveConfigurationConext(configurationContext);
	}

	/**
	 * Helper method to add a configuration context to the database.
	 * 
	 * @param sourceContext
	 * @param targetContext
	 * @param numberOfParams
	 * @param configurationTypeId
	 */
	private Long addMappingConfiguration(Long sourceContextId,
			Long targetContextId, Long numberOfParams,
			Long configurationTypeId, Long configurationServiceClientId,
			String description, List<String> keyLocationQueries)
	{

		return this.xaMappingConfigurationService.addMappingConfiguration(
				sourceContextId, targetContextId, numberOfParams,
				configurationTypeId, configurationServiceClientId,
				keyLocationQueries, description);
	}

	/**
	 * Helper method to add a source system value to the database.
	 * 
	 * @param sourceSystemValue
	 * @param configurationContextId
	 * @param targetConfigurationValueId
	 */
	private Long addSourceSystemConfiguration(String sourceSystemValue,
			Long mappingConfigurationId,
			TargetConfigurationValue targetConfigurationValue)
	{
		SourceConfigurationValue sourceConfigurationValue = new SourceConfigurationValue();
		sourceConfigurationValue
				.setMappingConfigurationId(mappingConfigurationId);
		sourceConfigurationValue.setSourceSystemValue(sourceSystemValue);
		sourceConfigurationValue
				.setTargetConfigurationValue(targetConfigurationValue);

		return this.xaMappingConfigurationDao
				.storeSourceConfigurationValue(sourceConfigurationValue);
	}

	/**
	 * Helper method to add a target system value to the database.
	 * 
	 * @param targetSystemValue
	 */
	private TargetConfigurationValue addTargetSystemConfiguration(
			String targetSystemValue)
	{
		TargetConfigurationValue targetConfigurationValue = new TargetConfigurationValue();
		targetConfigurationValue.setTargetSystemValue(targetSystemValue);

		this.xaMappingConfigurationDao
				.storeTargetConfigurationValue(targetConfigurationValue);

		return targetConfigurationValue;
	}
}
