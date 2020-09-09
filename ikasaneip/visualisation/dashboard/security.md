![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Visualisation Dashboard Security

Ikasan supports role based access control. 

- [User Management](./user-management.md) Ikasan supports both local users, as well as LDAP users if Ikasan is configured for LDAP security. Users are in fact security principals whose type is 'user'. If Ikasan is configured for LDAP security, user authentication is performed against LDAP, otherwise it is performed againt security credentials held in the Ikasan database.
- [Group Management](./group-management.md) is only relevant if Ikasan is configured for LDAP security. Groups are in fact security principals whose type is 'application'. There is a one to one relationship to LDAP groups. If an Ikasan 'Role' is assigned to a 'Group', all users within that group will be assigned that 'Role' within Ikasan.  
- [Role Management](./role-management.md) An Ikasan 'Role' is assigned security 'Policies'. A 'Role' can be assigned to a 'User' or a 'Group'.
- [Policy Management](./policy-management.md) A 'Policy' defines fine grained security associated with Ikasan features. 
- [LDAP Management](./ldap-management.md) The LDAP management section allows Ikasan to be configured to operate within a corporate environment employing LDAP security features.



