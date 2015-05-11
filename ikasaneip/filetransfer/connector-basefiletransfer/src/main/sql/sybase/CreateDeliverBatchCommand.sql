--
-- $Id: CreateFTDeliverBatchCommand.sql 43183 2015-02-06 11:15:54Z stewmi $
-- $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/Ikasan-0.8.4.x/connector-basefiletransfer/src/main/sql/sybase/CreateFTDeliverBatchCommand.sql $
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

IF OBJECT_ID('FTDeliverBatchCommand') IS NOT NULL
BEGIN
    DROP TABLE FTDeliverBatchCommand
    IF OBJECT_ID('FTDeliverBatchCommand') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE FTDeliverBatchCommand >>>'
    ELSE
        PRINT '<<< DROPPED TABLE FTDeliverBatchCommand >>>'
END
go

CREATE TABLE FTDeliverBatchCommand 
(
    Id                     numeric(18,0),
    OutputDirectory           varchar(255) NULL,
    TempDirectory           varchar(255) NULL,
    BatchFolder            varchar(255) NULL,
    PutAttempted           bit NOT NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go

IF OBJECT_ID('FTDeliverBatchCommand') IS NOT NULL
    PRINT '<<< CREATED TABLE FTDeliverBatchCommand >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE FTDeliverBatchCommand >>>'
go

-- NOTE: Permissioning needs to be done on a per client basis, we recommend something like the below
--GRANT ALL ON FTDeliverBatchCommand TO IkasanAdm
--GRANT SELECT ON FTDeliverBatchCommand TO IkasanSup
--GRANT SELECT ON FTDeliverBatchCommand TO IkasanDev
--go

CREATE UNIQUE INDEX FTDeliverBatchCommand01u
    ON FTDeliverBatchCommand(Id)
go

IF EXISTS (SELECT * FROM sysindexes WHERE id=OBJECT_ID('FTDeliverBatchCommand') AND name='FTDeliverBatchCommand01u')
    PRINT '<<< CREATED INDEX FTDeliverBatchCommand.FTDeliverBatchCommand01u >>>'
ELSE
    PRINT '<<< FAILED CREATING INDEX FTDeliverBatchCommand.FTDeliverBatchCommand01u >>>'
go