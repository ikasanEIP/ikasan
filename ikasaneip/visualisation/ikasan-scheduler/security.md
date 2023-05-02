

## Role Definitions
```sql
--<role-name> security roles and associated policies --

-- <role-name>_READ_ONLY --
insert into SECURITYROLE (NAME, DESCRIPTION)
values ('<role-name>_READ_ONLY', 'Read only role for <role-name> job plans.');

insert into ROLEPOLICY (ROLEID, POLICYID)
values (select ID from SECURITYROLE where NAME = '<role-name>_READ_ONLY', select ID from SECURITYPOLICY where NAME = 'scheduler-read');
-- END IES_ASSET_CONTROL_READ_ONLY --

-- <role-name>_WRITE --
insert into SECURITYROLE (NAME, DESCRIPTION)
values ('<role-name>_WRITE', 'Write role for <role-name> job plans.');

insert into ROLEPOLICY (ROLEID, POLICYID)
values (select ID from SECURITYROLE where NAME = '<role-name>_WRITE', select ID from SECURITYPOLICY where NAME = 'scheduler-write');
-- END <role-name>_WRITE --

-- <role-name>_ADMIN --
insert into SECURITYROLE (NAME, DESCRIPTION)
values ('<role-name>_ADMIN', 'Administrator role for Asset Control job plans.');

insert into ROLEPOLICY (ROLEID, POLICYID)
values (select ID from SECURITYROLE where NAME = '<role-name>_ADMIN', select ID from SECURITYPOLICY where NAME = 'scheduler-admin');
-- <role-name>_ADMIN --

-- END <role-name> security roles and associated policies --
```

## Associated Agents
```sql
-- <role-name> security associated agents --
delete
from ROLEMODULE
where ROLEID IN (select ID from SECURITYROLE where NAME = '<role-name>_READ_ONLY', select ID from SECURITYROLE where NAME = '<role-name>_WRITE', select ID from SECURITYROLE where NAME = '<role-name>_ADMIN');

-- <role-name>_READ_ONLY --
insert into ROLEMODULE (ROLEID, MODULENAME)
values (select ID from SECURITYROLE where NAME = '<role-name>_READ_ONLY', '<agent-name>');
-- END <role-name>_READ_ONLY --

-- <role-name>_WRITE --
insert into ROLEMODULE (ROLEID, MODULENAME)
values (select ID from SECURITYROLE where NAME = '<role-name>_WRITE', '<agent-name>');
-- END <role-name>_WRITE --

-- <role-name>_ADMIN --
insert into ROLEMODULE (ROLEID, MODULENAME)
values (select ID from SECURITYROLE where NAME = '<role-name>_ADMIN', '<agent-name>');
-- <role-name>_ADMIN --

-- END <role-name> associated agents --
```

## Associated Job Plans

```sql
-- Asset Control associated job plans --
delete
from ROLEJOBPLAN
where ROLEID IN (select ID from SECURITYROLE where NAME = 'IES_ASSET_CONTROL_READ_ONLY', select ID from SECURITYROLE where NAME = 'IES_ASSET_CONTROL_WRITE', select ID from SECURITYROLE where NAME = 'IES_ASSET_CONTROL_ADMIN');

-- <role-name>_READ_ONLY --
insert into ROLEJOBPLAN (ROLEID, JOBPLANNAME)
values (select ID from SECURITYROLE where NAME = '<role-name>_READ_ONLY', '<job-plan-name>');
-- END IES_ASSET_CONTROL_READ_ONLY --

-- <role-name>_WRITE --
insert into ROLEJOBPLAN (ROLEID, JOBPLANNAME)
values (select ID from SECURITYROLE where NAME = '<role-name>_WRITE', '<job-plan-name>');
-- END <role-name>_WRITE --

-- <role-name>_ADMIN --
insert into ROLEJOBPLAN (ROLEID, JOBPLANNAME)
values (select ID from SECURITYROLE where NAME = '<role-name>_ADMIN', '<job-plan-name>');
-- <role-name>_ADMIN --

-- END <role-name> associated job plans --
```
