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
--

IF OBJECT_ID('Xid') IS NOT NULL
BEGIN
    DROP TABLE Xid
    IF OBJECT_ID('Xid') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE Xid >>>'
    ELSE
        PRINT '<<< DROPPED TABLE Xid >>>'
END
go

CREATE TABLE Xid 
(
    Id              numeric(18,0) IDENTITY,
    State           varchar(255)  NOT NULL,
    GlobalTransactionId          varchar(255)  NOT NULL,
    BranchQualifier          varchar(255)  NOT NULL,
    FormatId          numeric(18,0)  NOT NULL,
    ClientId           varchar(255)  NOT NULL,
    CreatedDateTime  datetime NOT NULL,
    LastUpdatedDateTime  datetime NOT NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go

IF OBJECT_ID('Xid') IS NOT NULL
    PRINT '<<< CREATED TABLE Xid >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE Xid >>>'
go

-- NOTE: Permissioning needs to be done on a per client basis, we recommend something like the below
--GRANT ALL ON Xid TO IkasanAdm
--GRANT SELECT ON Xid TO IkasanSup
--GRANT SELECT ON Xid TO IkasanDev
--go

CREATE UNIQUE INDEX Xid01u
    ON Xid(Id)
go

CREATE UNIQUE INDEX Xid02u
    ON Xid(GlobalTransactionId, BranchQualifier)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('Xid') AND name='Xid01u')
    PRINT '<<< CREATED INDEX Xid.Xid01u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX Xid.Xid01u >>>'
go