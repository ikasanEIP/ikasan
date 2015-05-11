SET IDENTITY_INSERT AuthenticationMethod ON

insert into AuthenticationMethod (
   Id
  ,Method
  ,LdapServerUrl
  ,LdapBindUserDn
  ,LdapBindUserPassword
  ,LdapUserSearchBaseDn
  ,LdapUserSearchFilter
) VALUES (
   1   -- Id - IN numeric(18, 0)
  ,'AUTH_METHOD_LDAP'  -- Method - IN varchar(128)
  ,'ldap://UK.MIZUHO-SC.COM:389/' -- LdapServerUrl - IN varchar(256)
  ,'CN=cmi2d,OU=Unix Service Accounts,OU=Logins,DC=uk,DC=mizuho-sc,DC=com' -- LdapBindUserDn - IN varchar(256)
  ,'Cm12devcontrol' -- LdapBindUserPassword - IN varchar(256)
  ,'OU=People,OU=Logins,DC=uk,DC=mizuho-sc,DC=com' -- LdapUserSearchBaseDn - IN varchar(256)
  ,'(sAMAccountName={0})' -- LdapUserSearchFilter - IN varchar(256)
)

SET IDENTITY_INSERT AuthenticationMethod OFF