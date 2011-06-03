--
-- $Id$
-- $URL$
-- 
-- ====================================================================
-- Ikasan Enterprise Integration Platform
--  
-- Distributed under the Modified BSD License.
-- Copyright notice: The copyright for this software and a full listing 
-- of individual contributors are as shown in the packaged copyright.txt 
-- file. 
-- 
-- All rights reserved.
-- 
-- Redistribution and use in source and binary forms, with or without 
-- modification, are permitted provided that the following conditions are met:
-- 
--  - Redistributions of source code must retain the above copyright notice, 
--    this list of conditions and the following disclaimer.
-- 
--  - Redistributions in binary form must reproduce the above copyright notice, 
--    this list of conditions and the following disclaimer in the documentation 
--    and/or other materials provided with the distribution.
-- 
--  - Neither the name of the ORGANIZATION nor the names of its contributors may
--    be used to endorse or promote products derived from this software without 
--    specific prior written permission.
-- 
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
-- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
-- IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
-- DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
-- FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
-- DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
-- SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
-- CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
-- OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
-- USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-- ====================================================================
-- 
-- Author:  Ikasan Development Team
-- 
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
