CREATE TABLE PlatformConfiguration
(
   Id               			NUMERIC(18, 0) IDENTITY NOT NULL,
   Name			    			VARCHAR(256) NOT NULL UNIQUE,
   Value			    		VARCHAR(256) NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id), -- clustered index on Id,
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1
          
          
          
CREATE TABLE PlatformConfiguration
(
   Id               			NUMERIC(18, 0) IDENTITY NOT NULL,
   Name			    			VARCHAR(256) NOT NULL UNIQUE,
   Value			    		VARCHAR(256) NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id), -- clustered index on Id,
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1
          
          
          
set identity_insert PlatformConfiguration on

 insert into PlatformConfiguration (
   Id
  ,Name
  ,[Value]
  ,CreatedDateTime
  ,UpdatedDateTime
) VALUES (
   1   -- Id - IN numeric(18, 0)
  ,'mappingExportSchemaLocation'  -- Name - IN varchar(256)
  ,'http://svc-stewmi:8080/ikasan-dashboard/static/org/ikasan/dashboard/mappingConfigurationImportExport.xsd'  -- Value - IN varchar(256)
  ,getDate()  -- CreatedDateTime - IN datetime
  ,getDate()  -- UpdatedDateTime - IN datetime
)

insert into PlatformConfiguration (
   Id
  ,Name
  ,[Value]
  ,CreatedDateTime
  ,UpdatedDateTime
) VALUES (
   2   -- Id - IN numeric(18, 0)
  ,'mappingValuesExportSchemaLocation'  -- Name - IN varchar(256)
  ,'http://svc-stewmi:8080/ikasan-dashboard/static/org/ikasan/dashboard/mappingConfigurationValuesImportExport.xsd'  -- Value - IN varchar(256)
  ,getDate()  -- CreatedDateTime - IN datetime
  ,getDate()  -- UpdatedDateTime - IN datetime
)

set identity_insert PlatformConfiguration off

commit