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
import org.ikasan.mapping.model.*;
import org.ikasan.mapping.service.configuration.MappingConfigurationServiceConfiguration;
import org.ikasan.spec.mapping.MappingService;
import org.ikasan.spec.mapping.NamedResult;
import org.ikasan.spec.mapping.QueryParameter;
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
	private MappingManagementService xaMappingManagementService;

	@Resource
	MappingService xaMappingService;
	@Resource
	private MappingConfigurationDao xaMappingConfigurationDao;

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
				.addConfigurationServiceClient("CMI2");
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
				contextId2, 2, dealerToDealerId,
				configurationServiceClientId, "description context 1",
				keyLocationQueries1);
		Long mappingConfigurationId2 = this.addMappingConfiguration(contextId1,
				contextId2, 1, salesPersonToSalesPersonId,
				configurationServiceClientId, "description context 2",
				keyLocationQueries2);
		Long mappingConfigurationId3 = this.addMappingConfiguration(contextId1,
				contextId2, 1, productTypeToTradeBookId,
				configurationServiceClientId, "description context 2",
				keyLocationQueries3);
		Long mappingConfigurationId4 = this.addMappingConfiguration(contextId1,
				contextId2, 4, ignoreMappingId,
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
		this.addSourceSystemConfiguration("reverse", "name1",
				mappingConfigurationId3, targetId1);
		this.addSourceSystemConfiguration("singleValueTwice","name2",
				mappingConfigurationId4, targetId15);
		this.addSourceSystemConfiguration("", "name3",mappingConfigurationId4,
				targetId15);
		this.addSourceSystemConfiguration("", "name4",mappingConfigurationId4,
				targetId15);
		this.addSourceSystemConfiguration("", "name5",mappingConfigurationId4,
				targetId15);
		
		this.addSourceSystemConfiguration("singleValueTwice","name1",
				mappingConfigurationId4, targetId16);
		this.addSourceSystemConfiguration("", "name2", mappingConfigurationId4,
				targetId16);
		this.addSourceSystemConfiguration("", "name3",mappingConfigurationId4,
				targetId16);
		this.addSourceSystemConfiguration("", "name4",mappingConfigurationId4,
				targetId16);

		this.addSourceSystemConfiguration("singleValue","name1",
				mappingConfigurationId4, targetId14);
		this.addSourceSystemConfiguration("", "name2",mappingConfigurationId4,
				targetId14);
		this.addSourceSystemConfiguration("", "name3",mappingConfigurationId4,
				targetId14);
		this.addSourceSystemConfiguration("", "name4",mappingConfigurationId4,
				targetId14);

		this.addSourceSystemConfiguration("1424", "name1", mappingConfigurationId4,
				targetId13);
		this.addSourceSystemConfiguration("1424", "name2",mappingConfigurationId4,
				targetId13);
		this.addSourceSystemConfiguration("", "name3", mappingConfigurationId4,
				targetId13);
		this.addSourceSystemConfiguration("", "name4", mappingConfigurationId4,
				targetId13);

		this.addSourceSystemConfiguration("BARX", "name1",mappingConfigurationId4,
				targetId1);
		this.addSourceSystemConfiguration("TRSY","name2", mappingConfigurationId4,
				targetId1);
		this.addSourceSystemConfiguration("", "name3",mappingConfigurationId4,
				targetId1);
		this.addSourceSystemConfiguration("", "name4",mappingConfigurationId4,
				targetId1);

		this.addSourceSystemConfiguration("1074", "name1",mappingConfigurationId4,
				targetId7);
		this.addSourceSystemConfiguration("1074", "name2", mappingConfigurationId4,
				targetId7);
		this.addSourceSystemConfiguration("", "name3",mappingConfigurationId4,
				targetId7);
		this.addSourceSystemConfiguration("", "name4", mappingConfigurationId4,
				targetId7);

		this.addSourceSystemConfiguration("1254", "name1",mappingConfigurationId4,
				targetId10);
		this.addSourceSystemConfiguration("1254", "name2",mappingConfigurationId4,
				targetId10);
		this.addSourceSystemConfiguration("Libor", "name3",mappingConfigurationId4,
				targetId10);
		this.addSourceSystemConfiguration("Libor", "name4", mappingConfigurationId4,
				targetId10);

		this.addSourceSystemConfiguration("1254", "name1",mappingConfigurationId4,
				targetId11);
		this.addSourceSystemConfiguration("1254", "name2",mappingConfigurationId4,
				targetId11);
		this.addSourceSystemConfiguration("Libor", "name3",mappingConfigurationId4,
				targetId11);
		this.addSourceSystemConfiguration("Libor/Libor","name4",
				mappingConfigurationId4, targetId11);

		this.addSourceSystemConfiguration("1208", "name1",mappingConfigurationId4,
				targetId12);
		this.addSourceSystemConfiguration("1208", "name2",mappingConfigurationId4,
				targetId12);
		this.addSourceSystemConfiguration("Libor","name3", mappingConfigurationId4,
				targetId12);
		this.addSourceSystemConfiguration("", "name4",mappingConfigurationId4,
				targetId12);

		this.addSourceSystemConfiguration("1208", "name1",mappingConfigurationId4,
				targetId8);
		this.addSourceSystemConfiguration("1208", "name2",mappingConfigurationId4,
				targetId8);
		this.addSourceSystemConfiguration("CMS", "name3",mappingConfigurationId4,
				targetId8);
		this.addSourceSystemConfiguration("", "name4",mappingConfigurationId4,
				targetId8);

		this.addSourceSystemConfiguration("1208", "name1",mappingConfigurationId4,
				targetId9);
		this.addSourceSystemConfiguration("1208", "name2",mappingConfigurationId4,
				targetId9);
		this.addSourceSystemConfiguration("CMS Spread","name3",
				mappingConfigurationId4, targetId9);
		this.addSourceSystemConfiguration("", "name4",mappingConfigurationId4,
				targetId9);

		this.addSourceSystemConfiguration("Myself", "name1",mappingConfigurationId4,
				targetId2);
		this.addSourceSystemConfiguration("", "name2",mappingConfigurationId4,
				targetId2);
		this.addSourceSystemConfiguration("", "name3",mappingConfigurationId4,
				targetId2);
		this.addSourceSystemConfiguration("", "name4",mappingConfigurationId4,
				targetId2);

		this.addSourceSystemConfiguration("On My Own", "name1",mappingConfigurationId4,
				targetId3);
		this.addSourceSystemConfiguration("Value2", "name2",mappingConfigurationId4,
				targetId3);
		this.addSourceSystemConfiguration("", "name3",mappingConfigurationId4,
				targetId3);
		this.addSourceSystemConfiguration("", "name4",mappingConfigurationId4,
				targetId3);

		this.addSourceSystemConfiguration("On My Own", "name1",mappingConfigurationId4,
				targetId4);
		this.addSourceSystemConfiguration("", "name2",mappingConfigurationId4,
				targetId4);
		this.addSourceSystemConfiguration("", "name3",mappingConfigurationId4,
				targetId4);
		this.addSourceSystemConfiguration("", "name4",mappingConfigurationId4,
				targetId4);

		this.addSourceSystemConfiguration("BARX","name1", mappingConfigurationId1,
				targetId1);
		this.addSourceSystemConfiguration("TRSY", "name2",mappingConfigurationId1,
				targetId1);
		this.addSourceSystemConfiguration("AGCY", "name3",mappingConfigurationId1,
				targetId1);
		this.addSourceSystemConfiguration("MBS", "name4",mappingConfigurationId1,
				targetId1);

		this.addSourceSystemConfiguration("BNPP", "name1",mappingConfigurationId1,
				targetId2);
		this.addSourceSystemConfiguration("TRSY", "name2",mappingConfigurationId1,
				targetId2);
		this.addSourceSystemConfiguration("AGCY", "name3",mappingConfigurationId1,
				targetId2);
		this.addSourceSystemConfiguration("MBS", "name4",mappingConfigurationId1,
				targetId2);

		this.addSourceSystemConfiguration("azehra", "name1",mappingConfigurationId2,
				targetId3);
		this.addSourceSystemConfiguration("isabelv", "name2",mappingConfigurationId2,
				targetId4);
		this.addSourceSystemConfiguration("briordan2", "name3",mappingConfigurationId2,
				targetId5);
		this.addSourceSystemConfiguration("vimondi", "name4",mappingConfigurationId2,
				targetId6);

		TargetConfigurationValue targetId70 = this
				.addTargetSystemConfiguration("YENGOVT");
		TargetConfigurationValue targetId80 = this
				.addTargetSystemConfiguration("YENTBFB");

		this.addSourceSystemConfiguration("false","name1", mappingConfigurationId3,
				targetId70);
		this.addSourceSystemConfiguration("true","name2", mappingConfigurationId3,
				targetId80);


		ConfigurationServiceClient configurationServiceClient = this.addConfigurationServiceClient2("CMI22");

		ConfigurationContext context1 = this.addConfigurationContext2("Tradeweb2", "Tradeweb2");
		ConfigurationContext context2 = this.addConfigurationContext2("Bloomberg2", "Bloomberg2");

		ConfigurationType manyToManyMapping = this.addConfigurationType2("Many to Many");

		Long configurationContextId4 = this.addMappingConfiguration(context1, context2, -1,
				manyToManyMapping, configurationServiceClient, "description context 4");



		ArrayList<String> sourceValues = new ArrayList<>();
		sourceValues.add("s1");
		sourceValues.add("s2");
		sourceValues.add("s3");
		sourceValues.add("s4");

		ArrayList<String> targetValues = new ArrayList<>();
		targetValues.add("t1");
		targetValues.add("t2");
		targetValues.add("t3");
		targetValues.add("t4");

		this.addManyToManySourceSystemConfiguration(sourceValues, targetValues, configurationContextId4, 1l);

		sourceValues = new ArrayList<>();
		sourceValues.add("s5");
		sourceValues.add("s6");
		sourceValues.add("s7");
		sourceValues.add("s8");

		targetValues = new ArrayList<>();
		targetValues.add("t5");
		targetValues.add("t6");
		targetValues.add("t7");
		targetValues.add("t8");
		targetValues.add("t9");
		targetValues.add("t10");
		targetValues.add("t11");
		targetValues.add("t12");

		this.addManyToManySourceSystemConfiguration(sourceValues, targetValues, configurationContextId4, 2l);

		sourceValues = new ArrayList<>();
		sourceValues.add("alone");

		targetValues = new ArrayList<>();
		targetValues.add("t13");
		targetValues.add("t14");
		targetValues.add("t15");
		targetValues.add("t16");
		targetValues.add("t17");
		targetValues.add("t18");
		targetValues.add("t19");
		targetValues.add("t20");

		this.addManyToManySourceSystemConfiguration(sourceValues, targetValues, configurationContextId4, 3l);
	}

	@Test
	@DirtiesContext
	public void test_success_many_to_many()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("s1");
		sourceSystemValues.add("s2");
		sourceSystemValues.add("s3");
		sourceSystemValues.add("s4");

		List<String> result = this.xaMappingService.getTargetConfigurationValues("CMI22", "Many to Many", "Tradeweb2",
				"Bloomberg2", sourceSystemValues);

		Assert.assertEquals(4, result.size());
		Assert.assertTrue(result.contains("t1"));
		Assert.assertTrue(result.contains("t2"));
		Assert.assertTrue(result.contains("t3"));
		Assert.assertTrue(result.contains("t4"));

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("s5");
		sourceSystemValues.add("s6");
		sourceSystemValues.add("s7");
		sourceSystemValues.add("s8");

		result = this.xaMappingService.getTargetConfigurationValues("CMI22", "Many to Many", "Tradeweb2",
				"Bloomberg2", sourceSystemValues);

		Assert.assertEquals(8, result.size());

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("s1");
		sourceSystemValues.add("s6");
		sourceSystemValues.add("s7");
		sourceSystemValues.add("s8");

		result = this.xaMappingService.getTargetConfigurationValues("CMI22", "Many to Many", "Tradeweb2",
				"Bloomberg2", sourceSystemValues);

		Assert.assertEquals(0, result.size());

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("alone");

		result = this.xaMappingService.getTargetConfigurationValues("CMI22", "Many to Many", "Tradeweb2",
				"Bloomberg2", sourceSystemValues);

		Assert.assertEquals(8, result.size());

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("s5");
		sourceSystemValues.add("s6");

		result = this.xaMappingService.getTargetConfigurationValues("CMI22", "Many to Many", "Tradeweb2",
				"Bloomberg2", sourceSystemValues);

		Assert.assertEquals(0, result.size());

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("s5");
		sourceSystemValues.add("s6");
		sourceSystemValues.add("s3");
		sourceSystemValues.add("s4");

		result = this.xaMappingService.getTargetConfigurationValues("CMI22", "Many to Many", "Tradeweb2",
				"Bloomberg2", sourceSystemValues);

		Assert.assertEquals(0, result.size());
	}

	@Test
	@DirtiesContext
	public void test_success_many_to_many_with_ordinality()
	{
		List<QueryParameter> sourceSystemValues = new ArrayList<QueryParameter>();
		sourceSystemValues.add(this.createQueryParameter("name1", "s1"));
		sourceSystemValues.add(this.createQueryParameter("name2","s2"));
		sourceSystemValues.add(this.createQueryParameter("name3","s3"));
		sourceSystemValues.add(this.createQueryParameter("name4","s4"));

		List<NamedResult> result = this.xaMappingService.getTargetConfigurationValuesWithOrdinality("CMI22", "Many to Many", "Tradeweb2",
				"Bloomberg2", sourceSystemValues);

		Assert.assertEquals(4, result.size());

		sourceSystemValues = new ArrayList<QueryParameter>();
		sourceSystemValues.add(this.createQueryParameter("name1", "s2"));
		sourceSystemValues.add(this.createQueryParameter("name2","s2"));
		sourceSystemValues.add(this.createQueryParameter("name3","s3"));
		sourceSystemValues.add(this.createQueryParameter("name4","s4"));

		result = this.xaMappingService.getTargetConfigurationValuesWithOrdinality("CMI22", "Many to Many", "Tradeweb2",
				"Bloomberg2", sourceSystemValues);

		Assert.assertEquals(0, result.size());

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

		String result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Fixed-Float", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1254");
		sourceSystemValues.add("1254");
		sourceSystemValues.add("Libor");
		sourceSystemValues.add("Libor/Libor");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Basis", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("Libor");
		sourceSystemValues.add("");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Floater", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("Libor");
		sourceSystemValues.add("ignore");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Floater", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("CMS Spread");
		sourceSystemValues.add("");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("CMS", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("CMS Spread");
		sourceSystemValues.add("ignore");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("CMS", result);
		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("CMS");
		sourceSystemValues.add("ignore");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("CMS", result);

		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("1208");
		sourceSystemValues.add("CMS");
		sourceSystemValues.add("");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("CMS", result);

		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1424");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("");
		sourceSystemValues.add("");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Snowball", result);
		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1424");
		sourceSystemValues.add("1424");
		sourceSystemValues.add(null);
		sourceSystemValues.add(null);

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Snowball", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1424");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add("ignore");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Snowball", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1424");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add("");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Snowball", result);
		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1424");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add(null);

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("Snowball", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("singleValue");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add("");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("singleValueResult", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("singleValue");
		sourceSystemValues.add("");
		sourceSystemValues.add("");
		sourceSystemValues.add("");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("singleValueResult", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("singleValue");
		sourceSystemValues.add("blah1");
		sourceSystemValues.add("blah2");
		sourceSystemValues.add("blah3");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("singleValueResult", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("");
		sourceSystemValues.add("1424");
		sourceSystemValues.add("");
		sourceSystemValues.add("");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals(null, result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("");
		sourceSystemValues.add("CMS Spread");
		sourceSystemValues.add("");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals(null, result);
		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("1208");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add("ignore");
		sourceSystemValues.add("ignore");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals(null, result);
		
		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("");
		sourceSystemValues.add("");
		sourceSystemValues.add("CMS Spread");
		sourceSystemValues.add("");

		result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals(null, result);

	}
	
	@Test(expected = RuntimeException.class)
	@DirtiesContext
	public void test_exception_null_source_value()
			throws MappingConfigurationServiceException
	{
		String value = null;
		this.xaMappingService
					.getTargetConfigurationValue("CMI2",
							"Product Type to Tradebook Mapping", "Tradeweb", "Bloomberg",
							value);
	}
	
	@Test(expected = RuntimeException.class)
	@DirtiesContext
	public void test_exception_null_source_values()
			throws MappingConfigurationServiceException
	{
		List<String> values = null;
		this.xaMappingService
					.getTargetConfigurationValue("CMI2",
							"Product Type to Tradebook Mapping", "Tradeweb", "Bloomberg",
							values);
	}
	
	@Test(expected = RuntimeException.class)
	@DirtiesContext
	public void test_exception_empty_source_values()
			throws MappingConfigurationServiceException
	{
		List<String> values = new ArrayList<String>();
		this.xaMappingService
					.getTargetConfigurationValue("CMI2",
							"Product Type to Tradebook Mapping", "Tradeweb", "Bloomberg",
							values);
	}
	
	@Test(expected = RuntimeException.class)
	@DirtiesContext
	public void test_exception_reverse_mapping_with_multiple_source_values()
			throws MappingConfigurationServiceException
	{
		MappingConfigurationServiceConfiguration config = new MappingConfigurationServiceConfiguration();
		config.setReverseMapping(true);
		this.xaMappingService.setConfiguration(config);
		
		List<String> values = new ArrayList<String>();
		values.add("value1");
		values.add("value2");
		this.xaMappingService
					.getTargetConfigurationValue("CMI2",
							"Product Type to Tradebook Mapping", "Tradeweb", "Bloomberg",
							values);
	}
	
	
	@Test
	@DirtiesContext
	public void test_reverse_mapping()
	{
		MappingConfigurationServiceConfiguration config = new MappingConfigurationServiceConfiguration();
		config.setReverseMapping(true);
		this.xaMappingService.setConfiguration(config);
		
		String result = this.xaMappingService
				.getTargetConfigurationValue("CMI2",
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

		String result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("BNPPAR", result);
	}

	@Test
	@DirtiesContext
	public void test_success_4_paramater_mapping_with_ignores_1_params_with_cardinality()
	{
		List<QueryParameter> sourceSystemValues = new ArrayList<QueryParameter>();
		sourceSystemValues.add(this.createQueryParameter("name1","Myself"));
		sourceSystemValues.add(this.createQueryParameter("name2","blah3"));
		sourceSystemValues.add(this.createQueryParameter("name3","blah"));
		sourceSystemValues.add(this.createQueryParameter("name4","blah1"));

		String result = this.xaMappingService
				.getTargetConfigurationValueWithIgnoresWithOrdinality("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("BNPPAR", result);
	}

	@Test
	@DirtiesContext
	public void test_success_4_paramater_mapping_with_ignores_1_params_with_cardinalityBad_name()
	{
		List<QueryParameter> sourceSystemValues = new ArrayList<QueryParameter>();
		sourceSystemValues.add(this.createQueryParameter("bad name","Myself"));
		sourceSystemValues.add(this.createQueryParameter("name2","blah3"));
		sourceSystemValues.add(this.createQueryParameter("name3","blah"));
		sourceSystemValues.add(this.createQueryParameter("name4","blah1"));

		String result = this.xaMappingService
				.getTargetConfigurationValueWithIgnoresWithOrdinality("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals(null, result);
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

		String result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("ZEKRAA", result);
	}
	
	@DirtiesContext
	@Test
	public void test_success_4_paramater_mapping_with_ignores()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("On My Own");
		sourceSystemValues.add("ignore1");
		sourceSystemValues.add("ignore2");
		sourceSystemValues.add("ignore3");

		String result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("VIDAUISA", result);
	}

	@DirtiesContext
	@Test
	public void test_success_4_paramater_mapping_with_ignores_with_ordinality()
	{
		List<QueryParameter> sourceSystemValues = new ArrayList<QueryParameter>();
		sourceSystemValues.add(createQueryParameter("name1", "On My Own"));
		sourceSystemValues.add(createQueryParameter("name2", "ignore1"));
		sourceSystemValues.add(createQueryParameter("name3", "ignore2"));
		sourceSystemValues.add(createQueryParameter("name4", "ignore3"));

		String result = this.xaMappingService
				.getTargetConfigurationValueWithIgnoresWithOrdinality("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("VIDAUISA", result);
	}

	@DirtiesContext
	@Test
	public void test_success_4_paramater_mapping_with_ignores_with_ordinality_bad_name()
	{
		List<QueryParameter> sourceSystemValues = new ArrayList<QueryParameter>();
		sourceSystemValues.add(createQueryParameter("bad name", "On My Own"));
		sourceSystemValues.add(createQueryParameter("name2", "ignore1"));
		sourceSystemValues.add(createQueryParameter("name3", "ignore2"));
		sourceSystemValues.add(createQueryParameter("name4", "ignore3"));

		String result = this.xaMappingService
				.getTargetConfigurationValueWithIgnoresWithOrdinality("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals(null, result);
	}

	private QueryParameter createQueryParameter(String name, String value)
	{
		QueryParameterImpl param = new QueryParameterImpl(name, value);

		return param;
	}

	@DirtiesContext
	@Test
	public void test_success_4_paramater_mapping_with_ignores2()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("On My Own");
		sourceSystemValues.add("Value2");
		sourceSystemValues.add("ignore2");
		sourceSystemValues.add("ignore3");

		String result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg",
						sourceSystemValues);

		Assert.assertEquals("ZEKRAA", result);
	}

	@Test(expected = IllegalArgumentException.class)
	@DirtiesContext
	public void test_null_dao()
	{
		new MappingManagementServiceImpl(null);
	}


	@Test
	@DirtiesContext
	public void test_success_no_results()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("no_results");

		String result = this.xaMappingService
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

		MappingConfiguration result = this.xaMappingManagementService
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

		MappingConfiguration result = this.xaMappingManagementService
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

		String result = this.xaMappingService
				.getTargetConfigurationValue("CMI2",
						"Salesperson to Salesperson Mapping", "Tradeweb",
						"Bloomberg", sourceSystemValues);

		Assert.assertEquals("BEN", result);
	}

	@Test
	@DirtiesContext
	public void test_success_1_paramater_mapping_not_passing_list_for_source_system_values()
	{
		String result = this.xaMappingService
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

		String result = this.xaMappingService
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

		String result = this.xaMappingService
				.getTargetConfigurationValueWithIgnores("CMI2",
						"Ignore Mapping", "Tradeweb", "Bloomberg", sourceSystemValues);
	}

	@Test
	@DirtiesContext
	public void test_success_2_paramater_mapping_2()
	{
		List<String> sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("true");

		String result = this.xaMappingService
				.getTargetConfigurationValue("CMI2",
						"Product Type to Tradebook Mapping", "Tradeweb",
						"Bloomberg", sourceSystemValues);

		Assert.assertEquals("YENTBFB", result);

		sourceSystemValues = new ArrayList<String>();
		sourceSystemValues.add("false");

		result = this.xaMappingService
				.getTargetConfigurationValue("CMI2",
						"Product Type to Tradebook Mapping", "Tradeweb",
						"Bloomberg", sourceSystemValues);

		Assert.assertEquals("YENGOVT", result);
	}

	@Test
	@DirtiesContext
	public void test_get_all_configuration_context_success()
	{
		List<ConfigurationContext> result = this.xaMappingManagementService
				.getAllConfigurationContexts();

		Assert.assertEquals(4, result.size());
	}

	@Test
	@DirtiesContext
	public void test_get_all_configuration_types_success()
	{
		List<ConfigurationType> result = this.xaMappingManagementService
				.getAllConfigurationTypes();

		Assert.assertEquals(5, result.size());
	}

	@Test
	@DirtiesContext
	public void test_get_all_configuration_service_clients_success()
	{
		List<ConfigurationServiceClient> result = this.xaMappingManagementService
				.getAllConfigurationServiceClients();

		Assert.assertEquals(2, result.size());
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
				if(sourceConfigurationValue.getTargetConfigurationValue() != null)
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
			MappingConfiguration mappingConfigurationSearched = this.xaMappingManagementService
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
			List<MappingConfiguration> mappingConfigurationsSearched = this.xaMappingManagementService
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
			List<MappingConfiguration> mappingConfigurationsSearched = this.xaMappingManagementService
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
			List<MappingConfiguration> mappingConfigurationsSearched = this.xaMappingManagementService
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
			List<MappingConfiguration> mappingConfigurationsSearched = this.xaMappingManagementService
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
			ConfigurationContext configurationContextSearched = this.xaMappingManagementService
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
			ConfigurationServiceClient configurationServiceClientSearched = this.xaMappingManagementService
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
			ConfigurationType configurationTypeSearched = this.xaMappingManagementService
					.getConfigurationTypeById(configurationType.getId());

			Assert.assertEquals(configurationType, configurationTypeSearched);
		}
	}

	@Test
	@DirtiesContext
	public void test_get_configuration_types_by_client_name()
			throws MappingConfigurationServiceException
	{
		List<ConfigurationType> configurationTypes = this.xaMappingManagementService
				.getConfigurationTypesByClientName("CMI2");

		Assert.assertNotNull(configurationTypes);
	}

	@Test
	@DirtiesContext
	public void test_get_source_configuration_contexts_by_client_name_and_type()
			throws MappingConfigurationServiceException
	{
		List<ConfigurationContext> configurationContexts = this.xaMappingManagementService
				.getSourceConfigurationContextsByClientNameAndType("CMI2",
						"Salesperson to Salesperson Mapping");

		Assert.assertNotNull(configurationContexts);

		configurationContexts = this.xaMappingManagementService
				.getSourceConfigurationContextsByClientNameAndType("CMI2", null);

		Assert.assertNotNull(configurationContexts);

		configurationContexts = this.xaMappingManagementService
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
		List<ConfigurationContext> configurationContexts = this.xaMappingManagementService
				.getTargetConfigurationContextByClientNameTypeAndSourceContext(
						"CMI2", "Salesperson to Salesperson Mapping",
						"Tradeweb");

		Assert.assertNotNull(configurationContexts);

		configurationContexts = this.xaMappingManagementService
				.getTargetConfigurationContextByClientNameTypeAndSourceContext(
						"CMI2", "Salesperson to Salesperson Mapping", null);

		Assert.assertNotNull(configurationContexts);

		configurationContexts = this.xaMappingManagementService
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
			List<ParameterName> keyLocationQueries = this.xaMappingManagementService
					.getParameterNamesByMappingConfigurationId(mappingConfiguration
							.getId());

			for (ParameterName keyLocationQuery : keyLocationQueries)
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
			List<SourceConfigurationValue> sourceConfigutationValues = this.xaMappingManagementService
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
			List<SourceConfigurationValue> sourceConfigutationValues = this.xaMappingManagementService
					.getSourceConfigurationValueByMappingConfigurationId(mappingConfiguration
							.getId());
		}
	}

	/**
	 * Helper method to add the configuration type to the database.
	 *
	 */
	private Long addConfigurationServiceClient(String name)
	{
		ConfigurationServiceClient configurationServiceClient = new ConfigurationServiceClient();
		configurationServiceClient.setName(name);

		return this.xaMappingManagementService
				.saveConfigurationServiceClient(configurationServiceClient);
	}

	/**
	 * Helper method to add the configuration type to the database.
	 */
	private Long addConfigurationType(String name)
	{
		ConfigurationType configurationType = new ConfigurationType();
		configurationType.setName(name);

		return this.xaMappingManagementService
				.saveConfigurationType(configurationType);
	}

	private Long addConfigurationContext(String name, String description)
	{
		ConfigurationContext configurationContext = new ConfigurationContext();
		configurationContext.setName(name);
		configurationContext.setDescription(description);

		return this.xaMappingManagementService
				.saveConfigurationConext(configurationContext);
	}

	/**
	 * Helper method to add a configuration context to the database.
	 *
	 */
	private Long addMappingConfiguration(Long sourceContextId,
										 Long targetContextId, int numberOfParams,
										 Long configurationTypeId, Long configurationServiceClientId,
										 String description, List<String> keyLocationQueries)
	{

		return this.xaMappingManagementService.addMappingConfiguration(
				sourceContextId, targetContextId, numberOfParams,
				configurationTypeId, configurationServiceClientId,
				keyLocationQueries, description);
	}

	/**
	 * Helper method to add a source system value to the database.
	 *
	 * @param sourceSystemValue
	 * @param name
	 * @param mappingConfigurationId
	 * @param targetConfigurationValue
     * @return
     */
	private Long addSourceSystemConfiguration(String sourceSystemValue, String name,
			Long mappingConfigurationId,
			TargetConfigurationValue targetConfigurationValue)
	{
		SourceConfigurationValue sourceConfigurationValue = new SourceConfigurationValue();
		sourceConfigurationValue
				.setMappingConfigurationId(mappingConfigurationId);
		sourceConfigurationValue.setName(name);
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

	private void addManyToManySourceSystemConfiguration(List<String> sourceSystemValues, List<String> targetSystemValues, Long mappingConfigurationId, Long groupingId)
	{
		int i=1;
		for(String sourceValue: sourceSystemValues)
		{
			SourceConfigurationValue value = new SourceConfigurationValue();

			value.setMappingConfigurationId(mappingConfigurationId);
			value.setSourceSystemValue(sourceValue);
			value.setSourceConfigGroupId(groupingId);
			value.setName("name"+i);

			this.xaMappingConfigurationDao.storeSourceConfigurationValue(value);

			i++;
		}

		for(String targetValue: targetSystemValues)
		{
			ManyToManyTargetConfigurationValue value = new ManyToManyTargetConfigurationValue();
			value.setTargetSystemValue(targetValue);
			value.setGroupId(groupingId);

			this.xaMappingConfigurationDao.storeManyToManyTargetConfigurationValue(value);
		}
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	private ConfigurationType addConfigurationType2(String name)
	{
		ConfigurationType configurationType = new ConfigurationType();
		configurationType.setName(name);

		this.xaMappingConfigurationDao.storeConfigurationType(configurationType);

		return configurationType;
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	private ConfigurationServiceClient addConfigurationServiceClient2(String name)
	{
		ConfigurationServiceClient configurationServiceClient = new ConfigurationServiceClient();
		configurationServiceClient.setName(name);

		this.xaMappingConfigurationDao.storeConfigurationServiceClient(configurationServiceClient);

		return configurationServiceClient;
	}

	/**
	 *
	 * @param name
	 * @param description
	 * @return
	 */
	private ConfigurationContext addConfigurationContext2(String name, String description)
	{
		ConfigurationContext configurationContext = new ConfigurationContext();
		configurationContext.setName(name);
		configurationContext.setDescription(description);

		this.xaMappingConfigurationDao.storeConfigurationContext(configurationContext);

		return configurationContext;
	}

	/**
	 *
	 * @param sourceContext
	 * @param targetContext
	 * @param numberOfParams
	 * @param configurationType
	 * @param configurationServiceClient
	 * @param description
	 * @return
	 */
	private Long addMappingConfiguration(ConfigurationContext sourceContext, ConfigurationContext targetContext, int	 numberOfParams, ConfigurationType configurationType,
										 ConfigurationServiceClient configurationServiceClient, String description)
	{
		MappingConfiguration configurationContext = new MappingConfiguration();
		configurationContext.setConfigurationType(configurationType);
		configurationContext.setNumberOfParams(numberOfParams);
		configurationContext.setSourceContext(sourceContext);
		configurationContext.setTargetContext(targetContext);
		configurationContext.setConfigurationServiceClient(configurationServiceClient);
		configurationContext.setDescription(description);

		return this.xaMappingConfigurationDao.storeMappingConfiguration(configurationContext);
	}

	/**
	 * Helper method to add a source system value to the database.
	 *
	 * @param sourceSystemValue
	 * @param mappingConfigurationId
	 * @param targetConfigurationValue
	 * @return
	 */
	private Long addSourceSystemConfiguration2(String sourceSystemValue, Long mappingConfigurationId, TargetConfigurationValue targetConfigurationValue)
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
	private TargetConfigurationValue addTargetSystemConfiguration2(String targetSystemValue)
	{
		TargetConfigurationValue targetConfigurationValue = new TargetConfigurationValue();
		targetConfigurationValue.setTargetSystemValue(targetSystemValue);

		this.xaMappingConfigurationDao.storeTargetConfigurationValue(targetConfigurationValue);

		return targetConfigurationValue;
	}


}
