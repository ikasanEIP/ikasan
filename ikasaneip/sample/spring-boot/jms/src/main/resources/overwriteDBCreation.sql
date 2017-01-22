
INSERT INTO Authorities (Id , Authority, Description )
VALUES ( 10,'ROLE_USER', 'Users who may log into the system' );

INSERT
INTO Authorities (Id , Authority, Description )
VALUES (20, 'ROLE_ADMIN','Users who may perform administration functions on					the system' );

--INSERT INTO Authorities (Id , Authority, Description )
--VALUES ( 30,'ALL', 'Users who may log access API' );

INSERT INTO Users (Id, Username, Password, Enabled, FirstName, Surname,PreviousAccess )
					VALUES (1,'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', 1 , 'Admin', 'User',10);

INSERT INTO Users (Id, Username, Password, Enabled, FirstName, Surname,PreviousAccess )
					VALUES (2,'api', 'd033e22ae348aeb5660fc2140aec35850c4da997', 1 , 'API', 'API',10);

INSERT
					INTO UsersAuthorities values ( 1, 10 );
INSERT
					INTO UsersAuthorities values ( 1,20 );

--INSERT
--					INTO UsersAuthorities values ( 1,30 );


      INSERT INTO SecurityPrincipal (Name , PrincipalType, Description, CreatedDateTime ,UpdatedDateTime)
        VALUES ( 'admin','user', 'The administrator user' ,'1970-01-01 00:00:00','1970-01-01 00:00:00');

      INSERT INTO SecurityRole (Name ,  Description, CreatedDateTime ,UpdatedDateTime)
        VALUES ( 'ADMIN', 'Users who may perform administration functions on the system','1970-01-01 00:00:00','1970-01-01 00:00:00' );
      INSERT INTO SecurityRole (Name ,  Description,CreatedDateTime ,UpdatedDateTime )
        VALUES ( 'User', 'Users who have a read only view on the system','1970-01-01 00:00:00','1970-01-01 00:00:00' );
      INSERT INTO SecurityPolicy (Name ,  Description, CreatedDateTime ,UpdatedDateTime )
        VALUES ( 'ALL', 'Policy to do everything','1970-01-01 00:00:00','1970-01-01 00:00:00' );



			 INSERT
			 INTO UserPrincipal ( UserId, PrincipalId )
			 (SELECT u.Id, s.Id
			  FROM Users u, SecurityPrincipal s
			  WHERE
			  	   u.Username = 'admin'
			  	   and s.Name = 'admin'
			  );

			 INSERT INTO PrincipalRole ( PrincipalId, RoleId )
			 (SELECT s.Id, r.Id
			 FROM
			 	SecurityPrincipal s, SecurityRole r
			 WHERE
			 	s.Name = 'admin'
			 	and r.Name = 'ADMIN'
			 );

			 INSERT INTO PrincipalRole ( PrincipalId, RoleId )
			 (SELECT s.Id, r.Id
			 FROM
			 	SecurityPrincipal s, SecurityRole r
			 WHERE
			 	s.Name = 'User'
			 	and r.Name = 'Read Only'
			 );

			 INSERT INTO RolePolicy ( RoleId, PolicyId )
			 (SELECT r.Id, p.Id
			 FROM
			 	SecurityRole r , SecurityPolicy p
			 WHERE
			 	r.Name = 'ADMIN'
			 	and p.Name = 'ALL'
			 );





