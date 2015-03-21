
--
-- $Id: 1_ConfigurationService-create.sql 40152 2014-10-17 15:57:49Z stewmi $
-- $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/sql/sybase/1_ConfigurationService-create.sql $
--
-- ====================================================================
--
-- Copyright (c) 2000-2012 by Mizuho International plc.
-- All Rights Reserved.
--
-- ====================================================================
--
-- Author: CMI2 Development Team
--

-- Create the ConfigurationType table
CREATE TABLE MCSConfigurationType
(
   Id               	NUMERIC(18, 0) 		   IDENTITY NOT NULL,
   Name		      VARCHAR(256)		   UNIQUE NOT NULL,
   CreatedDateTime      DATETIME default getdate() NOT NULL,
   UpdatedDateTime      DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id) -- clustered index on Id
) 
LOCK DATAROWS


IF OBJECT_ID('MCSConfigurationType') IS NOT NULL 
   PRINT '<<< CREATED TABLE MCSConfigurationType >>>' 
ELSE
   PRINT '<<< FAILED CREATING TABLE MCSConfigurationType>>>' 
go

-- Permissions
go
GRANT DELETE ON MCSConfigurationType TO CMI2Adm
go
GRANT DELETE STATISTICS ON MCSConfigurationType TO CMI2Adm
go
GRANT INSERT ON MCSConfigurationType TO CMI2Adm
go
GRANT REFERENCES ON MCSConfigurationType TO CMI2Adm
go
GRANT SELECT ON MCSConfigurationType TO CMI2Adm
go
GRANT SELECT ON MCSConfigurationType TO CMI2Dev
go
GRANT SELECT ON MCSConfigurationType TO CMI2Sup
go
GRANT TRUNCATE TABLE ON MCSConfigurationType TO CMI2Adm
go
GRANT UPDATE ON MCSConfigurationType TO CMI2Adm
go
GRANT UPDATE STATISTICS ON MCSConfigurationType TO CMI2Adm
go

-- Create the Context table
CREATE TABLE MCSConfigurationContext
(
   Id               	NUMERIC(18, 0) 		   IDENTITY NOT NULL,
   Name		      VARCHAR(256)		   UNIQUE NOT NULL,
   Description	      VARCHAR(1024)		   NOT NULL,
   CreatedDateTime      DATETIME default getdate() NOT NULL,
   UpdatedDateTime      DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id) -- clustered index on Id
) 
LOCK DATAROWS


IF OBJECT_ID('MCSConfigurationContext') IS NOT NULL 
   PRINT '<<< CREATED TABLE MCSConfigurationContext >>>' 
ELSE
   PRINT '<<< FAILED CREATING TABLE MCSConfigurationContext >>>' 
go

-- Permissions
go
GRANT DELETE ON MCSConfigurationContext TO CMI2Adm
go
GRANT DELETE STATISTICS ON MCSConfigurationContext TO CMI2Adm
go
GRANT INSERT ON MCSConfigurationContext TO CMI2Adm
go
GRANT REFERENCES ON MCSConfigurationContext TO CMI2Adm
go
GRANT SELECT ON MCSConfigurationContext TO CMI2Adm
go
GRANT SELECT ON MCSConfigurationContext TO CMI2Dev
go
GRANT SELECT ON MCSConfigurationContext TO CMI2Sup
go
GRANT TRUNCATE TABLE ON MCSConfigurationContext TO CMI2Adm
go
GRANT UPDATE ON MCSConfigurationContext TO CMI2Adm
go
GRANT UPDATE STATISTICS ON MCSConfigurationContext TO CMI2Adm
go


CREATE TABLE MCSConfigServiceClient
(
   Id               				NUMERIC(18, 0) IDENTITY NOT NULL,
   Name		      			VARCHAR(256) UNIQUE NOT NULL,
   KeyLocationQueryProcessorType		VARCHAR(256) NOT NULL,
   CreatedDateTime      			DATETIME default getdate() NOT NULL,
   UpdatedDateTime      			DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id) -- clustered index on Id
) 
LOCK DATAROWS

CREATE NONCLUSTERED INDEX ConfigServiceClient_Name ON MCSConfigServiceClient(Name)

IF OBJECT_ID('MCSConfigServiceClient') IS NOT NULL 
   PRINT '<<< CREATED TABLE MCSConfigServiceClient >>>' 
ELSE
   PRINT '<<< FAILED CREATING TABLE MCSConfigServiceClient>>>' 
go

-- Permissions
go
GRANT DELETE ON MCSConfigServiceClient TO CMI2Adm
go
GRANT DELETE STATISTICS ON MCSConfigServiceClient TO CMI2Adm
go
GRANT INSERT ON MCSConfigServiceClient TO CMI2Adm
go
GRANT REFERENCES ON MCSConfigServiceClient TO CMI2Adm
go
GRANT SELECT ON MCSConfigServiceClient TO CMI2Adm
go
GRANT SELECT ON MCSConfigServiceClient TO CMI2Dev
go
GRANT SELECT ON MCSConfigServiceClient TO CMI2Sup
go
GRANT TRUNCATE TABLE ON MCSConfigServiceClient TO CMI2Adm
go
GRANT UPDATE ON MCSConfigServiceClient TO CMI2Adm
go
GRANT UPDATE STATISTICS ON MCSConfigServiceClient TO CMI2Adm
go

