--
-- $Id$
-- $URL$
-- 
-- ====================================================================
-- Ikasan Enterprise Integration Platform
-- 
-- Distributed under the Modified BSD License.
-- Copyright notice: The copyright for this software and a full listing 
-- of individual contributors are as shown in the packaged copyright.txt 
-- file. 
-- 
-- All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without 
-- modification, are permitted provided that the following conditions are met:
--
--  - Redistributions of source code must retain the above copyright notice, 
--    this list of conditions and the following disclaimer.
--
--  - Redistributions in binary form must reproduce the above copyright notice, 
--    this list of conditions and the following disclaimer in the documentation 
--    and/or other materials provided with the distribution.
--
--  - Neither the name of the ORGANIZATION nor the names of its contributors may
--    be used to endorse or promote products derived from this software without 
--    specific prior written permission.
--
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
-- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
-- IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
-- DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
-- FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
-- DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
-- SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
-- CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
-- OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
-- USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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