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

IF OBJECT_ID('TransactionalResourceCommand') IS NOT NULL
BEGIN
    DROP TABLE TransactionalResourceCommand
    IF OBJECT_ID('TransactionalResourceCommand') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE TransactionalResourceCommand >>>'
    ELSE
        PRINT '<<< DROPPED TABLE TransactionalResourceCommand >>>'
END
go

CREATE TABLE TransactionalResourceCommand 
(
    Id              numeric(18,0) IDENTITY,
    State           varchar(255)  NOT NULL,
    Xid_Id numeric(18,0) NOT NULL references Xid(Id),
    Type           varchar(255)  NOT NULL,
    ExecutionTimestamp char(24) NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go

IF OBJECT_ID('TransactionalResourceCommand') IS NOT NULL
    PRINT '<<< CREATED TABLE TransactionalResourceCommand >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE TransactionalResourceCommand >>>'
go

-- NOTE: Permissioning needs to be done on a per client basis, we recommend something like the below
--GRANT ALL ON TransactionalResourceCommand TO IkasanAdm
--GRANT SELECT ON TransactionalResourceCommand TO IkasanSup
--GRANT SELECT ON TransactionalResourceCommand TO IkasanDev
--go

CREATE UNIQUE INDEX TransResCommand01u
    ON TransactionalResourceCommand(Id)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('TransactionalResourceCommand') AND name='TransResCommand01u')
    PRINT '<<< CREATED INDEX TransactionalResourceCommand.TransResCommand01u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX TransactionalResourceCommand.TransResCommand01u >>>'
go