-- Create the MCSMappingConfiguration table
CREATE TABLE MCSMappingConfiguration
(
   Id               	  		NUMERIC(18, 0) IDENTITY NOT NULL,
   SourceContextId         		NUMERIC(18, 0) NOT NULL,
   TargetContextId         		NUMERIC(18, 0) NOT NULL,
   NumberOfParams       		NUMERIC(18, 0) NOT NULL,
   Description         			VARCHAR(1024) NOT NULL,
   ConfigurationTypeId  		NUMERIC(18, 0) NOT NULL,
   ConfigurationServiceClientId  	NUMERIC(18, 0) NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id), -- clustered index on Id
   FOREIGN KEY (ConfigurationTypeId) REFERENCES MCSConfigurationType(Id),
   FOREIGN KEY (ConfigurationServiceClientId) REFERENCES MCSConfigServiceClient(Id),
   FOREIGN KEY (SourceContextId) REFERENCES MCSConfigurationContext(Id),
   FOREIGN KEY (TargetContextId) REFERENCES MCSConfigurationContext(Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

-- This is for StateModelHistory state model id searches
CREATE NONCLUSTERED INDEX MappingConfig_ConfigTypeId ON MCSMappingConfiguration(ConfigurationTypeId)
CREATE UNIQUE INDEX MappingConfig_unique ON MCSMappingConfiguration(ConfigurationServiceClientId, SourceContextId, TargetContextId, ConfigurationTypeId)

IF OBJECT_ID('MCSMappingConfiguration') IS NOT NULL 
   PRINT '<<< CREATED TABLE MCSMappingConfiguration>>>' 
ELSE
   PRINT '<<< FAILED CREATING TABLE MCSMappingConfiguration>>>' 
go

-- Permissions
go
GRANT DELETE ON MCSMappingConfiguration TO CMI2Adm
go
GRANT DELETE STATISTICS ON MCSMappingConfiguration TO CMI2Adm
go
GRANT INSERT ON MCSMappingConfiguration TO CMI2Adm
go
GRANT REFERENCES ON MCSMappingConfiguration TO CMI2Adm
go
GRANT SELECT ON MCSMappingConfiguration TO CMI2Adm
go
GRANT SELECT ON MCSMappingConfiguration TO CMI2Dev
go
GRANT SELECT ON MCSMappingConfiguration TO CMI2Sup
go
GRANT TRUNCATE TABLE ON MCSMappingConfiguration TO CMI2Adm
go
GRANT UPDATE ON MCSMappingConfiguration TO CMI2Adm
go
GRANT UPDATE STATISTICS ON MCSMappingConfiguration TO CMI2Adm
go

-- Create the TargetConfigurationValuetable
CREATE TABLE MCSTargetConfigValue
(
   Id               			NUMERIC(18, 0) IDENTITY NOT NULL,
   TargetSystemValue    		VARCHAR(256) NOT NULL,   
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id) -- clustered index on Id
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

IF OBJECT_ID('MCSTargetConfigValue') IS NOT NULL 
   PRINT '<<< CREATED TABLE MCSTargetConfigValue >>>' 
ELSE
   PRINT '<<< FAILED CREATING TABLE MCSTargetConfigValue >>>' 
go

-- Permissions
go
GRANT DELETE ON MCSTargetConfigValue TO CMI2Adm
go
GRANT DELETE STATISTICS ON MCSTargetConfigValue TO CMI2Adm
go
GRANT INSERT ON MCSTargetConfigValue TO CMI2Adm
go
GRANT REFERENCES ON MCSTargetConfigValue TO CMI2Adm
go
GRANT SELECT ON MCSTargetConfigValue TO CMI2Adm
go
GRANT SELECT ON MCSTargetConfigValue TO CMI2Dev
go
GRANT SELECT ON MCSTargetConfigValue TO CMI2Sup
go
GRANT TRUNCATE TABLE ON MCSTargetConfigValue TO CMI2Adm
go
GRANT UPDATE ON MCSTargetConfigValue TO CMI2Adm
go
GRANT UPDATE STATISTICS ON MCSTargetConfigValue TO CMI2Adm
go


-- Create the SourceConfigurationValuetable
CREATE TABLE MCSSourceConfigValue
(
   Id               			NUMERIC(18, 0) IDENTITY NOT NULL,
   SourceSystemValue    		VARCHAR(256) NOT NULL,
   MappingConfigurationId		NUMERIC(18, 0) NOT NULL,
   TargetConfigurationValueId   NUMERIC(18, 0) NOT NULL,
   SourceConfigGroupId          NUMERIC(18, 0) NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id), -- clustered index on Id
   FOREIGN KEY (MappingConfigurationId) REFERENCES MCSMappingConfiguration(Id),
   FOREIGN KEY (TargetConfigurationValueId) REFERENCES MCSTargetConfigValue(Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

-- This is for TransitionBlockedItem state model id searches
CREATE NONCLUSTERED INDEX SrcConfigValue_ConfigContextId ON MCSSourceConfigValue(MappingConfigurationId)
-- This is for TransitionBlockedItem state model id searches
CREATE NONCLUSTERED INDEX SrcConfig_TgtSysConfigValueId ON MCSSourceConfigValue(TargetConfigurationValueId)

IF OBJECT_ID('MCSSourceConfigValue') IS NOT NULL 
   PRINT '<<< CREATED TABLE MCSSourceConfigValue >>>' 
ELSE
   PRINT '<<< FAILED CREATING TABLE MCSSourceConfigValue >>>' 
go

-- Permissions
go
GRANT DELETE ON MCSSourceConfigValue TO CMI2Adm
go
GRANT DELETE STATISTICS ON MCSSourceConfigValue TO CMI2Adm
go
GRANT INSERT ON MCSSourceConfigValue TO CMI2Adm
go
GRANT REFERENCES ON MCSSourceConfigValue TO CMI2Adm
go
GRANT SELECT ON MCSSourceConfigValue TO CMI2Adm
go
GRANT SELECT ON MCSSourceConfigValue TO CMI2Dev
go
GRANT SELECT ON MCSSourceConfigValue TO CMI2Sup
go
GRANT TRUNCATE TABLE ON MCSSourceConfigValue TO CMI2Adm
go
GRANT UPDATE ON MCSSourceConfigValue TO CMI2Adm
go
GRANT UPDATE STATISTICS ON MCSSourceConfigValue TO CMI2Adm
go

-- Create the MCSKeyLocationQuery
CREATE TABLE MCSKeyLocationQuery
(
   Id               			NUMERIC(18, 0) IDENTITY NOT NULL,
   Value			    		VARCHAR(256) NOT NULL,
   MappingConfigurationId    		NUMERIC(18, 0) NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id), -- clustered index on Id
   FOREIGN KEY (MappingConfigurationId) REFERENCES MCSMappingConfiguration(Id),
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE NONCLUSTERED INDEX MCSKeyLocQuery_mappingConfId ON MCSKeyLocationQuery(MappingConfigurationId)

IF OBJECT_ID('MCSKeyLocationQuery') IS NOT NULL 
   PRINT '<<< CREATED TABLE MCSKeyLocationQuery>>>' 
ELSE
   PRINT '<<< FAILED CREATING TABLE MCSKeyLocationQuery>>>' 
go

-- Permissions
go
GRANT DELETE ON MCSKeyLocationQuery TO CMI2Adm
go
GRANT DELETE STATISTICS ON MCSKeyLocationQuery TO CMI2Adm
go
GRANT INSERT ON MCSKeyLocationQuery TO CMI2Adm
go
GRANT REFERENCES ON MCSKeyLocationQuery TO CMI2Adm
go
GRANT SELECT ON MCSKeyLocationQuery TO CMI2Adm
go
GRANT SELECT ON MCSKeyLocationQuery TO CMI2Dev
go
GRANT SELECT ON MCSKeyLocationQuery TO CMI2Sup
go
GRANT TRUNCATE TABLE ON MCSKeyLocationQuery TO CMI2Adm
go
GRANT UPDATE ON MCSKeyLocationQuery TO CMI2Adm
go
GRANT UPDATE STATISTICS ON MCSKeyLocationQuery TO CMI2Adm
go

-- Create new table
CREATE TABLE MCSSourceConfigGroupSeq
(
   Id               NUMERIC(18, 0) IDENTITY NOT NULL,
   SequenceNumber   NUMERIC(20, 0) NULL,
   PRIMARY KEY (Id) -- clustered index on Id
)

LOCK DATAROWS

IF OBJECT_ID('MCSSourceConfigGroupSeq') IS NOT NULL 
   PRINT '<<< CREATED TABLE MCSSourceConfigGroupSeq >>>' 
ELSE
   PRINT '<<< FAILED CREATING TABLE MCSSourceConfigGroupSeq >>>' 
go

SET IDENTITY_INSERT MCSSourceConfigGroupSeq ON
insert into MCSSourceConfigGroupSeq (
   Id
  ,SequenceNumber
) VALUES (
   1   -- Id - IN numeric(18, 0)
  ,100   -- SequenceNumber - IN numeric(20, 0)
)
SET IDENTITY_INSERT MCSSourceConfigGroupSeq OFF

commit

-- Permissions
GRANT DELETE ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT DELETE STATISTICS ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT INSERT ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT REFERENCES ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT SELECT ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT TRUNCATE TABLE ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT UPDATE ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT UPDATE STATISTICS ON MCSSourceConfigGroupSeq TO CMI2Adm
go
