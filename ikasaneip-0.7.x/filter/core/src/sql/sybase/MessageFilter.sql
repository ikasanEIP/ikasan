--
-- $Id: $
-- $URL: $
-- 
-- ====================================================================
-- Ikasan Enterprise Integration Platform
-- Copyright (c) 2003-2010 Mizuho International plc. and individual contributors as indicated
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

-- Drop aleady existing table if any
IF OBJECT_ID('MessageFilter') IS NOT NULL
BEGIN
    DROP TABLE MessageFilter
    IF OBJECT_ID('MessageFilter') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE MessageFilter >>>'
    ELSE
        PRINT '<<< DROPPED TABLE MessageFilter >>>'
END
go

-- Create new table
CREATE TABLE dbo.MessageFilter
(
    Criteria        numeric(18,0) NOT NULL,
    ClientId        varchar(255)  NOT NULL,
    CreatedDateTime datetime      NOT NULL,
    Expiry          datetime      NOT NULL,
    PRIMARY KEY     (Criteria, ClientId)
)
LOCK DATAROWS
IF OBJECT_ID('dbo.MessageFilter') IS NOT NULL
    PRINT '<<< CREATED TABLE dbo.MessageFilter >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE dbo.MessageFilter >>>'
GO

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('dbo.MessageFilter') AND name='MessageFilter01i')
BEGIN
    DROP INDEX MessageFilter.MessageFilter01i
    IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('dbo.MessageFilter') AND name='MessageFilter01i')
        PRINT '<<< FAILED DROPPING INDEX dbo.MessageFilter.MessageFilter01i >>>'
    ELSE
        PRINT '<<< DROPPED INDEX dbo.MessageFilter.MessageFilter01i >>>'
END
go
CREATE NONCLUSTERED INDEX MessageFilter01i
    ON MessageFilter(Expiry)
go
IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('dbo.MessageFilter') AND name='MessageFilter01i')
    PRINT '<<< CREATED INDEX dbo.MessageFilter.MessageFilter01i >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX dbo.MessageFilter.MessageFilter01i >>>'
go
