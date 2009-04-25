--
-- $Id: CreateFileFilter.sql 16767 2009-04-23 12:37:52Z mitcje $
-- $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/sql/CreateFileFilter.sql $
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

IF OBJECT_ID('FileFilter') IS NOT NULL
BEGIN
    DROP TABLE FileFilter
    IF OBJECT_ID('FileFilter') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE FileFilter >>>'
    ELSE
        PRINT '<<< DROPPED TABLE FileFilter >>>'
END
go

CREATE TABLE FileFilter 
(
    Id              numeric(18,0) IDENTITY,
    ClientId        varchar(255)  NOT NULL,
    Criteria        varchar(255)  NOT NULL,
    LastModified    datetime      NOT NULL,
    LastAccessed    datetime      NOT NULL,
    Size            numeric(18,0) NOT NULL,
    CreatedDateTime datetime      DEFAULT getDate() NOT NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go

IF OBJECT_ID('FileFilter') IS NOT NULL
    PRINT '<<< CREATED TABLE FileFilter >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE FileFilter >>>'
go

-- NOTE: Permissioning needs to be done on a per client basis, we recommend something like the below
--GRANT ALL ON FileFilter TO IkasanAdm
--GRANT SELECT ON FileFilter TO IkasanSup
--GRANT SELECT ON FileFilter TO IkasanDev
--go

CREATE UNIQUE INDEX FileFilter01u
    ON FileFilter(Id)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('FileFilter') AND name='FileFilter01u')
    PRINT '<<< CREATED INDEX FileFilter.FileFilter01u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX FileFilter.FileFilter01u >>>'
go

CREATE UNIQUE INDEX FileFilter02u
    ON FileFilter(ClientId,Criteria,LastModified,Size)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('FileFilter') AND name='FileFilter02u')
    PRINT '<<< CREATED INDEX FileFilter.FileFilter02u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX FileFilter.FileFilter02u >>>'
go

CREATE INDEX FileFilter03u
    ON FileFilter(ClientId,CreatedDateTime)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('FileFilter') AND name='FileFilter03u')
    PRINT '<<< CREATED INDEX FileFilter.FileFilter03u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX FileFilter.FileFilter03u >>>'
    
