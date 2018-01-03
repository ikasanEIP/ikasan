      INSERT INTO Users (Id, Username, Password, Enabled, FirstName, Surname,PreviousAccess )
					VALUES (1,'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', 1 , 'Admin', 'User',10);

      INSERT INTO Users (Id, Username, Password, Enabled, FirstName, Surname,PreviousAccess )
					VALUES (2,'api', 'd033e22ae348aeb5660fc2140aec35850c4da997', 1 , 'API', 'API',10);

      INSERT INTO SecurityPrincipal (Name , PrincipalType, Description, CreatedDateTime ,UpdatedDateTime)
        VALUES ( 'admin','Admin user', 'The administrator user' ,'1970-01-01 00:00:00','1970-01-01 00:00:00');

       INSERT INTO SecurityPolicy (Name ,  Description, CreatedDateTime ,UpdatedDateTime )
         VALUES ( 'WriteBlueConsole', 'Policy to do write BlueConsole','1970-01-01 00:00:00','1970-01-01 00:00:00' );


			 INSERT
			 INTO UserPrincipal ( UserId, PrincipalId )
			 (SELECT u.Id, s.Id
			  FROM Users u, SecurityPrincipal s
			  WHERE
			  	   u.Username = 'admin'
			  	   and s.Name = 'admin'
			  );

			 INSERT
			 INTO UserPrincipal ( UserId, PrincipalId )
			 (SELECT u.Id, s.Id
			  FROM Users u, SecurityPrincipal s
			  WHERE
			  	   u.Username = 'admin'
			  	   and s.Name = 'user'
			  );

			 INSERT INTO PrincipalRole ( PrincipalId, RoleId )
			 (SELECT s.Id, r.Id
			 FROM
			 	SecurityPrincipal s, SecurityRole r
			 WHERE
			 	s.Name = 'admin'
			 	and r.Name = 'ADMIN'
			 );


			 INSERT INTO RolePolicy ( RoleId, PolicyId )
			 (SELECT r.Id, p.Id
			 FROM
			 	SecurityRole r , SecurityPolicy p
			 WHERE
			 	r.Name = 'ADMIN'
			 	and p.Name = 'ALL'
			 );

			INSERT INTO RolePolicy ( RoleId, PolicyId )
			 (SELECT r.Id, p.Id
			 FROM
			 	SecurityRole r , SecurityPolicy p
			 WHERE
			 	r.Name = 'ADMIN'
			 	and p.Name = 'ReadBlueConsole'
			 );

      INSERT INTO RolePolicy ( RoleId, PolicyId )
			 (SELECT r.Id, p.Id
			 FROM
			 	SecurityRole r , SecurityPolicy p
			 WHERE
			 	r.Name = 'ADMIN'
			 	and p.Name = 'WriteBlueConsole'
			 );







