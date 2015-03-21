-----------------------------------------------------------------------------------------------------------------------
-- First thing we want to do is to set up the Client, in our case CMI2 and associate the query processor with the client.
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSConfigServiceClient ON
insert into MCSConfigServiceClient(Id, Name, KeyLocationQueryProcessorType) values(1, 'CMI2', 'com.mizuho.cmi2.mappingConfiguration.keyQueryProcessor.impl.XPathKeyLocationQueryProcessor')
SET IDENTITY_INSERT MCSConfigServiceClient OFF

-----------------------------------------------------------------------------------------------------------------------
-- Now go ahead and set up the configuration contexts for the new tradeweb-jgbPostTrade module. We need two contexts
-- for this module as it books two tickets via the mgiCmfTgt module and we want the mapping behaviour to be different
-- for each ticket type.
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSConfigurationContext ON
insert into MCSConfigurationContext(Id, Name, Description) values(1, 'tradeweb-jgbPostTradeSrc_electronicTradingTicket', 'Module that receives tradeweb JGB post trade messages.' )
insert into MCSConfigurationContext(Id, Name, Description) values(2, 'blbgToms-mhiCmfTgt', 'Module that send trade bookings to Bloomberg LDN.' )
insert into MCSConfigurationContext(Id, Name, Description) values(3, 'tradeweb-jgbPostTradeSrc_traderTicket', 'Module that receives tradeweb JGB post trade messages.' )
insert into MCSConfigurationContext(Id, Name, Description) values(4, 'r2r-msusa2Mhi-electronicTrade', 'MSUSA R2R Electronic Module.' )
insert into MCSConfigurationContext(Id, Name, Description) values(5, 'r2r-msusa2Mhi-voiceTrade', 'MSUSA R2R Voice Module.' )
insert into MCSConfigurationContext(Id, Name, Description) values(6, 'r2r-mhsa2Mhi-electronicTrade', 'MHSA R2R Electronic Module.' )
insert into MCSConfigurationContext(Id, Name, Description) values(7, 'r2r-mhsa2Mhi-voiceTrade', 'MHSA R2R Voice Module.' )
insert into MCSConfigurationContext(Id, Name, Description) values(8, 'r2r-fi2Mtn-voiceTrade', 'Fixed Income to MTN R2R Voice Trade Module.' )

SET IDENTITY_INSERT MCSConfigurationContext OFF

-----------------------------------------------------------------------------------------------------------------------
-- Set up the configuration type for the Salesperson to Salesperson mapping
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSConfigurationType ON
insert into MCSConfigurationType(Id, Name) values(1, 'Salesperson to Salesperson' )
SET IDENTITY_INSERT MCSConfigurationType OFF

