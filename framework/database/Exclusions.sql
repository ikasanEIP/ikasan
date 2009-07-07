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

IF OBJECT_ID('ExcludedPayload') IS NOT NULL
BEGIN
    DROP TABLE ExcludedPayload
    IF OBJECT_ID('ExcludedPayload') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE ExcludedPayload >>>'
    ELSE
        PRINT '<<< DROPPED TABLE ExcludedPayload >>>'
END
go
IF OBJECT_ID('ExcludedEvent') IS NOT NULL
BEGIN
    DROP TABLE ExcludedEvent
    IF OBJECT_ID('ExcludedEvent') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE ExcludedEvent >>>'
    ELSE
        PRINT '<<< DROPPED TABLE ExcludedEvent >>>'
END
go
CREATE TABLE ExcludedEvent
(
    Id                 numeric(19,0) IDENTITY,
    ModuleName         varchar(255)  NOT NULL,
    FlowName           varchar(255)  NOT NULL,
    ExclusionTime      datetime      NOT NULL,
    EventId            varchar(255)  NOT NULL,
    Priority        numeric(19,0)  NULL,
    Timestamp       numeric(19,0)  NULL,
    CONSTRAINT ExcludedEv_125240472
    PRIMARY KEY CLUSTERED (Id)

)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go
IF OBJECT_ID('ExcludedEvent') IS NOT NULL
    PRINT '<<< CREATED TABLE ExcludedEvent >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE ExcludedEvent >>>'
go




CREATE TABLE ExcludedPayload
(
    PersistenceId   numeric(19,0)  IDENTITY,
    PayloadId       varchar(255)   NULL,
    Content         image          NULL,
    Priority        numeric(19,0)  NULL,
    Timestamp       numeric(19,0)  NULL,   
    Spec            varchar(255)   NULL,
    Name            varchar(255)   NULL,
    SrcSytem        varchar(255)   NULL,
    ExcludedEventId numeric(19,0)  NULL,
    PayloadPosition int            NULL,
    CONSTRAINT ExcludedPa_445241612
    PRIMARY KEY CLUSTERED (PersistenceId)
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go
IF OBJECT_ID('ExcludedPayload') IS NOT NULL
    PRINT '<<< CREATED TABLE ExcludedPayload >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE ExcludedPayload >>>'
go





ALTER TABLE ExcludedPayload
    ADD CONSTRAINT FK54371C4438A8DA2F
    FOREIGN KEY (ExcludedEventId)
    REFERENCES ExcludedEvent (Id)
go