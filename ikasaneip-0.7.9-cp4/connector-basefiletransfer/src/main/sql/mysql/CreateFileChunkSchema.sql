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
DROP TABLE IF EXISTS `eai`.`filechunk`;
DROP TABLE IF EXISTS `eai`.`filechunkheader`;


CREATE TABLE  `eai`.`filechunkheader` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `SequenceLength` bigint(20) NOT NULL,
  `InternalMd5Hash` varchar(255) DEFAULT NULL,
  `ExternalMd5Hash` varchar(255) DEFAULT NULL,
  `FileName` varchar(255) NOT NULL,
  `ChunkTimeStamp` bigint(20) NOT NULL,
  `ClientId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE  `eai`.`filechunk` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Content` tinyblob NOT NULL,
  `Ordinal` bigint(20) NOT NULL,
  `Md5Hash` varchar(255) NOT NULL,
  `FileChunkHeader_Id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `FK276D6A31B5789524` (`FileChunkHeader_Id`),
  CONSTRAINT `FK276D6A31B5789524` FOREIGN KEY (`FileChunkHeader_Id`) REFERENCES `filechunkheader` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;