--
-- $Id: FlowEventTrigger.sql 16808 2009-04-27 07:28:17Z mitcje $
-- $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/database/FlowEventTrigger.sql $
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

-- Drop tables
IF OBJECT_ID('FlowEventTriggerParameters') IS NOT NULL
BEGIN
    DROP TABLE FlowEventTriggerParameters
    IF OBJECT_ID('FlowEventTriggerParameters') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE FlowEventTriggerParameters >>>'
    ELSE
        PRINT '<<< DROPPED TABLE FlowEventTriggerParameters >>>'
END
IF OBJECT_ID('FlowEventTrigger') IS NOT NULL
BEGIN
    DROP TABLE FlowEventTrigger
    IF OBJECT_ID('FlowEventTrigger') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE FlowEventTrigger >>>'
    ELSE
        PRINT '<<< DROPPED TABLE FlowEventTrigger >>>'
END


-- FlowEventTrigger
CREATE TABLE FlowEventTrigger
(
    Id                  NUMERIC IDENTITY NOT NULL,
    ModuleName          VARCHAR(128)  NOT NULL,
    FlowName            VARCHAR(128)  NOT NULL,
    Relationship        VARCHAR(32)  NOT NULL,
    FlowElementName     VARCHAR(128),
    JobName           	VARCHAR(64)   NOT NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE UNIQUE INDEX FlowEventTrigger01u ON FlowEventTrigger(Id)

IF OBJECT_ID('FlowEventTrigger') IS NOT NULL
    PRINT '<<< CREATED TABLE FlowEventTrigger >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE FlowEventTrigger >>>'
    



-- FlowEventTriggerParameters
CREATE TABLE FlowEventTriggerParameters
(
    TriggerId          NUMERIC NOT NULL,
    ParamName          VARCHAR(128)  NOT NULL,
    ParamValue            VARCHAR(128) 
)
  ALTER TABLE FlowEventTriggerParameters
    ADD CONSTRAINT FlowEventTriggerParam_Id_FK
    FOREIGN KEY (TriggerId)
    REFERENCES FlowEventTrigger (Id)  
IF OBJECT_ID('FlowEventTrigger') IS NOT NULL
    PRINT '<<< CREATED TABLE FlowEventTriggerParameters >>>'
ELSE
    PRINT '<<< FAILED CREATING TABLE FlowEventTriggerParameters >>>'
    
  
    

