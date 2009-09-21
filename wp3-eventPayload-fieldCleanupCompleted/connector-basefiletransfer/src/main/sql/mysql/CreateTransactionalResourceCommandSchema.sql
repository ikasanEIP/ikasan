-- 
-- ====================================================================
-- Ikasan Enterprise Integration Platform
-- Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
-- by the @authors tag. See the copyright.txt in the distribution for a
-- full listing of individual contributors.
--
-- This is free software; you can redistribute it and/or modify it
-- under the terms of the GNU Lesser General Public License as
-- published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This software is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this software; if not, write to the 
-- Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
-- or see the FSF site: http://www.fsfeurope.org/.
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