SET IDENTITY_INSERT MCSConfigurationType ON
insert into MCSConfigurationType(Id, Name) values(2, 'Tradebook Mapping' )
SET IDENTITY_INSERT MCSConfigurationType OFF


SET IDENTITY_INSERT MCSMappingConfiguration ON
insert into MCSMappingConfiguration(Id, Description, SourceContextId, TargetContextId, NumberOfParams, ConfigurationTypeId, ConfigurationServiceClientId ) values(2,'This mapping manages the product to trade book mapping required between Tradeweb and Bloomberg TOMS.', 1, 2, 1, 2, 1)
SET IDENTITY_INSERT MCSMappingConfiguration OFF

SET IDENTITY_INSERT MCSKeyLocationQuery ON
insert into MCSKeyLocationQuery(Id, Value, MappingConfigurationId) values(2,'contains(/cmfTrade/additionalData[@NAME=''longNote5'']/text(), ''NOTE'')', 2)
SET IDENTITY_INSERT MCSKeyLocationQuery OFF

SET IDENTITY_INSERT MCSTargetConfigValue ON
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(28, 'YENGOVT')
insert into MCSTargetConfigValue (Id, TargetSystemValue) values(29, 'YENTBFB')
SET IDENTITY_INSERT MCSTargetConfigValue OFF

SET IDENTITY_INSERT MCSSourceConfigValue ON
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(28, 'true',  2, 28)
insert into MCSSourceConfigValue (Id, SourceSystemValue, MappingConfigurationId, TargetConfigurationValueId) values(29, 'false',  2, 29)
SET IDENTITY_INSERT MCSSourceConfigValue OFF

commit