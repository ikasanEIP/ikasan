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

-- ====================================================================
-- Sample webconsole users population SQL
--
-- Purpose: This file creates various user security data entries for Ikasan Web.
--
-- Author:  Ikasan Development Team
-- 
-- ====================================================================

begin

-- populate sample authorities
INSERT INTO dbo.Authorities ( Authority, Description ) 
         VALUES ( 'ROLE_USER', 'Users who may log into the system' ) 
INSERT INTO dbo.Authorities ( Authority, Description ) 
         VALUES ( 'ROLE_ADMIN', 'Users who may perform administration functions on the system' ) 


-- populate sample user entries
declare @authorityUserId NUMERIC
declare @authorityAdminId NUMERIC
declare @userId NUMERIC
declare @username VARCHAR(50)

select @authorityUserId = Id from dbo.Authorities where Authority = 'ROLE_USER'
select @authorityAdminId = Id from dbo.Authorities where Authority = 'ROLE_ADMIN'

set @username='duncro'
INSERT INTO dbo.Users ( Username, Password, Enabled ) 
         VALUES ( @username, '5a46b8253d07320a14cace9b4dcbf80f93dcef04', 1 ) 
select @userId = Id from dbo.Users where Username = @username
INSERT INTO dbo.UsersAuthorities values ( @userId, @authorityUserId ) 
INSERT INTO dbo.UsersAuthorities values ( @userId, @authorityAdminId ) 

set @username='verbma'
INSERT INTO dbo.Users ( Username, Password, Enabled ) 
         VALUES ( @username, '5a46b8253d07320a14cace9b4dcbf80f93dcef04', 1 ) 
select @userId = Id from dbo.Users where Username = @username
INSERT INTO dbo.UsersAuthorities values ( @userId, @authorityUserId ) 
INSERT INTO dbo.UsersAuthorities values ( @userId, @authorityAdminId ) 

set @username='hasasu'
INSERT INTO dbo.Users ( Username, Password, Enabled ) 
         VALUES ( @username, '5a46b8253d07320a14cace9b4dcbf80f93dcef04', 1 ) 
select @userId = Id from dbo.Users where Username = @username
INSERT INTO dbo.UsersAuthorities values ( @userId, @authorityUserId ) 
INSERT INTO dbo.UsersAuthorities values ( @userId, @authorityAdminId ) 

set @username='mitcje'
INSERT INTO dbo.Users ( Username, Password, Enabled ) 
         VALUES ( @username, '5a46b8253d07320a14cace9b4dcbf80f93dcef04', 1 ) 
select @userId = Id from dbo.Users where Username = @username
INSERT INTO dbo.UsersAuthorities values ( @userId, @authorityUserId ) 
INSERT INTO dbo.UsersAuthorities values ( @userId, @authorityAdminId ) 

set @username='suetju'
INSERT INTO dbo.Users ( Username, Password, Enabled ) 
         VALUES ( @username, '5a46b8253d07320a14cace9b4dcbf80f93dcef04', 1 ) 
select @userId = Id from dbo.Users where Username = @username
INSERT INTO dbo.UsersAuthorities values ( @userId, @authorityUserId ) 
INSERT INTO dbo.UsersAuthorities values ( @userId, @authorityAdminId ) 

end
go

