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

IF OBJECT_ID('IkasanWiretap') IS NOT NULL
BEGIN
    DROP TABLE IkasanWiretap
    IF OBJECT_ID('IkasanWiretap') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE IkasanWiretap >>>'
    ELSE
        PRINT '<<< DROPPED TABLE IkasanWiretap >>>'
END

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

CREATE INDEX IkasanWiretap02i ON IkasanWiretap(ModuleName, FlowName, ComponentName, EventId, PayloadId, CeatedDateTime)

CREATE INDEX IkasanWiretap03i ON IkasanWiretap(Expiry)

IF OBJECT_ID('IkasanWiretap') IS NOT NULL
    PRINT '<<< CREATED TABLE IkasanWiretap >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE IkasanWiretap >>>'
    

