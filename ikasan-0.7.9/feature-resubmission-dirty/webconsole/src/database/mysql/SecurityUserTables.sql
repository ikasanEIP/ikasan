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

DROP TABLE IF EXISTS `Ikasan01`.`UsersAuthorities`;
DROP TABLE IF EXISTS `Ikasan01`.`Authorities`;
DROP TABLE IF EXISTS `Ikasan01`.`Users`;

CREATE TABLE  `Ikasan01`.`Users` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Username` varchar(255) DEFAULT NULL,
  `Password` varchar(255) NOT NULL,
  `Enabled` bit(1) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Username` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE  `Ikasan01`.`Authorities` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Authority` varchar(255) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Authority` (`Authority`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;


CREATE TABLE  `Ikasan01`.`UsersAuthorities` (
  `UserId` bigint(20) NOT NULL,
  `AuthorityId` bigint(20) NOT NULL,
  PRIMARY KEY (`UserId`,`AuthorityId`),
  KEY `FK991EDC1922123CD1` (`UserId`),
  KEY `FK991EDC19ACF38401` (`AuthorityId`),
  CONSTRAINT `FK991EDC19ACF38401` FOREIGN KEY (`AuthorityId`) REFERENCES `Authorities` (`Id`),
  CONSTRAINT `FK991EDC1922123CD1` FOREIGN KEY (`UserId`) REFERENCES `Users` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;