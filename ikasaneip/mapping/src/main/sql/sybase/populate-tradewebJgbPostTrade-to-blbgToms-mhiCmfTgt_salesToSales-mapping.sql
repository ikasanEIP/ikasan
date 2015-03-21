SET IDENTITY_INSERT MCSConfigServiceClient ON
insert into MCSConfigServiceClient(Id, Name, KeyLocationQueryProcessorType) values(1, 'CMI2', 'com.mizuho.cmi2.mappingConfiguration.keyQueryProcessor.impl.XPathKeyLocationQueryProcessor')
SET IDENTITY_INSERT MCSConfigServiceClient OFF

SET IDENTITY_INSERT MCSConfigurationType ON
insert into MCSConfigurationType(Id, Name) values(1, 'Salesperson to Salesperson' )
SET IDENTITY_INSERT MCSConfigurationType OFF

SET IDENTITY_INSERT MCSConfigurationContext ON
insert into MCSConfigurationContext(Id, Name, Description) values(1, 'tradeweb-jgbPostTradeSrc', 'Module that receives tradeweb JGB post trade messages.' )
insert into MCSConfigurationContext(Id, Name, Description) values(2, 'blbgToms-mhiCmfTgt', 'Module that send trade bookings to Bloomberg LDN.' )
SET IDENTITY_INSERT MCSConfigurationContext OFF

SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) values(1,'This mapping manages the salesperson to sales person mapping required between sales people managed in Tradeweb and those in Bloomberg.', 1, 2, 1, 1, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(1,'/cmfTrade/tradeDetails/salesmanName', 1)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

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
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(14, 'KIMDU')
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

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(1, 'azehra',  1, 1)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(2, 'isabelv',  1, 2)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(3, 'briordan2',  1, 3)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(4, 'vimondi',  1, 4)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(5, 'rmahboob',  1, 5)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(6, 'dcrema',  1, 6)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(7, 'rayers',  1, 7)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(8, 'dparker2',  1, 8)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(9, 'gwiltshire',  1, 9)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(10, 'mscott5',  1, 10)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(11, 'cmacmillan',  1, 11)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(12, 'jdyermo',  1, 12)

insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(13, 'maraujo',  1, 13)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(14, 'kimdu',  1, 14)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(15, 'rendo',  1, 15)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(16, 'jpidcock',  1, 16)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(17, 'dgrundy3',  1, 17)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(18, 'tombany',  1, 18)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(19, 'csavornin1',  1, 19)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(20, 'sbeverley1',  1, 20)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(21, 'sharzo2',  1, 21)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(22, 'chrisc',  1, 22)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(23, 'rkennedy',  1, 23)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(24, 'jakim',  1, 24)

insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(25, 'paolaroca',  1, 25)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(26, 'chauge2',  1, 26)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(27, 'ganson4',  1, 27)

SET IDENTITY_INSERT MCSSourceConfigValue OFF

commit