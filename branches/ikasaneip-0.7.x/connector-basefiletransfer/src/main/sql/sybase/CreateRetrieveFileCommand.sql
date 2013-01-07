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

IF OBJECT_ID('RetrieveFileCommand') IS NOT NULL
BEGIN
    DROP TABLE RetrieveFileCommand
    IF OBJECT_ID('RetrieveFileCommand') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE RetrieveFileCommand >>>'
    ELSE
        PRINT '<<< DROPPED TABLE RetrieveFileCommand >>>'
END
go

CREATE TABLE RetrieveFileCommand 
(
    Id                   numeric(18,0),
    Destructive          bit NOT NULL,
    RenameOnSuccess      bit NOT NULL,
    RenameExtension      varchar(255) NULL,
    MoveOnSuccess        bit NOT NULL,
    MoveNewPath          varchar(255) NULL,
    SourcePath           varchar(255) NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go

IF OBJECT_ID('RetrieveFileCommand') IS NOT NULL
    PRINT '<<< CREATED TABLE RetrieveFileCommand >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE RetrieveFileCommand >>>'
go

-- NOTE: Permissioning needs to be done on a per client basis, we recommend something like the below
--GRANT ALL ON RetrieveFileCommand TO IkasanAdm
--GRANT SELECT ON RetrieveFileCommand TO IkasanSup
--GRANT SELECT ON RetrieveFileCommand TO IkasanDev
--go

CREATE UNIQUE INDEX RetrieveFileCommand01u
    ON RetrieveFileCommand(Id)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('RetrieveFileCommand') AND name='RetrieveFileCommand01u')
    PRINT '<<< CREATED INDEX RetrieveFileCommand.RetrieveFileCommand01u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX RetrieveFileCommand.RetrieveFileCommand01u >>>'
go
