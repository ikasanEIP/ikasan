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