-----------------------------------------------------------------------------------------------------------------------
-- Now go ahead and set up the mapping configuration and associated key location queries for the 
-- tradeweb-jgbPostTradeSrc_electronicTradingTicket context.
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) values(1,'This mapping manages the salesperson to sales person mapping required between sales people managed in Tradeweb and those in Bloomberg.', 1, 2, 1, 1, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(1,'/cmfTrade/tradeDetails/salesmanId', 1)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

-----------------------------------------------------------------------------------------------------------------------
-- Set up the target values. These can be shared by the tradeweb-jgbPostTradeSrc_electronicTradingTicket and
-- tradeweb-jgbPostTradeSrc_electronicTraderTicket contexts.
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(1, 'ZEKRAA')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(2, 'VIDAUISA')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(3, 'BEN')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(4, 'IMONDIV')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(5, 'RAJUMAHB')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(6, 'CREMAD')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(7, 'AYERSRI')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(8, 'PARKERD')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(9, 'WILTGR')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(10, 'SCOTTM')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(11, 'MACCHRIS')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(12, 'DEYERMOJ')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(13, 'ARAUJOMA')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(14, 'BAD_SALESPERSON')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(15, 'ENDORINA')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(16, 'PIDCOCKJ')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(17, 'GRUNDYD')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(18, 'KOULTO')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(19, 'SAVORNIN')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(20, 'BEVERLEY')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(21, 'HARZSO')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(22, 'CAPPCH')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(23, 'KENNEDY')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(24, 'HK10')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(25, 'ROCA')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(26, 'HAUGEC')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(27, 'ANSONG')
SET IDENTITY_INSERT MCSTargetConfigValue OFF

-----------------------------------------------------------------------------------------------------------------------
-- And finally the source values for the tradeweb-jgbPostTradeSrc_electronicTradingTicket context.
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(1, 'azehra',  1, 1, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(2, 'isabelv',  1, 2, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(3, 'briordan2',  1, 3, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(4, 'vimondi',  1, 4, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(5, 'rmahboob',  1, 5, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(6, 'dcrema',  1, 6, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(7, 'rayers',  1, 7, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(8, 'dparker2',  1, 8, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(9, 'gwiltshire',  1, 9, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(10, 'mscott5',  1, 10, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(11, 'cmacmillan',  1, 11, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(12, 'jdyermo',  1, 12, NULL)

insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(13, 'maraujo',  1, 13, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(14, 'kimdu',  1, 14, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(15, 'rendo',  1, 15, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(16, 'jpidcock',  1, 16, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(17, 'dgrundy3',  1, 17, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(18, 'tombany',  1, 18, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(19, 'csavornin1',  1, 19, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(20, 'sbeverley1',  1, 20, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(21, 'sharzo2',  1, 21, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(22, 'chrisc',  1, 22, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(23, 'rkennedy',  1, 23, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(24, 'jakim',  1, 24, NULL)

insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(25, 'paolaroca',  1, 25, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(26, 'chauge2',  1, 26, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(27, 'ganson4',  1, 27, NULL)

insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(200, 'azehra',  1, 1, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(201, 'isabelv',  1, 2, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(202, 'briordan2',  1, 3, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(203, 'vimondi',  1, 4, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(204, 'rmahboob',  1, 5, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(205, 'dcrema',  1, 6, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(206, 'rayers',  1, 7, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(207, 'dparker2',  1, 8, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(208, 'gwiltshire',  1, 9, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(209, 'mscott5',  1, 10, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(210, 'cmacmillan',  1, 11, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(211, 'jdyermo',  1, 12, NULL)

insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(212, 'maraujo',  1, 13, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(213, 'kimdu',  1, 14, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(214, 'rendo',  1, 15, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(215, 'jpidcock',  1, 16, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(216, 'dgrundy3',  1, 17, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(217, 'tombany',  1, 18, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(218, 'csavornin1',  1, 19, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(219, 'sbeverley1',  1, 20, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(220, 'sharzo2',  1, 21, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(221, 'chrisc',  1, 22, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(222, 'rkennedy',  1, 23, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(223, 'jakim',  1, 24, NULL)

insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(224, 'paolaroca',  1, 25, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(225, 'chauge2',  1, 26, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(226, 'ganson4',  1, 27, NULL)

insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(227, 'azehra',  1, 1, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(228, 'isabelv',  1, 2, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(229, 'briordan2',  1, 3, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(230, 'vimondi',  1, 4, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(231, 'rmahboob',  1, 5, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(232, 'dcrema',  1, 6, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(233, 'rayers',  1, 7, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(234, 'dparker2',  1, 8, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(235, 'gwiltshire',  1, 9, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(236, 'mscott5',  1, 10, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(237, 'cmacmillan',  1, 11, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(238, 'jdyermo',  1, 12, NULL)

insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(239, 'maraujo',  1, 13, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(240, 'kimdu',  1, 14, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(241, 'rendo',  1, 15, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(242, 'jpidcock',  1, 16, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(243, 'dgrundy3',  1, 17, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(244, 'tombany',  1, 18, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(245, 'csavornin1',  1, 19, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(246, 'sbeverley1',  1, 20, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(247, 'sharzo2',  1, 21, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(248, 'chrisc',  1, 22, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(249, 'rkennedy',  1, 23, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(250, 'jakim',  1, 24, NULL)

insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(251, 'paolaroca',  1, 25, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(252, 'chauge2',  1, 26, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(253, 'ganson4',  1, 27, NULL)

SET IDENTITY_INSERT MCSSourceConfigValue OFF

-----------------------------------------------------------------------------------------------------------------------
-- Set up the configuration type for the Counterparty mapping
-----------------------------------------------------------------------------------------------------------------------

SET IDENTITY_INSERT MCSConfigurationType ON
insert into MCSConfigurationType(Id, Name) values(2, 'Counterparty Mapping' )
SET IDENTITY_INSERT MCSConfigurationType OFF

-----------------------------------------------------------------------------------------------------------------------
-- Now go ahead and set up the mapping configuration and associated key location queries for the 
-- tradeweb-jgbPostTradeSrc_electronicTradingTicket context.
-----------------------------------------------------------------------------------------------------------------------

SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) values(4,'This mapping manages the counterparty mapping required between Tradeweb and Bloomberg TOMS.', 3, 2, 1, 2, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(4,'''ANY''', 4)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF


SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(28, 'IBJSTOK')
SET IDENTITY_INSERT MCSTargetConfigValue OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(56, 'ANY',  4, 28, NULL)
SET IDENTITY_INSERT MCSSourceConfigValue OFF


-----------------------------------------------------------------------------------------------------------------------
-- Set up the configuration type for the Tradebook Mapping
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSConfigurationType ON
insert into MCSConfigurationType(Id, Name) values(3, 'Tradebook Mapping' )
SET IDENTITY_INSERT MCSConfigurationType OFF


SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) values(5,'This mapping manages the product to trade book mapping required between Tradeweb and Bloomberg TOMS.', 1, 2, 1, 3, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(5,'contains(/cmfTrade/additionalData[@NAME=''tradewebSecurityType'']/text(), ''NOTE'')', 5)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(29, 'YENGOVT')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(30, 'YENTBFB')
SET IDENTITY_INSERT MCSTargetConfigValue OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(57, 'true',  5, 29, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(58, 'false',  5, 30, NULL)
SET IDENTITY_INSERT MCSSourceConfigValue OFF

SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) values(6,'This mapping manages the product to trade book mapping required between Tradeweb and Bloomberg TOMS.', 3, 2, 1, 3, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(6,'contains(/cmfTrade/additionalData[@NAME=''tradewebSecurityType'']/text(), ''NOTE'')', 6)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(59, 'true',  6, 29, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(60, 'false',  6, 30, NULL)
SET IDENTITY_INSERT MCSSourceConfigValue OFF

-----------------------------------------------------------------------------------------------------------------------
-- Set up the configuration type for the UUID Mapping
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSConfigurationType ON
insert into MCSConfigurationType(Id, Name) values(4, 'UUID Mapping' )
SET IDENTITY_INSERT MCSConfigurationType OFF


SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) 
values(7,'This configuration manages the mapping between the Tradeweb trader name and the Bloomberg TOMS UUID.', 1, 2, 1, 4, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(7, '/cmfTrade/tradeDetails/traderName', 7)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(31, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(32, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(33, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(34, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(35, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(36, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(37, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(38, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(39, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(40, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(41, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(42, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(43, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(48, '565666')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(100, '565666')


SET IDENTITY_INSERT MCSTargetConfigValue OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(66, 'kmotokawa',  7, 31, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(67, 'hkanazawa',  7, 32, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(61, 'yakao',  7, 33, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(62, 'hkawai',  7, 34, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(63, 'misato',  7, 35, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(64, 'stakenaga',  7, 36, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(65, 'ykoita',  7, 37, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(75, 'hkonaka',  7, 38, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(77, 'rkojima',  7, 48, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(79, 'kshimao',  7, 39, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(81, 'sokamoto2',  7, 40, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(82, 'jsuzuki',  7, 41, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(83, 'ymurai',  7, 42, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(84, 'twatari',  7, 43, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(110, 'mstewartd',  7, 100, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(112, 'lyseri-Auto',  7, 100, NULL)


SET IDENTITY_INSERT MCSSourceConfigValue OFF

SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) values(8,'This configuration manages the mapping between the Tradeweb trader name and the Bloomberg TOMS UUID.', 3, 2, 1, 4, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(8,'/cmfTrade/tradeDetails/traderName', 8)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(73, 'kmotokawa',  8, 31, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(74, 'hkanazawa',  8, 32, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(68, 'yakao',  8, 33, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(69, 'hkawai',  8, 34, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(70, 'misato',  8, 35, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(71, 'stakenaga',  8, 36, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(72, 'ykoita',  8, 37, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(76, 'hkonaka',  8, 38, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(78, 'rkojima',  8, 38, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(80, 'kshimao',  8, 39, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(85, 'sokamoto2',  8, 40, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(86, 'jsuzuki',  8, 41, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(87, 'ymurai',  8, 42, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(88, 'twatari',  8, 43, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(111, 'mstewartd',  8, 100, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(113, 'lyseri-Auto',  8, 100, NULL)

SET IDENTITY_INSERT MCSSourceConfigValue OFF

-----------------------------------------------------------------------------------------------------------------------
-- Set up the configuration type for the R2R Context to UUID Mapping
-----------------------------------------------------------------------------------------------------------------------

-----------------------------------------------------------------------------------------------------------------------
-- msusa2Mhi-electronicTrade
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSConfigurationType ON
insert into MCSConfigurationType(Id, Name) values(5, 'R2R Context to UUID Mapping' )
SET IDENTITY_INSERT MCSConfigurationType OFF

SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) 
values(12,'This configuration manages the mapping between the R2R context and the Bloomberg TOMS UUID for MSUSA to MHI Electronic Trades.', 4, 2, 1, 5, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(12, '/cmfTrade/additionalData[@NAME=''context'']', 12)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(44, '0')


SET IDENTITY_INSERT MCSTargetConfigValue OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(89, 'E1',  12, 44, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(90, 'E2',  12, 44, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(91, 'E3',  12, 44, NULL)
SET IDENTITY_INSERT MCSSourceConfigValue OFF

-----------------------------------------------------------------------------------------------------------------------
-- msusa2Mhi-voiceTrade
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) 
values(9,'This configuration manages the mapping between the R2R context and the Bloomberg TOMS UUID for MSUSA to MHI Voice Trades.', 5, 2, 1, 5, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(9, '/cmfTrade/additionalData[@NAME=''context'']', 9)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(45, '0')


SET IDENTITY_INSERT MCSTargetConfigValue OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(92, 'V1',  9, 45, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(93, 'V2',  9, 45, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(94, 'V3',  9, 45, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(95, 'V4',  9, 45, NULL)
SET IDENTITY_INSERT MCSSourceConfigValue OFF

-----------------------------------------------------------------------------------------------------------------------
-- mhsa2Mhi-electronicTrade
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) 
values(10,'This configuration manages the mapping between the R2R context and the Bloomberg TOMS UUID for MHSA to MHI Electronic Trades.', 6, 2, 1, 5, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(10, '/cmfTrade/additionalData[@NAME=''context'']', 10)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(46, '0')


SET IDENTITY_INSERT MCSTargetConfigValue OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(96, 'E1',  10, 46, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(97, 'E2',  10, 46, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(98, 'E3',  10, 46, NULL)
SET IDENTITY_INSERT MCSSourceConfigValue OFF

-----------------------------------------------------------------------------------------------------------------------
-- mhsa2Mhi-voiceTrade
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) 
values(11,'This configuration manages the mapping between the R2R context and the Bloomberg TOMS UUID for MHSA to MHI Voice Trades.', 7, 2, 1, 5, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(11, '/cmfTrade/additionalData[@NAME=''context'']', 11)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(47, '0')


SET IDENTITY_INSERT MCSTargetConfigValue OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(99, 'V1',  11, 47, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(100, 'V2',  11, 47, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(101, 'V3',  11, 47, NULL)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(102, 'V4',  11, 47, NULL)
SET IDENTITY_INSERT MCSSourceConfigValue OFF


----------------------------------------------------------------------------------------------------------------------
-- Many to one mapping
-----------------------------------------------------------------------------------------------------------------------
SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) 
values(14,'Many to one test data.', 7, 2, 2, 2, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(14, '/cmfTrade/additionalData[@NAME=''context'']', 14)
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(13, '/cmfTrade/additionalData[@NAME=''context'']', 14)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(49, '0')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(50, '1')


SET IDENTITY_INSERT MCSTargetConfigValue OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(103, 'V1',  14, 49, 1)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(104, 'V2',  14, 49, 1)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(105, 'V3',  14, 50, 2)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(106, 'V4',  14, 50, 2)
SET IDENTITY_INSERT MCSSourceConfigValue OFF


SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) 
values(15,'Many to one test data.', 7, 2, 4, 3, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(15, '/cmfTrade/additionalData[@NAME=''context'']', 15)
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(16, '/cmfTrade/additionalData[@NAME=''context'']', 15)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(51, '0')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(52, '1')


SET IDENTITY_INSERT MCSTargetConfigValue OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(307, 'V1',  15, 51, 1)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(308, 'V2',  15, 51, 1)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(309, 'V3',  15, 51, 1)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(310, 'V4',  15, 51, 1)

insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(311, 'V11',  15, 52, 2)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(312, 'V22',  15, 52, 2)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(313, 'V32',  15, 52, 2)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId, SourceConfigGroupId) values(314, 'V42',  15, 52, 2)
SET IDENTITY_INSERT MCSSourceConfigValue OFF


commit