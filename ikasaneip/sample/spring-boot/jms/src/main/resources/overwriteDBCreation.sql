
INSERT INTO Authorities (Id , Authority, Description )
VALUES ( 10,'ROLE_USER', 'Users who may log into the system' );

INSERT
INTO Authorities (Id , Authority, Description )
VALUES (20, 'ROLE_ADMIN','Users who may perform administration functions on					the system' );


INSERT INTO Users (Id, Username, Password, Enabled, FirstName, Surname,PreviousAccess )
					VALUES (1,'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', 1 , 'Admin', 'User',10);


INSERT
					INTO UsersAuthorities values ( 1, 10 );
INSERT
					INTO UsersAuthorities values ( 1,20 );


