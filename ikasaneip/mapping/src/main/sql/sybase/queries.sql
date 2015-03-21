select distinct targetconf2_.TargetSystemValue as col_0_0_ 
from MCSConfigurationType configurat0_, MCSMappingConfiguration mappingcon1_, MCSTargetConfigValue targetconf2_, MCSSourceConfigValue sourceconf3_ 
where configurat0_.Name='Counterparty Mapping'
and configurat0_.Id=mappingcon1_.ConfigurationTypeId 
and mappingcon1_.SourceContextId=(select configurat4_.Id from MCSConfigurationContext configurat4_ where configurat4_.Name='tradeweb-jgbPostTradeSrc') 
and mappingcon1_.TargetContextId=(select configurat5_.Id from MCSConfigurationContext configurat5_ where configurat5_.Name='blbgToms-mhiCmfTgt') 
and mappingcon1_.NumberOfParams=1
and mappingcon1_.Id=sourceconf3_.MappingConfigurationId 
and targetconf2_.Id=sourceconf3_.TargetConfigurationValueId 
and (sourceconf3_.TargetConfigurationValueId in (select sourceconf6_.TargetConfigurationValueId from MCSSourceConfigValue sourceconf6_ where sourceconf6_.SourceSystemValue='ANY')) 


select distinct targetconf2_.TargetSystemValue as col_0_0_ 
from MCSConfigurationType configurat0_, MCSMappingConfiguration mappingcon1_, MCSTargetConfigValue targetconf2_, MCSSourceConfigValue sourceconf3_ 
where configurat0_.Name='Salesperson to Salesperson'
and configurat0_.Id=mappingcon1_.ConfigurationTypeId 
and mappingcon1_.SourceContextId=(select configurat4_.Id from MCSConfigurationContext configurat4_ where configurat4_.Name='tradeweb-jgbPostTradeSrc_electronicTradingTicket') 
and mappingcon1_.TargetContextId=(select configurat5_.Id from MCSConfigurationContext configurat5_ where configurat5_.Name='blbgToms-mhiCmfTgt') 
and mappingcon1_.NumberOfParams=1
and mappingcon1_.Id=sourceconf3_.MappingConfigurationId 
and targetconf2_.Id=sourceconf3_.TargetConfigurationValueId 
and (sourceconf3_.TargetConfigurationValueId in (select sourceconf6_.TargetConfigurationValueId from MCSSourceConfigValue sourceconf6_ where sourceconf6_.SourceSystemValue='rendo')) 


select distinct targetconf2_.TargetSystemValue as col_0_0_ 
from MCSConfigurationType configurat0_, MCSMappingConfiguration mappingcon1_, MCSTargetConfigValue targetconf2_, MCSSourceConfigValue sourceconf3_ 
where configurat0_.Name='Tradebook Mapping'
and configurat0_.Id=mappingcon1_.ConfigurationTypeId 
and mappingcon1_.SourceContextId=(select configurat4_.Id from MCSConfigurationContext configurat4_ where configurat4_.Name='tradeweb-jgbPostTradeSrc_electronicTradingTicket') 
and mappingcon1_.TargetContextId=(select configurat5_.Id from MCSConfigurationContext configurat5_ where configurat5_.Name='blbgToms-mhiCmfTgt') 
and mappingcon1_.NumberOfParams=1
and mappingcon1_.Id=sourceconf3_.MappingConfigurationId 
and targetconf2_.Id=sourceconf3_.TargetConfigurationValueId 
and (sourceconf3_.TargetConfigurationValueId in (select sourceconf6_.TargetConfigurationValueId from MCSSourceConfigValue sourceconf6_ where sourceconf6_.SourceSystemValue='true')) 