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
-- =============================================================================
--

--------------
-- Module Data
--------------

-- Modules - Wiretapped
INSERT INTO Module (Name, Description, DesignDiagramURL) VALUES ('demoFileDelivery-ftpSource', 'Deals with picking up files for demonstration purposes', NULL)
INSERT INTO Module (Name, Description, DesignDiagramURL) VALUES ('demoFileDelivery-ftpTarget', 'Deals with delivering files for demonstration purposes', NULL)
GO

-------------------------------
-- PointToPointFlowProfile Data
-------------------------------

INSERT INTO PointToPointFlowProfile (Name) VALUES ('Demonstration File Delivery')
GO

------------------------
------------------------
-- PointToPointFlow Data
------------------------
------------------------

------------------------------
-- Demonstration File Delivery
------------------------------

-- demoFileDelivery-ftpSource
INSERT INTO PointToPointFlow (PointToPointFlowProfileId, FromModuleId, ToModuleId) 
SELECT PointToPointFlowProfile.Id, NULL, Module.Id
FROM PointToPointFlowProfile, Module
WHERE PointToPointFlowProfile.Name = 'Demonstration File Delivery'
AND Module.Name = 'demoFileDelivery-ftpSource'

-- demoFileDelivery-ftpSource --> demoFileDelivery-ftpTarget
INSERT INTO PointToPointFlow (PointToPointFlowProfileId, FromModuleId, ToModuleId) 
SELECT PointToPointFlowProfile.Id, M1.Id, M2.Id
FROM PointToPointFlowProfile, Module M1, Module M2
WHERE PointToPointFlowProfile.Name = 'Demonstration File Delivery'
AND M1.Name = 'demoFileDelivery-ftpSource'
AND M2.Name = 'demoFileDelivery-ftpTarget'

-- demoFileDelivery-ftpTarget
INSERT INTO PointToPointFlow (PointToPointFlowProfileId, FromModuleId, ToModuleId) 
SELECT PointToPointFlowProfile.Id, Module.Id, NULL
FROM PointToPointFlowProfile, Module
WHERE PointToPointFlowProfile.Name = 'Demonstration File Delivery'
AND Module.Name = 'demoFileDelivery-ftpTarget'
GO