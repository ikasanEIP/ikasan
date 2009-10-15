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
IF OBJECT_ID('ExcludedPayloadAttribute') IS NOT NULL
BEGIN
    DROP TABLE ExcludedPayloadAttribute
    IF OBJECT_ID('ExcludedPayloadAttribute') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE ExcludedPayloadAttribute >>>'
    ELSE
        PRINT '<<< DROPPED TABLE ExcludedPayloadAttribute >>>'
END
go
IF OBJECT_ID('ExcludedPayload') IS NOT NULL
BEGIN
    DROP TABLE ExcludedPayload
    IF OBJECT_ID('ExcludedPayload') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE ExcludedPayload >>>'
    ELSE
        PRINT '<<< DROPPED TABLE ExcludedPayload >>>'
END
go
IF OBJECT_ID('ExcludedEvent') IS NOT NULL
BEGIN
    DROP TABLE ExcludedEvent
    IF OBJECT_ID('ExcludedEvent') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE ExcludedEvent >>>'
    ELSE
        PRINT '<<< DROPPED TABLE ExcludedEvent >>>'
END
go
CREATE TABLE ExcludedEvent
(
    Id                 numeric(19,0) IDENTITY,
    ModuleName         varchar(255)  NOT NULL,
    FlowName           varchar(255)  NOT NULL,
    ExclusionTime      datetime      NOT NULL,
    EventId            varchar(255)  NOT NULL,
    Priority        int  NULL,
    Timestamp       datetime  NULL,
    ResubmissionTime       datetime  NULL,
    Resubmitter        varchar(255) NULL,
    CONSTRAINT ExcludedEv_125240472
    PRIMARY KEY CLUSTERED (Id)

)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go
IF OBJECT_ID('ExcludedEvent') IS NOT NULL
    PRINT '<<< CREATED TABLE ExcludedEvent >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE ExcludedEvent >>>'
go




CREATE TABLE ExcludedPayload
(
    PersistenceId   numeric(19,0)  IDENTITY,
    PayloadId       varchar(255)   NULL,
    Content         image          NULL,
    ExcludedEventId numeric(19,0)  NULL,
    PayloadPosition int            NULL,
    CONSTRAINT ExcludedPa_445241612
    PRIMARY KEY CLUSTERED (PersistenceId)
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
go
IF OBJECT_ID('ExcludedPayload') IS NOT NULL
    PRINT '<<< CREATED TABLE ExcludedPayload >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE ExcludedPayload >>>'
go

ALTER TABLE ExcludedPayload
    ADD CONSTRAINT FK54371C4438A8DA2F
    FOREIGN KEY (ExcludedEventId)
    REFERENCES ExcludedEvent (Id)
go



CREATE TABLE ExcludedPayloadAttribute
(
    ExcludedPayloadId numeric(19,0) NOT NULL,
    AttributeValue    varchar(255)  NOT NULL,
    AttributeName     varchar(255)  NOT NULL,
    CONSTRAINT ExcludedPa_8730511152
    PRIMARY KEY CLUSTERED (ExcludedPayloadId,AttributeName)
    WITH RESERVEPAGEGAP=0,
         MAX_ROWS_PER_PAGE=0
)
LOCK DATAROWS
go
IF OBJECT_ID('ExcludedPayloadAttribute') IS NOT NULL
    PRINT '<<< CREATED TABLE ExcludedPayloadAttribute >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE ExcludedPayloadAttribute >>>'
go
ALTER TABLE ExcludedPayloadAttribute
    ADD CONSTRAINT FKDDC43AB89439F4DD
    FOREIGN KEY (ExcludedPayloadId)
    REFERENCES ExcludedPayload (PersistenceId)
go

