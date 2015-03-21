SET IDENTITY_INSERT ConfigurationType ON
insert into ConfigurationType(Id, Name) values(2, 'Salesperson to Salesperson Mapping' )
SET IDENTITY_INSERT ConfigurationType OFF

SET IDENTITY_INSERT ConfigurationContext ON
insert into ConfigurationContext(Id, SourceSystem, TargetSystem, NumberOfParams, ConfigurationTypeId  ) values(2, 'Tradeweb', 'Bloomberg', 1, 2)
SET IDENTITY_INSERT ConfigurationContext OFF

SET IDENTITY_INSERT TargetConfigurationValue ON
insert into TargetConfigurationValue (Id, TargetSystemValue) values(28, 'ZEKRAA')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(29, 'VIDAUISA')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(30, 'BEN')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(31, 'IMONDIV')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(32, 'RAJUMAHB')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(33, 'CREMAD')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(34, 'AYERSRI')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(35, 'PARKERD')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(36, 'WILTGR')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(37, 'SCOTTM')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(38, 'MACCHRIS')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(39, 'DEYERMOJ')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(40, 'ARAUJOMA')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(41, 'KIMDU')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(42, 'ENDORINA')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(43, 'PIDCOCKJ')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(44, 'GRUNDYD')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(45, 'KOULTO')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(46, 'SAVORNIN')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(47, 'BEVERLEY')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(48, 'HARZSO')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(49, 'CAPPCH')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(50, 'KENNEDY')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(51, 'HK10')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(52, 'ROCA')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(53, 'HAUGEC')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(54, 'ANSONG')
SET IDENTITY_INSERT TargetConfigurationValue OFF

SET IDENTITY_INSERT SourceConfigurationValue ON
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(13, 'azehra',  2, 28)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(14, 'isabelv',  2, 29)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(15, 'briordan2',  2, 30)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(16, 'vimondi',  2, 31)

insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(17, 'rmahboob',  2, 32)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(18, 'dcrema',  2, 33)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(19, 'rayers',  2, 34)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(20, 'dparker2',  2, 35)

insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(21, 'gwiltshire',  2, 36)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(22, 'mscott5',  2, 36)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(23, 'cmacmillan',  2, 37)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(24, 'jdyermo',  2, 38)

insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(25, 'maraujo',  2, 39)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(26, 'kimdu',  2, 40)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(27, 'rendo',  2, 41)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(28, 'jpidcock',  2, 42)

insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(29, 'dgrundy3',  2, 43)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(30, 'tombany',  2, 44)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(31, 'csavornin1',  2, 45)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(32, 'sbeverley1',  2, 46)

insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(33, 'sharzo2',  2, 47)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(34, 'chrisc',  2, 48)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(35, 'rkennedy',  2, 49)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(36, 'jakim',  2, 50

insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(37, 'paolaroca',  2, 51)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(38, 'chauge2',  2, 52)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(39, 'ganson4',  2, 53)
SET IDENTITY_INSERT SourceConfigurationValue OFF

commit