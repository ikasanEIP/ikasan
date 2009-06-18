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

IF OBJECT_ID('CleanupChunksCommand') IS NOT NULL
BEGIN
    DROP TABLE CleanupChunksCommand
    IF OBJECT_ID('CleanupChunksCommand') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE CleanupChunksCommand >>>'
    ELSE
        PRINT '<<< DROPPED TABLE CleanupChunksCommand >>>'
END
go

CREATE TABLE CleanupChunksCommand 
(
    Id              numeric(18,0),
    FileChunkHeaderId           numeric(18,0) NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go

IF OBJECT_ID('CleanupChunksCommand') IS NOT NULL
    PRINT '<<< CREATED TABLE CleanupChunksCommand >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE CleanupChunksCommand >>>'
go

-- NOTE: Permissioning needs to be done on a per client basis, we recommend something like the below
--GRANT ALL ON CleanupChunksCommand TO IkasanAdm
--GRANT SELECT ON CleanupChunksCommand TO IkasanSup
--GRANT SELECT ON CleanupChunksCommand TO IkasanDev
--go

CREATE UNIQUE INDEX CleanupChunksCommand01u
    ON CleanupChunksCommand(Id)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('CleanupChunksCommand') AND name='CleanupChunksCommand01u')
    PRINT '<<< CREATED INDEX CleanupChunksCommand.CleanupChunksCommand01u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX CleanupChunksCommand.CleanupChunksCommand01u >>>'
go