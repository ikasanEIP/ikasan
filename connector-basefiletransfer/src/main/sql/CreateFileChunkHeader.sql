--
-- $Id: CreateFileChunkHeader.sql 16767 2009-04-23 12:37:52Z mitcje $
-- $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/sql/CreateFileChunkHeader.sql $
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

IF OBJECT_ID('FileChunkHeader') IS NOT NULL
BEGIN
    DROP TABLE FileChunkHeader
    IF OBJECT_ID('FileChunkHeader') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE FileChunkHeader >>>'
    ELSE
        PRINT '<<< DROPPED TABLE FileChunkHeader >>>'
END
go

CREATE TABLE FileChunkHeader 
(
    Id              numeric(18,0) IDENTITY,
    SequenceLength  numeric(18,0) NOT NULL,
    InternalMd5Hash  char(32) NULL,
    ExternalMd5Hash  char(32) NULL,
    FileName        varchar(255) NOT NULL,
    ChunkTimeStamp  numeric(18,0) NOT NULL,
    ClientId  varchar(255) NULL

)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go

IF OBJECT_ID('FileChunkHeader') IS NOT NULL
    PRINT '<<< CREATED TABLE FileChunkHeader >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE FileChunkHeader >>>'
go

-- NOTE: Permissioning needs to be done on a per client basis, we recommend something like the below
--GRANT ALL ON FileChunkHeader TO IkasanAdm
--GRANT SELECT ON FileChunkHeader TO IkasanSup
--GRANT SELECT ON FileChunkHeader TO IkasanDev
--go

CREATE UNIQUE INDEX FileChunkHeader01u
    ON FileChunkHeader(Id)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('FileChunkHeader') AND name='FileChunkHeader01u')
    PRINT '<<< CREATED INDEX FileChunkHeader.FileChunkHeader01u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX FileChunkHeader.FileChunkHeader01u >>>'
go