--
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

DROP TABLE IF EXISTS `ikasan01`.`Module`;

CREATE TABLE  `ikasan01`.`Module` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) NOT NULL,
  `Description` varchar(255) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE  `ikasan01`.`PointToPointFlowProfile` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE  `ikasan01`.`PointToPointFlow` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  KEY 'PTPP_ID_FK' (`PointToPointFlowProfileId`) NOT NULL,
  KEY 'FROM_MODULE_ID_FK' (`FromModuleId`) DEFAULT NULL,
  KEY 'TO_MODULE_ID_FK' (`ToModuleId`) DEFAULT NULL,
  CONSTRAINT `PTPP_ID_FK` FOREIGN KEY (`PointToPointFlowProfileId`) REFERENCES `PointToPointFlowProfile` (`Id`),
  CONSTRAINT `FROM_MODULE_ID_FK` FOREIGN KEY (`FromModuleId`) REFERENCES `Module` (`Id`),
  CONSTRAINT `TO_MODULE_ID_FK` FOREIGN KEY (`ToModuleId`) REFERENCES `Module` (`Id`),
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
