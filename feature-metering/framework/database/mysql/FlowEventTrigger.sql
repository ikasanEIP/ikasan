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

DROP TABLE IF EXISTS `ikasan01`.`floweventtriggerparameters`;
DROP TABLE IF EXISTS `ikasan01`.`floweventtrigger`;
CREATE TABLE  `ikasan01`.`floweventtrigger` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ModuleName` varchar(255) NOT NULL,
  `FlowName` varchar(255) NOT NULL,
  `Relationship` varchar(255) NOT NULL,
  `FlowElementName` varchar(255) DEFAULT NULL,
  `JobName` varchar(255) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE  `ikasan01`.`floweventtriggerparameters` (
  `TriggerId` bigint(20) NOT NULL,
  `ParamValue` varchar(255) DEFAULT NULL,
  `ParamName` varchar(255) NOT NULL,
  PRIMARY KEY (`TriggerId`,`ParamName`),
  KEY `FK71FA5536C7DD805` (`TriggerId`),
  CONSTRAINT `FK71FA5536C7DD805` FOREIGN KEY (`TriggerId`) REFERENCES `floweventtrigger` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;