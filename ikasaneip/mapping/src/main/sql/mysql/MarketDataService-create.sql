--
-- $Id: MarketDataService-create.sql 31790 2013-07-22 13:32:26Z stewmi $
-- $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/sql/mysql/MarketDataService-create.sql $
--
-- ====================================================================
--
-- Copyright (c) 2000-2010 by Mizuho International plc.
-- All Rights Reserved.
--
-- ====================================================================
--
-- Author: CMI2 Development Team
--

-- Create new table
CREATE TABLE eai.`Instrument`
(
   Id               VARCHAR(256) NOT NULL,
   Type             VARCHAR(256) NULL,
   Currency         CHAR(3) NULL,
   Segment          VARCHAR(256) NULL,
   MarketSector     VARCHAR(256) NULL,
   MaturityDate     DATETIME NULL,
   Active           BIT,
   CreatedDateTime  DATETIME NOT NULL,
   UpdatedDateTime  DATETIME NOT NULL,
   PRIMARY KEY (Id) -- clustered index on Id
)

-- Create new table
CREATE TABLE eai.`Identifier`
(
   InstrumentId     VARCHAR(256) NOT NULL,
   Type             VARCHAR(256) NOT NULL,
   Value            VARCHAR(256) NOT NULL,
   FOREIGN KEY (InstrumentId) REFERENCES Instrument(Id)
)

-- Create new table
CREATE TABLE eai.`Price`
(
   Id               INTEGER NOT NULL,
   InstrumentId     VARCHAR(256) NOT NULL,
   Vendor           VARCHAR(256) NOT NULL,
   PriceDateTime    DATETIME NULL,
   Bid              NUMERIC(20, 10) NULL,
   BidYield         NUMERIC(20, 10) NULL,
   Ask              NUMERIC(20, 10) NULL,
   AskYield         NUMERIC(20, 10) NULL,
   Mid              NUMERIC(20, 10) NULL,
   MidYield         NUMERIC(20, 10) NULL,
   CreatedDateTime  DATETIME NOT NULL,
   UpdatedDateTime  DATETIME NOT NULL,
   PRIMARY KEY (Id), -- clustered index on InstrumentId,Vendor
   FOREIGN KEY (InstrumentId) REFERENCES Instrument(Id)
)

-- Create new table
CREATE TABLE eai.`TradePosition`
(
   Id                   INTEGER NOT NULL,
   InstrumentId         VARCHAR(256) NOT NULL,
   BookId               VARCHAR(256) NOT NULL,
   CurrentValue         NUMERIC(38, 10) NULL,
   CreatedDateTime      DATETIME NOT NULL,
   UpdatedDateTime      DATETIME NOT NULL,
   PRIMARY KEY (Id), -- clustered index on BookId,InstrumentId
   FOREIGN KEY (InstrumentId) REFERENCES Instrument(Id)
)
