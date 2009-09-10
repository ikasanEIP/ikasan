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
-- Example script for creating admin users and assining both user and admin roles 
--
-- Assumes that the ROLE_USER and ROLE_ADMIN roles are already created

select @authorityUserId:= Id from Authorities where Authority = 'ROLE_USER';
select @authorityAdminId:= Id from Authorities where Authority = 'ROLE_ADMIN';


select @username:=<username here>;



INSERT INTO Users ( username, Password, Enabled )
         VALUES ( @username, <sha1 hashed password here>, 1 ) ;
select @userId:= Id from Users where Username = @username;

INSERT INTO UsersAuthorities values ( @userId, @authorityUserId );
INSERT INTO UsersAuthorities values ( @userId, @authorityAdminId );