      MERGE INTO Users ( Username, Password, Enabled, FirstName, Surname,PreviousAccess )
			KEY (Username)
					VALUES ('admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', 1 , 'Admin', 'User',10);

      MERGE INTO Users ( Username, Password, Enabled, FirstName, Surname,PreviousAccess )
			KEY (Username)
							VALUES ('api', 'd033e22ae348aeb5660fc2140aec35850c4da997', 1 , 'API', 'API',10);

      MERGE INTO SecurityPrincipal (Name , PrincipalType, Description, CreatedDateTime ,UpdatedDateTime)
      KEY (Name)
        VALUES ( 'admin','Admin user', 'The administrator user' ,'1970-01-01 00:00:00','1970-01-01 00:00:00');

      MERGE INTO SecurityPrincipal (Name , PrincipalType, Description, CreatedDateTime ,UpdatedDateTime)
      KEY (Name)
        VALUES ( 'user','user', 'The user' ,'1970-01-01 00:00:00','1970-01-01 00:00:00');

      MERGE INTO SecurityRole (Name ,  Description, CreatedDateTime ,UpdatedDateTime)
      KEY (Name)
        VALUES ( 'ADMIN', 'Users who may perform administration functions on the system','1970-01-01 00:00:00','1970-01-01 00:00:00' );

      MERGE INTO SecurityPolicy (Name ,  Description, CreatedDateTime ,UpdatedDateTime )
      KEY (Name)
        VALUES ( 'ALL', 'Policy to do everything','1970-01-01 00:00:00','1970-01-01 00:00:00' );

      MERGE INTO SecurityPolicy (Name ,  Description, CreatedDateTime ,UpdatedDateTime )
        KEY (Name)
        VALUES ( 'ReadBlueConsole', 'Policy to do view BlueConsole','1970-01-01 00:00:00','1970-01-01 00:00:00' );

      MERGE INTO SecurityPolicy (Name ,  Description, CreatedDateTime ,UpdatedDateTime )
        KEY (Name)
        VALUES ( 'WriteBlueConsole', 'Policy to do write BlueConsole','1970-01-01 00:00:00','1970-01-01 00:00:00' );


			MERGE INTO UserPrincipal ( UserId, PrincipalId )
			 KEY (UserId,PrincipalId)
			 (SELECT u.Id, s.Id
			  FROM Users u, SecurityPrincipal s
			  WHERE
			  	   u.Username = 'admin'
			  	   and s.Name = 'admin'
			  );

			 MERGE INTO UserPrincipal ( UserId, PrincipalId )
			 KEY (UserId,PrincipalId)
			 (SELECT u.Id, s.Id
			  FROM Users u, SecurityPrincipal s
			  WHERE
			  	   u.Username = 'admin'
			  	   and s.Name = 'user'
			  );

			 MERGE  INTO PrincipalRole ( PrincipalId, RoleId )
			  KEY (PrincipalId, RoleId)
			 (SELECT s.Id, r.Id
			 FROM
			 	SecurityPrincipal s, SecurityRole r
			 WHERE
			 	s.Name = 'admin'
			 	and r.Name = 'ADMIN'
			 );


			 MERGE INTO RolePolicy ( RoleId, PolicyId )
			 KEY(RoleId, PolicyId)
			 (SELECT r.Id, p.Id
			 FROM
			 	SecurityRole r , SecurityPolicy p
			 WHERE
			 	r.Name = 'ADMIN'
			 	and p.Name = 'ALL'
			 );

			MERGE INTO RolePolicy ( RoleId, PolicyId )
			 KEY(RoleId, PolicyId)
			 (SELECT r.Id, p.Id
			 FROM
			 	SecurityRole r , SecurityPolicy p
			 WHERE
			 	r.Name = 'ADMIN'
			 	and p.Name = 'ReadBlueConsole'
			 );

      MERGE INTO RolePolicy ( RoleId, PolicyId )
			 KEY(RoleId, PolicyId)
			 (SELECT r.Id, p.Id
			 FROM
			 	SecurityRole r , SecurityPolicy p
			 WHERE
			 	r.Name = 'ADMIN'
			 	and p.Name = 'WriteBlueConsole'
			 );

    ALTER TABLE IkasanWiretap ALTER COLUMN PayloadContent TYPE text;
    ALTER TABLE ErrorOccurrence ALTER COLUMN EventAsString TYPE text;
    ALTER TABLE ErrorOccurrence ALTER COLUMN ErrorDetail TYPE text;
    ALTER TABLE ErrorOccurrence ALTER COLUMN ErrorMessage TYPE text;
    ALTER TABLE ErrorOccurrence ALTER COLUMN Event TYPE BLOB;
    ALTER TABLE ExclusionEvent ALTER COLUMN Event TYPE BLOB;