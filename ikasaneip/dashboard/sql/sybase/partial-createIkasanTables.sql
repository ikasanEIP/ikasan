ALTER TABLE Users
ADD FirstName VARCHAR(50) NULL
					
ALTER TABLE Users
ADD Surname	VARCHAR(50) NULL

ALTER TABLE Users
ADD Department	VARCHAR(50) NULL

CREATE TABLE PolicyLinkType
(
   Id             				NUMERIC(18, 0) IDENTITY NOT NULL,
   Name		      				VARCHAR(256) UNIQUE NOT NULL,
   TableName		      		VARCHAR(256) UNIQUE NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE TABLE PolicyLink
(
   Id               			NUMERIC(18, 0) IDENTITY NOT NULL,
   PolicyLinkTypeId     		NUMERIC(18, 0) NOT NULL,
   TargetId         			NUMERIC(18, 0) NOT NULL,
   Name				      		VARCHAR(256) NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id), -- clustered index on Id
   FOREIGN KEY (PolicyLinkTypeId) REFERENCES PolicyLinkType(Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1


CREATE TABLE SecurityPrincipal
(
   Id               	NUMERIC(18, 0) 		   IDENTITY NOT NULL,
   Name		      		VARCHAR(256)		   UNIQUE NOT NULL,
   PrincipalType		VARCHAR(256)		    NOT NULL,
   Description			VARCHAR(1024)		    NOT NULL,
   CreatedDateTime      DATETIME default getdate() NOT NULL,
   UpdatedDateTime      DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id) -- clustered index on Id
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE NONCLUSTERED INDEX Principal_Name ON SecurityPrincipal(Name)
CREATE NONCLUSTERED INDEX Principal_Type ON SecurityPrincipal(PrincipalType)

CREATE TABLE SecurityRole
(
   Id               	NUMERIC(18, 0) 		       IDENTITY NOT NULL,
   Name		      		VARCHAR(256)		       UNIQUE NOT NULL,
   Description	     	VARCHAR(1024)		   	   NOT NULL,
   CreatedDateTime      DATETIME default getdate() NOT NULL,
   UpdatedDateTime      DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id) -- clustered index on Id
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE NONCLUSTERED INDEX Role_Name ON SecurityRole(Name)

CREATE TABLE SecurityPolicy
(
   Id               				NUMERIC(18, 0) IDENTITY NOT NULL,
   PolicyLinkId               		NUMERIC(18, 0) NULL,
   Name		      					VARCHAR(256) UNIQUE NOT NULL,
   Description	     				VARCHAR(1024)		   	   NOT NULL,
   CreatedDateTime      			DATETIME default getdate() NOT NULL,
   UpdatedDateTime      			DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id), -- clustered index on Id
   FOREIGN KEY (PolicyLinkId) REFERENCES PolicyLink(Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE NONCLUSTERED INDEX Policy_Name ON SecurityPolicy(Name)

CREATE TABLE UserPrincipal
(
   UserId         				NUMERIC(18, 0) NOT NULL,
   PrincipalId               	NUMERIC(18, 0) NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (UserId, PrincipalId), -- clustered index on Id
   FOREIGN KEY (PrincipalId) REFERENCES SecurityPrincipal(Id),
   FOREIGN KEY (UserId) REFERENCES Users(Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE TABLE PrincipalRole
(
   PrincipalId               	NUMERIC(18, 0) NOT NULL,
   RoleId         				NUMERIC(18, 0) NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (PrincipalId, RoleId), -- clustered index on Id
   FOREIGN KEY (PrincipalId) REFERENCES SecurityPrincipal(Id),
   FOREIGN KEY (RoleId) REFERENCES SecurityRole(Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1
					
					
CREATE TABLE RolePolicy
(
   RoleId               	    NUMERIC(18, 0) NOT NULL,
   PolicyId               	    NUMERIC(18, 0) NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (RoleId, PolicyId), -- clustered index on Id
   FOREIGN KEY (PolicyId) REFERENCES SecurityPolicy(Id),
   FOREIGN KEY (RoleId) REFERENCES SecurityRole(Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE TABLE AuthenticationMethod
(
   Id               			NUMERIC(18, 0) NOT NULL,
   Method    		          	VARCHAR(128) NOT NULL,
   LdapServerUrl    		  	VARCHAR(256) NULL,
   LdapBindUserDn		      	VARCHAR(256) NULL,
   LdapBindUserPassword   		VARCHAR(64) NULL,
   LdapUserSearchBaseDn   		VARCHAR(256) NULL,
   LdapUserSearchFilter   		VARCHAR(128) NULL,
   EmailAttributeName 			VARCHAR(64) NULL,
   UserAccNameAttributeName 	VARCHAR(64) NULL,
   AccountTypeAttributeName   	VARCHAR(64) NULL,
   AppSecBaseDn   				VARCHAR(64) NULL,
   AppSecGroupAttributeName 	VARCHAR(64) NULL,
   FirstNameAttributeName 		VARCHAR(64) NULL,
   SurnameAttributeName 		VARCHAR(64) NULL,
   DepartmentAttributeName	 	VARCHAR(64) NULL,
   AppSecDescAttributeName		VARCHAR(64) NULL,
   LdapUserDescAttributeName	VARCHAR(64) NULL,
   MemberofAttributeName		VARCHAR(64) NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE TABLE MCSConfigurationType
(
   Id               	NUMERIC(18, 0) 		   IDENTITY NOT NULL,
   Name		      		VARCHAR(256)		   UNIQUE NOT NULL,
   CreatedDateTime      DATETIME default getdate() NOT NULL,
   UpdatedDateTime      DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id) -- clustered index on Id
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE TABLE MCSConfigurationContext
(
   Id               	NUMERIC(18, 0) 		   IDENTITY NOT NULL,
   Name		      		VARCHAR(256)		   UNIQUE NOT NULL,
   Description	      	VARCHAR(1024)		   NOT NULL,
   CreatedDateTime      DATETIME default getdate() NOT NULL,
   UpdatedDateTime      DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id) -- clustered index on Id
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

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
WITH IDENTITY_GAP=1

CREATE NONCLUSTERED INDEX ConfigServiceClient_Name ON MCSConfigServiceClient(Name)

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

CREATE NONCLUSTERED INDEX SrcConfigValue_ConfigContextId ON MCSSourceConfigValue(MappingConfigurationId)
CREATE NONCLUSTERED INDEX SrcConfig_TgtSysConfigValueId ON MCSSourceConfigValue(TargetConfigurationValueId)

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

CREATE TABLE MCSSourceConfigGroupSeq
(
   Id               NUMERIC(18, 0) IDENTITY NOT NULL,
   SequenceNumber   NUMERIC(20, 0) NULL,
   PRIMARY KEY (Id) -- clustered index on Id
)

LOCK DATAROWS
WITH IDENTITY_GAP=1
					
SET IDENTITY_INSERT MCSSourceConfigGroupSeq ON
insert into MCSSourceConfigGroupSeq (
   Id
  ,SequenceNumber
) VALUES (
   1
  ,100)
SET IDENTITY_INSERT MCSSourceConfigGroupSeq OFF

CREATE TABLE PlatformConfiguration
(
   Id               			NUMERIC(18, 0) IDENTITY NOT NULL,
   Name			    			VARCHAR(256) NOT NULL,
   Value			    		VARCHAR(256) NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id), -- clustered index on Id,
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1