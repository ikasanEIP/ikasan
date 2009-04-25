--
-- $Id: CreateDeliverFileCommand.sql 16767 2009-04-23 12:37:52Z mitcje $
-- $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/main/sql/CreateDeliverFileCommand.sql $
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

IF OBJECT_ID('DeliverFileCommand') IS NOT NULL
BEGIN
    DROP TABLE DeliverFileCommand
    IF OBJECT_ID('DeliverFileCommand') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE DeliverFileCommand >>>'
    ELSE
        PRINT '<<< DROPPED TABLE DeliverFileCommand >>>'
END
go

CREATE TABLE DeliverFileCommand 
(
    Id                     numeric(18,0),
    FileName               varchar(255) NULL,
    TempFileName           varchar(255) NULL,
    OutputDirectory        varchar(255) NULL,
    OverwriteExisting      bit NOT NULL,
    PutAttempted           bit NOT NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go

IF OBJECT_ID('DeliverFileCommand') IS NOT NULL
    PRINT '<<< CREATED TABLE DeliverFileCommand >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE DeliverFileCommand >>>'
go

-- NOTE: Permissioning needs to be done on a per client basis, we recommend something like the below
--GRANT ALL ON DeliverFileCommand TO IkasanAdm
--GRANT SELECT ON DeliverFileCommand TO IkasanSup
--GRANT SELECT ON DeliverFileCommand TO IkasanDev
--go

CREATE UNIQUE INDEX DeliverFileCommand01u
    ON DeliverFileCommand(Id)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('DeliverFileCommand') AND name='DeliverFileCommand01u')
    PRINT '<<< CREATED INDEX DeliverFileCommand.DeliverFileCommand01u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX DeliverFileCommand.DeliverFileCommand01u >>>'
go