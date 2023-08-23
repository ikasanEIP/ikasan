DELETE FROM RoleModule;
DELETE FROM RoleJobPlan;
DELETE FROM RolePolicy;
DELETE FROM SecurityPolicy;
DELETE from PrincipalRole where principalId>20000;
DELETE from UserPrincipal where userId>10000;
DELETE from SecurityRole where id>30000;
DELETE from SecurityPrincipal where id>20000;
DELETE from Users where id>10000;
