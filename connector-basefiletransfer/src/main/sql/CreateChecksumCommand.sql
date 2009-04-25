-- 
--
-- $Id: CreateChecksumCommand.sql 16767 2009-04-23 12:37:52Z mitcje $
-- $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/sql/CreateChecksumCommand.sql $
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

IF OBJECT_ID('ChecksumCommand') IS NOT NULL
BEGIN
    DROP TABLE ChecksumCommand
    IF OBJECT_ID('ChecksumCommand') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE ChecksumCommand >>>'
    ELSE
        PRINT '<<< DROPPED TABLE ChecksumCommand >>>'
END
go

CREATE TABLE ChecksumCommand 
(
    Id                   numeric(18,0),
    Destructive          bit NOT NULL,
    ChecksumFilePath     varchar(255) NULL    
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go

IF OBJECT_ID('ChecksumCommand') IS NOT NULL
    PRINT '<<< CREATED TABLE ChecksumCommand >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE ChecksumCommand >>>'
go

-- NOTE: Permissioning needs to be done on a per client basis, we recommend something like the below
--GRANT ALL ON ChecksumCommand TO IkasanAdm
--GRANT SELECT ON ChecksumCommand TO IkasanSup
--GRANT SELECT ON ChecksumCommand TO IkasanDev
--go

CREATE UNIQUE INDEX ChecksumCommand01u
    ON ChecksumCommand(Id)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('ChecksumCommand') AND name='ChecksumCommand01u')
    PRINT '<<< CREATED INDEX ChecksumCommand.ChecksumCommand01u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX ChecksumCommand.ChecksumCommand01u >>>'
go