SET IDENTITY_INSERT MCSConfigurationType ON
insert into MCSConfigurationType(Id, Name) values(3, 'Counterparty Mapping' )
SET IDENTITY_INSERT MCSConfigurationType OFF


SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) values(3,'This mapping manages the counterparty mapping required between Tradeweb and Bloomberg TOMS.', 1, 2, 1, 3, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(3,'''ANY''', 2)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(30, 'IBJSTOK')
SET IDENTITY_INSERT MCSTargetConfigValue OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(30, 'ANY',  3, 30)
SET IDENTITY_INSERT MCSSourceConfigValue OFF

commit