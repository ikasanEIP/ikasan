	declare @authorityUserId numeric(18),
	@authorityAdminId numeric(18), @username varchar(50), @userId
	numeric(18)

	select @authorityUserId = Id from Authorities where
	Authority =
	'ROLE_USER'
	select @authorityAdminId = Id from
	Authorities where Authority =
	'ROLE_ADMIN'

	select @username = 'admin'

	INSERT INTO Users ( Username, Password, Enabled, FirstName, Surname )
	VALUES (
	@username, 'd033e22ae348aeb5660fc2140aec35850c4da997', 1 , 'Admin', 'User')
	select
	@userId = Id from Users where Username = @username

	INSERT INTO
	UsersAuthorities values ( @userId, @authorityUserId )
	INSERT INTO
	UsersAuthorities values ( @userId, @authorityAdminId )
	
	declare @securityPrincipalId numeric(18),
	@securityRoleId numeric(18), @securityPolicyId numeric(18)

	INSERT INTO 
	SecurityPrincipal ( Name, PrincipalType, Description )
	VALUES ( 'admin', 'user', 'This administrator user')

	select @securityPrincipalId = Id 
	from SecurityPrincipal 
	where Name = 'admin'

	SELECT @userId = Id from Users where Username = 'admin'

	INSERT
	INTO UserPrincipal ( UserId, PrincipalId )
	VALUES ( @userId, @securityPrincipalId )

	INSERT
	INTO SecurityRole ( Name, Description )
	VALUES ( 'Administrator',
	'Users who may perform administration functions on
	the system' )

	select @securityRoleId = Id 
	from SecurityRole 
	where Name = 'Administrators'

	INSERT
	INTO SecurityPolicy ( Name, Description )
	VALUES ( 'ALL', 'Policy to do everything' )

	select @securityPolicyId = Id 
	from SecurityPolicy 
	where Name = 'ALL'

	INSERT
	INTO PrincipalRole ( PrincipalId, RoleId )
	VALUES ( @securityPrincipalId, @securityRoleId )

	INSERT
	INTO SecurityRole ( Name, Description )
	VALUES ( 'User',
	'Users who have a read only view on the system' )

	select @securityRoleId = Id 
	from SecurityRole 
	where Name = 'User'

	INSERT
	INTO SecurityPolicy ( Name, Description )
	VALUES ( 'Read Only', 'Read only policy' )

	select @securityPolicyId = Id 
	from SecurityPolicy 
	where Name = 'Read Only'

	INSERT
	INTO RolePolicy ( RoleId, PolicyId )
	VALUES ( @securityRoleId,  @securityPolicyId )

	INSERT
	INTO SecurityRole ( Name, Description )
	VALUES ( 'COP Stream Administrator',
	'Cash Operations Stream Administrators' )

	select @securityRoleId = Id 
	from SecurityRole 
	where Name = 'COP Stream Administrator'

	select @securityPolicyId = Id 
	from SecurityPolicy 
	where Name = 'Read Only'

	INSERT
	INTO RolePolicy ( RoleId, PolicyId )
	VALUES ( @securityRoleId,  @securityPolicyId )

	INSERT
	INTO SecurityPolicy ( Name, Description )
	VALUES ( 'COP Control', 'Ability to control COP flows' )

	select @securityPolicyId = Id 
	from SecurityPolicy 
	where Name = 'COP Control'

	INSERT
	INTO RolePolicy ( RoleId, PolicyId )
	VALUES ( @securityRoleId,  @securityPolicyId )

	INSERT
	INTO SecurityPolicy ( Name, Description )
	VALUES ( 'COP Wiretap', 'Ability to view COP wiretaps' )

	select @securityPolicyId = Id 
	from SecurityPolicy 
	where Name = 'COP Wiretap'

	INSERT
	INTO RolePolicy ( RoleId, PolicyId )
	VALUES ( @securityRoleId,  @securityPolicyId )

	INSERT
	INTO SecurityPolicy ( Name, Description )
	VALUES ( 'COP Errors', 'View COP errors' )

	select @securityPolicyId = Id 
	from SecurityPolicy 
	where Name = 'COP Errors'

	INSERT
	INTO RolePolicy ( RoleId, PolicyId )
	VALUES ( @securityRoleId,  @securityPolicyId )

	INSERT
	INTO SecurityPolicy ( Name, Description )
	VALUES ( 'COP Replay', 'Policy to replay COP data' )

	select @securityPolicyId = Id 
	from SecurityPolicy 
	where Name = 'COP Replay'

	INSERT
	INTO RolePolicy ( RoleId, PolicyId )
	VALUES ( @securityRoleId,  @securityPolicyId )

	INSERT
	INTO SecurityPolicy ( Name, Description )
	VALUES ( 'COP Hospital', 'Policy to use the hospital for COP data' )

	select @securityPolicyId = Id 
	from SecurityPolicy 
	where Name = 'COP Hospital'

	INSERT
	INTO RolePolicy ( RoleId, PolicyId )
	VALUES ( @securityRoleId,  @securityPolicyId )

	INSERT
	INTO SecurityRole ( Name, Description )
	VALUES ( 'COP Stream User',
	'Cash Operations Stream User' )

	select @securityRoleId = Id 
	from SecurityRole 
	where Name = 'COP Stream User'

	select @securityPolicyId = Id 
	from SecurityPolicy 
	where Name = 'COP Wiretap'

	INSERT
	INTO RolePolicy ( RoleId, PolicyId )
	VALUES ( @securityRoleId,  @securityPolicyId )


	select @securityPolicyId = Id 
	from SecurityPolicy 
	where Name = 'COP Errors'

	INSERT
	INTO RolePolicy ( RoleId, PolicyId )
	VALUES ( @securityRoleId,  @securityPolicyId )

	INSERT
	INTO PolicyLinkType ( Name, TableName )
	VALUES ( 'Mapping Configuration',  'MCSMappingConfiguration' )

	INSERT INTO AuthenticationMethod (Id, Method)
	VALUES (1, 'AUTH_METHOD_LOCAL')

	INSERT INTO AuthenticationMethod (Id, Method)
	VALUES (1, 'AUTH_METHOD_LOCAL')
	
	set identity_insert PlatformConfiguration on

 insert into PlatformConfiguration (
   Id
  ,Name
  ,[Value]
  ,CreatedDateTime
  ,UpdatedDateTime
) VALUES (
   1   -- Id - IN numeric(18, 0)
  ,'mappingExportSchemaLocation'  -- Name - IN varchar(256)
  ,'http://svc-stewmi:8080/ikasan-dashboard/static/org/ikasan/dashboard/mappingConfigurationImportExport.xsd'  -- Value - IN varchar(256)
  ,getDate()  -- CreatedDateTime - IN datetime
  ,getDate()  -- UpdatedDateTime - IN datetime
)

insert into PlatformConfiguration (
   Id
  ,Name
  ,[Value]
  ,CreatedDateTime
  ,UpdatedDateTime
) VALUES (
   2   -- Id - IN numeric(18, 0)
  ,'mappingValuesExportSchemaLocation'  -- Name - IN varchar(256)
  ,'http://svc-stewmi:8080/ikasan-dashboard/static/org/ikasan/dashboard/mappingConfigurationValuesImportExport.xsd'  -- Value - IN varchar(256)
  ,getDate()  -- CreatedDateTime - IN datetime
  ,getDate()  -- UpdatedDateTime - IN datetime
)

set identity_insert PlatformConfiguration off

commit