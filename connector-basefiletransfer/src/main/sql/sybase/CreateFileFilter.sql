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
    
