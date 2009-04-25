--
-- $Id: CreateFileChunk.sql 16767 2009-04-23 12:37:52Z mitcje $
-- $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/sql/CreateFileChunk.sql $
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

IF OBJECT_ID('FileChunk') IS NOT NULL
BEGIN
    DROP TABLE FileChunk
    IF OBJECT_ID('FileChunk') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE FileChunk >>>'
    ELSE
        PRINT '<<< DROPPED TABLE FileChunk >>>'
END
go

CREATE TABLE FileChunk 
(
    Id              numeric(18,0) IDENTITY,
    Content         image  NOT NULL,
    Ordinal         numeric(18,0) NOT NULL,
    Md5Hash  		char(32) NULL,
    FileChunkHeader_Id numeric(18,0) NOT NULL references FileChunkHeader(Id)

)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go

IF OBJECT_ID('FileChunk') IS NOT NULL
    PRINT '<<< CREATED TABLE FileChunk >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE FileChunk >>>'
go

-- NOTE: Permissioning needs to be done on a per client basis, we recommend something like the below
--GRANT ALL ON FileChunk TO IkasanAdm
--GRANT SELECT ON FileChunk TO IkasanSup
--GRANT SELECT ON FileChunk TO IkasanDev
--go

CREATE UNIQUE INDEX FileChunk01u
    ON FileChunk(Id)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('FileChunk') AND name='FileChunk01u')
    PRINT '<<< CREATED INDEX FileChunk.FileChunk01u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX FileChunk.FileChunk01u >>>'
go