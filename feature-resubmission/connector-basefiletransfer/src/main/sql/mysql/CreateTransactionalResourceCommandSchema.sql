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


-- drop all the specialist command table
DROP TABLE IF EXISTS `eai`.`checksumcommand`;
DROP TABLE IF EXISTS `eai`.`cleanupchunkscommand`;
DROP TABLE IF EXISTS `eai`.`deliverbatchcommand`;
DROP TABLE IF EXISTS `eai`.`deliverfilecommand`;
DROP TABLE IF EXISTS `eai`.`retrievefilecommand`;

-- drop the parent command table
DROP TABLE IF EXISTS `eai`.`transactionalresourcecommand`;

-- drop the XID table
DROP TABLE IF EXISTS `eai`.`xid`;

-- create the XID table
CREATE TABLE  `eai`.`xid` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `State` varchar(255) NOT NULL,
  `GlobalTransactionId` varchar(255) NOT NULL,
  `BranchQualifier` varchar(255) NOT NULL,
  `FormatId` int(11) NOT NULL,
  `ClientId` varchar(255) NOT NULL,
  `CreatedDateTime` datetime NOT NULL,
  `LastUpdatedDateTime` datetime NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- create the parent command table
CREATE TABLE  `eai`.`transactionalresourcecommand` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Type` varchar(255) NOT NULL,
  `State` varchar(255) NOT NULL,
  `Xid_Id` bigint(20) DEFAULT NULL,
  `ExecutionTimestamp` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK9936B0B44985F7BE` (`Xid_Id`),
  CONSTRAINT `FK9936B0B44985F7BE` FOREIGN KEY (`Xid_Id`) REFERENCES `xid` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



-- create the checksum command table
CREATE TABLE  `eai`.`checksumcommand` (
  `Id` bigint(20) NOT NULL,
  `Destructive` bit(1) DEFAULT NULL,
  `ChecksumFilePath` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKEC851168692F9C06` (`Id`),
  CONSTRAINT `FKEC851168692F9C06` FOREIGN KEY (`Id`) REFERENCES `transactionalresourcecommand` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- create the cleanup chunks command table
CREATE TABLE  `eai`.`cleanupchunkscommand` (
  `Id` bigint(20) NOT NULL,
  `FileChunkHeaderId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK1F4EFCA17E0A0E0B` (`Id`),
  CONSTRAINT `FK1F4EFCA17E0A0E0B` FOREIGN KEY (`Id`) REFERENCES `transactionalresourcecommand` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- create the deliver batch command table
CREATE TABLE  `eai`.`deliverbatchcommand` (
  `Id` bigint(20) NOT NULL,
  `OutputDirectory` varchar(255) DEFAULT NULL,
  `TempDirectory` varchar(255) DEFAULT NULL,
  `BatchFolder` varchar(255) DEFAULT NULL,
  `PutAttempted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKB6D580566F914342` (`Id`),
  CONSTRAINT `FKB6D580566F914342` FOREIGN KEY (`Id`) REFERENCES `transactionalresourcecommand` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



-- create the deliver file command table
CREATE TABLE  `eai`.`deliverfilecommand` (
  `Id` bigint(20) NOT NULL,
  `FileName` varchar(255) DEFAULT NULL,
  `TempFileName` varchar(255) DEFAULT NULL,
  `OutputDirectory` varchar(255) DEFAULT NULL,
  `OverwriteExisting` bit(1) DEFAULT NULL,
  `PutAttempted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FKBE913FEAE58F1914` (`Id`),
  CONSTRAINT `FKBE913FEAE58F1914` FOREIGN KEY (`Id`) REFERENCES `transactionalresourcecommand` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- create the retrieve file command table
CREATE TABLE  `eai`.`retrievefilecommand` (
  `Id` bigint(20) NOT NULL,
  `SourcePath` varchar(255) DEFAULT NULL,
  `RenameOnSuccess` bit(1) DEFAULT NULL,
  `RenameExtension` varchar(255) DEFAULT NULL,
  `MoveOnSuccess` bit(1) DEFAULT NULL,
  `MoveNewPath` varchar(255) DEFAULT NULL,
  `Destructive` bit(1) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK6C459033E706058A` (`Id`),
  KEY `FK6C4590332501531F` (`Id`),
  CONSTRAINT `FK6C4590332501531F` FOREIGN KEY (`Id`) REFERENCES `transactionalresourcecommand` (`Id`),
  CONSTRAINT `FK6C459033E706058A` FOREIGN KEY (`Id`) REFERENCES `transactionalresourcecommand` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;