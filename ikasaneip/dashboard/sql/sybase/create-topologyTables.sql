CREATE TABLE Server
(
   Id             				NUMERIC(18, 0) 		IDENTITY NOT NULL,
   Name		      				VARCHAR(256) 		UNIQUE NOT NULL,
   Description		      		VARCHAR(1024) 		NOT NULL,
   Url		      				VARCHAR(256) 		NOT NULL,
   Port							NUMERIC(18, 0) 		NOT NULL,
   CreatedDateTime      		DATETIME 			default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME 			default getdate() NOT NULL,
   PRIMARY KEY (Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE TABLE IkasanModule
(
   Id             				NUMERIC(18, 0) 		IDENTITY NOT NULL,
   Name		      				VARCHAR(256) 		UNIQUE NOT NULL,
   ContextRoot     				VARCHAR(256) 		NOT NULL,
   Description		      		VARCHAR(1024) 		NOT NULL,
   DiagramUrl      				VARCHAR(256) 		default 'placeholder' NOT NULL,
   ServerId						NUMERIC(18, 0) 		NOT NULL,
   CreatedDateTime      		DATETIME 			default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME 			default getdate() NOT NULL,
   PRIMARY KEY (Id),
   FOREIGN KEY (ServerId) REFERENCES Server(Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE TABLE Flow
(
   Id             				NUMERIC(18, 0) 		IDENTITY NOT NULL,
   Name		      				VARCHAR(256) 	    NOT NULL,
   Description		      		VARCHAR(1024) 		NOT NULL,
   ModuleId						NUMERIC(18, 0) 		NOT NULL,
   CreatedDateTime      		DATETIME 			default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME 			default getdate() NOT NULL,
   PRIMARY KEY (Id),
   FOREIGN KEY (ModuleId) REFERENCES IkasanModule(Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE TABLE Component
(
   Id             				NUMERIC(18, 0) 		IDENTITY NOT NULL,
   Name		      				VARCHAR(256) 	    NOT NULL,
   Description		      		VARCHAR(1024) 		NOT NULL,
   IsConfigurable 				BIT DEFAULT 0,
   FlowId						NUMERIC(18, 0) 		NOT NULL,
   ConfigurationId				VARCHAR(256) 		NULL,
   CreatedDateTime      		DATETIME 			default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME 			default getdate() NOT NULL,
   PRIMARY KEY (Id),
   FOREIGN KEY (FlowId) REFERENCES Flow(Id),
   FOREIGN KEY (ConfigurationId) REFERENCES Configuration(ConfigurationId)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE TABLE BusinessStream
(
   Id             				NUMERIC(18, 0) 	IDENTITY NOT NULL,
   Name		      				VARCHAR(256) 	UNIQUE NOT NULL,
   Description		      		VARCHAR(1024) 	NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE TABLE BusinessStreamFlow
(
   BusinessStreamId				NUMERIC(18, 0) 	NOT NULL,
   FlowId						NUMERIC(18, 0) 	NOT NULL,
   FlowOrder						NUMERIC(18, 0) 	NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (BusinessStreamId, FlowId),
   FOREIGN KEY (BusinessStreamId) REFERENCES BusinessStream(Id),
   FOREIGN KEY (FlowId) REFERENCES Flow(Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE TABLE UserBusinessStream
(
   UserId						NUMERIC(18, 0) 	NOT NULL,
   BusinessStreamId				NUMERIC(18, 0) 	NOT NULL,
   CreatedDateTime      		DATETIME default getdate() NOT NULL,
   UpdatedDateTime      		DATETIME default getdate() NOT NULL,
   PRIMARY KEY (UserId, BusinessStreamId),
   FOREIGN KEY (UserId) REFERENCES Users(Id),
   FOREIGN KEY (BusinessStreamId) REFERENCES BusinessStream(Id)
) 
LOCK DATAROWS
WITH IDENTITY_GAP=1
