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
DROP TABLE IF EXISTS `Ikasan01`.`ExcludedPayloadAttribute`;
DROP TABLE IF EXISTS `Ikasan01`.`ExcludedPayload`;
DROP TABLE IF EXISTS `Ikasan01`.`ExcludedEvent`;


CREATE TABLE `Ikasan01`.`ExcludedEvent`
(
    `Id`              bigint(20)   NOT NULL AUTO_INCREMENT,
    `ModuleName`      varchar(128) NOT NULL,
    `FlowName`        varchar(128) DEFAULT NULL,
    `ExclusionTime`   datetime     NOT NULL,
    `EventId`         varchar(255) NOT NULL,
    `Priority`        int          DEFAULT NULL,
    `Timestamp`       datetime     DEFAULT NULL,
    `LastUpdatedTime` datetime     DEFAULT NULL,
    `LastUpdatedBy`   varchar(255) DEFAULT NULL,
    `Resolution`      varchar(128) DEFAULT NULL,
     PRIMARY KEY (`Id`)

)
ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `Ikasan01`.`ExcludedPayload`
(
    `PersistenceId`   bigint(20)    NOT NULL AUTO_INCREMENT,
    `PayloadId`       varchar(255)  DEFAULT NULL,
    `Content`         longtext      DEFAULT NULL,
    `ExcludedEventId` bigint(20)    DEFAULT NULL,
    `PayloadPosition` int           DEFAULT NULL,
     PRIMARY KEY (`PersistenceId`),
     FOREIGN KEY (`ExcludedEventId`) REFERENCES ExcludedEvent (`Id`)
)
ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE ExcludedPayloadAttribute
(
    `ExcludedPayloadId` bigint(20)    NOT NULL,
    `AttributeValue`    varchar(255)  NOT NULL,
    `AttributeName`     varchar(255)  NOT NULL,
     PRIMARY KEY (`ExcludedPayloadId`, `AttributeName`),
     FOREIGN KEY (`ExcludedPayloadId`) REFERENCES ExcludedPayload (`PersistenceId`)

)
ENGINE=InnoDB DEFAULT CHARSET=latin1;
