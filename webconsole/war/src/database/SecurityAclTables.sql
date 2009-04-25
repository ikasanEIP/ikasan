--
-- $Id: SecurityAclTables.sql 16798 2009-04-24 14:12:09Z mitcje $
-- $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/webconsole/war/src/database/SecurityAclTables.sql $
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

IF OBJECT_ID('acl_entry') IS NOT NULL
BEGIN
    DROP TABLE acl_entry
    IF OBJECT_ID('acl_entry') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE acl_entry >>>'
    ELSE
        PRINT '<<< DROPPED TABLE acl_entry >>>'     
END
GO


IF OBJECT_ID('acl_object_identity') IS NOT NULL
BEGIN
    DROP TABLE acl_object_identity
    IF OBJECT_ID('acl_object_identity') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE acl_object_identity >>>'
    ELSE
        PRINT '<<< DROPPED TABLE acl_object_identity >>>'     
END
GO


IF OBJECT_ID('acl_class') IS NOT NULL
BEGIN
    DROP TABLE acl_class
    IF OBJECT_ID('acl_class') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE acl_class >>>'
    ELSE
        PRINT '<<< DROPPED TABLE acl_class >>>'     
END
GO


IF OBJECT_ID('acl_sid') IS NOT NULL
BEGIN
    DROP TABLE acl_sid
    IF OBJECT_ID('acl_sid') IS NOT NULL
        PRINT '<<< FAILED DROPPING TABLE acl_sid >>>'
    ELSE
        PRINT '<<< DROPPED TABLE acl_sid >>>'     
END
GO


create table acl_sid (
  id  NUMERIC IDENTITY NOT NULL PRIMARY KEY, 
  principal BIT not null,
  sid VARCHAR(50) not null,
  --constraint unique_uk_1 unique(sid,principal) 
  );

create table acl_class (
  id NUMERIC IDENTITY NOT NULL PRIMARY KEY, 
  class VARCHAR(50) not null, 
  constraint unique_uk_2 unique(class) );

create table acl_object_identity (
  id NUMERIC IDENTITY NOT NULL PRIMARY KEY, 
  object_id_class NUMERIC not null, 
  object_id_identity NUMERIC not null, 
  parent_object NUMERIC NULL, 
  owner_sid NUMERIC NULL, 
  entries_inheriting BIT not null, 
  constraint unique_uk_3 unique(object_id_class,object_id_identity), 
  constraint foreign_fk_1 foreign key(parent_object)references acl_object_identity(id), 
  constraint foreign_fk_2 foreign key(object_id_class)references acl_class(id), 
  constraint foreign_fk_3 foreign key(owner_sid)references acl_sid(id) );

create table acl_entry ( 
  id NUMERIC IDENTITY NOT NULL PRIMARY KEY, 
  acl_object_identity NUMERIC not null,
  ace_order int not null,
  sid NUMERIC not null, 
  mask INT not null,
  granting BIT not null,
  audit_success BIT not null, 
  audit_failure BIT not null,
  constraint unique_uk_4 unique(acl_object_identity,ace_order), 
  constraint foreign_fk_4 foreign key(acl_object_identity) references acl_object_identity(id), 
  constraint foreign_fk_5 foreign key(sid) references acl_sid(id) );