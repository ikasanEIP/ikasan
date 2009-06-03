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

IF OBJECT_ID('UsersAuthorities') IS NOT NULL
BEGIN
    DROP TABLE UsersAuthorities
    IF OBJECT_ID('UsersAuthorities') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE UsersAuthorities >>>'
    ELSE
        PRINT '<<< DROPPED TABLE UsersAuthorities >>>'     
END
GO


IF OBJECT_ID('Authorities') IS NOT NULL
BEGIN
    DROP TABLE Authorities
    IF OBJECT_ID('Authorities') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE Authorities >>>'
    ELSE
        PRINT '<<< DROPPED TABLE Authorities >>>'     
END
GO

IF OBJECT_ID('Users') IS NOT NULL
BEGIN
    DROP TABLE Users
    IF OBJECT_ID('Users') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE Users >>>'
    ELSE
        PRINT '<<< DROPPED TABLE Users >>>'     
END
GO




CREATE TABLE Users(
    Id       NUMERIC IDENTITY NOT NULL PRIMARY KEY,
    Username VARCHAR(50) NOT NULL UNIQUE,
    Password VARCHAR(50) NOT NULL,
    Enabled  BIT NOT NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
GO


CREATE TABLE Authorities (
    Id          NUMERIC IDENTITY NOT NULL PRIMARY KEY,
    Authority   VARCHAR(50) NOT NULL UNIQUE,
    Description VARCHAR(512)
)
LOCK DATAROWS
WITH IDENTITY_GAP=1
GO

CREATE TABLE UsersAuthorities (
    UserId NUMERIC NOT NULL,
    AuthorityId NUMERIC NOT NULL,
    PRIMARY KEY (UserId,AuthorityId),
    CONSTRAINT USER_AUTH_USER_FK FOREIGN KEY(UserId) REFERENCES Users(Id),
    CONSTRAINT USER_AUTH_AUTH_FK FOREIGN KEY(AuthorityId) REFERENCES Authorities(Id)
)     
LOCK DATAROWS
WITH IDENTITY_GAP=1
GO    
   
