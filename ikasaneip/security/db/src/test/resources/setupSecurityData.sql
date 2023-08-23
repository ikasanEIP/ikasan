/* Users */
INSERT INTO Users (ID, Username, Firstname, Surname, Department, Email, NewPassword, Enabled, PreviousAccess, RequiresPasswordChange)
    VALUES (10001, 'username', 'firstname', 'surname', 'newPassword', 'firstname@lastname.com', 'password', true, '1595355876119', true);

/* SecurityPrincipal */
INSERT INTO SecurityPrincipal (ID, Name, PrincipalType, Description, CreatedDateTime, UpdatedDateTime)
    VALUES (20001, 'principal 1', 'application', 'Sample description', '2020-01-01 01:01:01', '2020-01-01 01:01:01');
INSERT INTO SecurityPrincipal (ID, Name, PrincipalType, Description, CreatedDateTime, UpdatedDateTime)
    VALUES (20002, 'principal 2', 'application', 'Another principal description', '2020-01-01 01:01:01', '2020-01-01 01:01:01');

/* SecurityRole */
INSERT INTO SecurityRole (Id, Name, Description, CreatedDateTime, UpdatedDateTime) VALUES (30001, 'admin', 'Admin role', '2020-01-01 01:01:01', '2020-01-01 01:01:01');
INSERT INTO SecurityRole (Id, Name, Description, CreatedDateTime, UpdatedDateTime) VALUES (30002, 'user', 'User role', '2020-01-01 01:01:01', '2020-01-01 01:01:01');

/* UserPrincipal */
INSERT INTO UserPrincipal(UserId, PrincipalId) VALUES (10001, 20001);
INSERT INTO UserPrincipal(UserId, PrincipalId) VALUES (10001, 20002);

/* PrincipalRole */
INSERT INTO PrincipalRole(PrincipalId, RoleId) VALUES (20001, 30001);
INSERT INTO PrincipalRole(PrincipalId, RoleId) VALUES (20002, 30002);

INSERT INTO SecurityPolicy (Id, Name, Description, CreatedDateTime, UpdatedDateTime) VALUES (40001, 'policy1', 'sample policy', '2020-01-01 01:01:01', '2020-01-01 01:01:01');
INSERT INTO SecurityPolicy (Id, Name, Description, CreatedDateTime, UpdatedDateTime) VALUES (40002, 'policy2', 'sample', '2020-01-01 01:01:01', '2020-01-01 01:01:01');

INSERT INTO RolePolicy(RoleId, PolicyId) VALUES (30001, 40001);
INSERT INTO RolePolicy(RoleId, PolicyId) VALUES (30002, 40002);

INSERT INTO RoleModule(RoleId, ModuleName, CreatedDateTime, UpdatedDateTime) VALUES (30001,'sample module', '2020-01-01 01:01:01', '2020-01-01 01:01:01');
INSERT INTO RoleJobPlan(RoleId, JobPlanName, CreatedDateTime, UpdatedDateTime) VALUES (30001,'sample job plan', '2020-01-01 01:01:01', '2020-01-01 01:01:01');
