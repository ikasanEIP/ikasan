--
-- $Id$
-- $URL$
-- 
-- ====================================================================
-- Ikasan Enterprise Integration Platform
-- Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
-- by the @authors tag. See the copyright.txt in the distribution for a
-- full listing of individual contributors.
--
-- This is free software; you can redistribute it and/or modify it
-- under the terms of the GNU Lesser General Public License as
-- published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This software is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this software; if not, write to the 
-- Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
-- or see the FSF site: http://www.fsfeurope.org/.
-- ====================================================================

-- ====================================================================
-- Table creation script for the Ikasan framework platform services.
--
-- ====================================================================

-- ====================================================================
-- Wiretap platform services tables.
-- The following tables support the wiretap event auditing as 
-- dynamic insertions for event tracking within a flow.
-- ====================================================================
-- FlowEventTrigger
CREATE TABLE FlowEventTrigger
(
    Id                  NUMERIC IDENTITY NOT NULL,
    ModuleName          VARCHAR(128)  NOT NULL,
    FlowName            VARCHAR(128)  NOT NULL,
    Relationship        VARCHAR(32)  NOT NULL,
    FlowElementName     VARCHAR(128),
    JobName           	VARCHAR(64)   NOT NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE UNIQUE INDEX FlowEventTrigger01u ON FlowEventTrigger(Id)

IF OBJECT_ID('FlowEventTrigger') IS NOT NULL
    PRINT '<<< CREATED TABLE FlowEventTrigger >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE FlowEventTrigger >>>'
    
-- FlowEventTriggerParameters
CREATE TABLE FlowEventTriggerParameters
(
    TriggerId          NUMERIC NOT NULL,
    ParamName          VARCHAR(128)  NOT NULL,
    ParamValue         VARCHAR(128) 
)
  ALTER TABLE FlowEventTriggerParameters
    ADD CONSTRAINT FlowEventTriggerParam_Id_FK
    FOREIGN KEY (TriggerId)
    REFERENCES FlowEventTrigger (Id)  
IF OBJECT_ID('FlowEventTrigger') IS NOT NULL
    PRINT '<<< CREATED TABLE FlowEventTriggerParameters >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE FlowEventTriggerParameters >>>'
    
-- Wiretap event persistence
CREATE TABLE IkasanWiretap
(
    Id                  NUMERIC IDENTITY NOT NULL,
    ModuleName          VARCHAR(128)  NOT NULL,
    FlowName            VARCHAR(128)  NOT NULL,
    ComponentName       VARCHAR(128)  NOT NULL,
    EventId             VARCHAR(64)   NOT NULL,
    PayloadId           VARCHAR(64)   NOT NULL,
    PayloadContent      TEXT          NOT NULL,
    CreatedDateTime     DATETIME      DEFAULT getDate() NOT NULL,
    UpdatedDateTime     DATETIME      DEFAULT getDate() NOT NULL,
    Expiry              DATETIME      NOT NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE UNIQUE INDEX IkasanWiretap01u ON IkasanWiretap(Id)

IF OBJECT_ID('IkasanWiretap') IS NOT NULL
    PRINT '<<< CREATED TABLE IkasanWiretap >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE IkasanWiretap >>>'
    

-- ====================================================================
-- Initiator state persistence command table.
-- This table persists the status of an initiator i.e. running/stopped
-- between server bounces.
-- ====================================================================
CREATE TABLE InitiatorCommand
(
    Id                  NUMERIC IDENTITY NOT NULL,
    ModuleName          VARCHAR(128)  NOT NULL,
    InitiatorName       VARCHAR(128)  NOT NULL,
    Action              VARCHAR(64)   NOT NULL,
    Actor               VARCHAR(64)   NOT NULL,
    SubmittedTime       DATETIME      DEFAULT getDate() NOT NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE UNIQUE INDEX InitiatorCommand01u ON InitiatorCommand(Id)

IF OBJECT_ID('InitiatorCommand') IS NOT NULL
    PRINT '<<< CREATED TABLE InitiatorCommand >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE InitiatorCommand >>>'
    
