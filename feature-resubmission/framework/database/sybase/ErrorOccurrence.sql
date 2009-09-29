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

IF OBJECT_ID('ErrorOccurrence') IS NOT NULL
BEGIN
    DROP TABLE ErrorOccurrence
    IF OBJECT_ID('ErrorOccurrence') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE ErrorOccurrence >>>'
    ELSE
        PRINT '<<< DROPPED TABLE ErrorOccurrence >>>'
END

CREATE TABLE ErrorOccurrence
(
    Id                  NUMERIC IDENTITY NOT NULL,
    ModuleName          VARCHAR(128)  NOT NULL,
    FlowName            VARCHAR(128) NULL,
    InitiatorName       VARCHAR(128) NULL,
    FlowElementName     VARCHAR(128) NULL,
    EventId             VARCHAR(128) NULL,
    CurrentEvent        TEXT NULL,
    ErrorDetail         TEXT NOT NULL,
    LogTime       		DATETIME NOT NULL,
    Expiry       		DATETIME NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE UNIQUE INDEX ErrorOccurrence01u ON ErrorOccurrence(Id)

IF OBJECT_ID('ErrorOccurrence') IS NOT NULL
    PRINT '<<< CREATED TABLE ErrorOccurrence >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE ErrorOccurrence >>>'
    

