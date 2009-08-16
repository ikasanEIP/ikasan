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

DROP TABLE IF EXISTS `Ikasan01`.`FlowEventTriggerParameters`;
DROP TABLE IF EXISTS `Ikasan01`.`FlowEventTrigger`;
CREATE TABLE  `Ikasan01`.`FlowEventTrigger` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ModuleName` varchar(255) NOT NULL,
  `FlowName` varchar(255) NOT NULL,
  `Relationship` varchar(255) NOT NULL,
  `FlowElementName` varchar(255) DEFAULT NULL,
  `JobName` varchar(255) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE  `Ikasan01`.`FlowEventTriggerParameters` (
  `TriggerId` bigint(20) NOT NULL,
  `ParamValue` varchar(255) DEFAULT NULL,
  `ParamName` varchar(255) NOT NULL,
  PRIMARY KEY (`TriggerId`,`ParamName`),
  KEY `FK71FA5536C7DD805` (`TriggerId`),
  CONSTRAINT `FK71FA5536C7DD805` FOREIGN KEY (`TriggerId`) REFERENCES `FlowEventTrigger` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;