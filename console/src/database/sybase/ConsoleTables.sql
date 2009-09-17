--
-- $Id$
-- $URL$
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

IF OBJECT_ID('Module') IS NOT NULL
BEGIN
    DROP TABLE Module
    IF OBJECT_ID('Module') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE Module >>>'
    ELSE
        PRINT '<<< DROPPED TABLE Module >>>'     
END
GO

IF OBJECT_ID('PointToPointFlow') IS NOT NULL
BEGIN
    DROP TABLE PointToPointFlow
    IF OBJECT_ID('PointToPointFlow') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE PointToPointFlow >>>'
    ELSE
        PRINT '<<< DROPPED TABLE PointToPointFlow >>>'     
END
GO

IF OBJECT_ID('PointToPointFlowProfile') IS NOT NULL
BEGIN
    DROP TABLE PointToPointFlowProfile
    IF OBJECT_ID('PointToPointFlowProfile') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE PointToPointFlowProfile >>>'
    ELSE
        PRINT '<<< DROPPED TABLE PointToPointFlowProfile >>>'     
END
GO

CREATE TABLE Module(
    Id          NUMERIC IDENTITY NOT NULL PRIMARY KEY,
    Name        VARCHAR(255) NOT NULL,
    Description VARCHAR(255) NOT NULL
)
GO

CREATE TABLE PointToPointFlowProfile(
    Id          NUMERIC IDENTITY NOT NULL PRIMARY KEY,
    Name        VARCHAR(255) NOT NULL
)
GO

-- No CONSTRAINT on the FromModuleId or ToModuleId as constraints enforce a Not NULL
-- and we want to allow NULLs
CREATE TABLE PointToPointFlow(
    Id                        NUMERIC IDENTITY NOT NULL PRIMARY KEY,
    PointToPointFlowProfileId NUMERIC NOT NULL,
    FromModuleId              NUMERIC NULL,
    ToModuleId                NUMERIC NULL,
    CONSTRAINT PTPP_ID_FK FOREIGN KEY(PointToPointFlowProfileId) REFERENCES PointToPointFlowProfile(Id)
)
GO
