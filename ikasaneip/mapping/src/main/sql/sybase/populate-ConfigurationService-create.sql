SET IDENTITY_INSERT ConfigurationType ON
insert into ConfigurationType(Id, Name) values(1, 'Dealer and Product to Account' )
SET IDENTITY_INSERT ConfigurationType OFF

SET IDENTITY_INSERT ConfigurationContext ON
insert into ConfigurationContext(Id, SourceSystem, TargetSystem, NumberOfParams, ConfigurationTypeId  ) values(1, 'Tradeweb', 'Bloomberg', 2, 1)
SET IDENTITY_INSERT ConfigurationContext OFF

SET IDENTITY_INSERT TargetConfigurationValue ON
insert into TargetConfigurationValue (Id, TargetSystemValue) values(1, 'BARCLON')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(2, 'BNPPAR')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(3, 'SAL')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(4, 'CRTAGRI')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(5, 'COMMFFT')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(6, 'CS')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(7, 'DB')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(8, 'FORTISBRUS')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(9, 'GS')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(10, 'HSBCBK')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(11, 'JPM')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(12, 'CHASELDN')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(13, 'ML')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(14, 'MS')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(15, 'NORDEABKDK')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(16, 'NOMURALON')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(17, 'RBSLON')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(18, 'SGPAR')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(19, 'SBCI')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(20, 'BMO')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(21, 'JEFFINTL')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(22, 'JEFFINTL')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(23, 'IMIMIL')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(24, 'DUMMY')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(25, 'DUMMY')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(26, 'DUMMY')
insert into TargetConfigurationValue (Id, TargetSystemValue) values(27, 'DUMMY')
SET IDENTITY_INSERT TargetConfigurationValue OFF

SET IDENTITY_INSERT SourceConfigurationValue ON
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(1, 'BARX',  1, 1)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(2, 'TRSY',  1, 1)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(3, 'AGCY',  1, 1)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(4, 'MBS',  1, 1)

insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(5, 'BNPP',  1, 2)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(6, 'TRSY',  1, 2)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(7, 'AGCY',  1, 2)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(8, 'MBS',  1, 2)

insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(9, 'C',  1, 3)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(10, 'TRSY',  1, 3)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(11, 'AGCY',  1, 3)
insert into SourceConfigurationValue (Id, SourceSystemValue, ConfigurationContextId, TargetConfigurationValueId) values(12, 'MBS',  1, 3)
SET IDENTITY_INSERT SourceConfigurationValue OFF

commit