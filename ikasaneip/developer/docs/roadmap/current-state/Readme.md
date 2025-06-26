![Problem Domain](../../quickstart-images/Ikasan-title-transparent.png)
# Ikasan Current State

## Module/Agent Authentication and Authorisation
Module/Agent authentication and authorisation can be configured to delegate to the Ikasan Dashboard, or delegate to the local
H2 database.

When the `ikasan.dashboard.extract.enabled` flag is set to true in the application.properties, authentication and authorisation 
delegates to the authentication REST service (see [Dashboard REST Services](../../../../visualisation/dashboard/dashboard-rest.md)) 
exposed in the Ikasan Dashboard. Depending on the configuration of the Ikasan Dashboard, authentication will performed against LDAP, 
or the dashboard's local H2 database. 

When configured in this manner, the module/agent is reliant upon the Ikasan Dashboard being available in order
to access the associated console. However, the module can still be stopped and started via the Ikasan Shell and assuming flows are 
configured to start automatically the module will process business messages unimpeded. 
```properties
ikasan.dashboard.extract.enabled=true
```

Module/Agent authentication and authorisation can also be configured to delegate to its local H2 database by setting the
`ikasan.dashboard.extract.enabled` flag to false in the application.properties.
```properties
ikasan.dashboard.extract.enabled=false
```

### Dashboard and LDAP Authentication and Authorisation
![module authentication](./images/ikasan-typology-Agent%20_%20Module%20Authentication.drawio.png)
1. Module or agent initiates a call to the authentication service exposed in the Ikasan Dashboard.
2. Using the credential provided in the authentication request, the user account is authenticated against the LDAP server.
3. If the authentication is successful, a call to the dashboard's underlying H2 database retrieves the policies for which the user is authorised.
4. A JSON Web Token (JWT) is returned to the module / agent providing access to the authorisations for the authenticated user.

> [!RISKS]  
> Relies on a highly available LDAP server.
> If the Ikasan Dashboard is not available, the module/agent cannot authenticate and the associated console cannot be accessed and entities cannot be harvested to the document store via the Ikasan Dashboard.
> If the H2 database associated with the Ikasan Dashboard is corrupted or unavailable, the authentication and authorisation will fail.

### Dashboard and Local Authentication and Authorisation
![module authentication](./images/ikasan-typology-Agent%20_%20Module%20Local%20Authentication.drawio.png)
1. Module or agent initiates a call to the authentication service exposed in the Ikasan Dashboard.
2. Using the credential provided in the authentication request, the user account is authenticated against the password stored in the H2 database.
3. If the authentication is successful, a call to the dashboard's underlying H2 database retrieves the policies for which the user is authorised.
4. A JSON Web Token (JWT) is returned to the module / agent providing access to the authorisations for the authenticated user.

> [!RISKS]
> If the Ikasan Dashboard is not available, the module/agent cannot authenticate and the associated console cannot be accessed and entities cannot be harvested to the document store via the Ikasan Dashboard.
> If the H2 database associated with the Ikasan Dashboard is corrupted or unavailable, the authentication and authorisation will fail.


### Module/Agent Local Authentication and Authorisation
![module authentication](./images/ikasan-typology-Agent_Module-local-authentication.drawio.png)
1. Module or agent initiates delegates to the local H2 database, and the user account is authenticated against the password stored in the H2 database.
2. If the authentication is successful, the module/agent policies are retrieved from the local H2 database and the user given access to the module/agent console with access to features for which they are authorised.

> [!RISKS]  
> If the H2 database associated with the module/agent is corrupted or unavailable, the authentication and authorisation will fail. Moreover, the module/agent will simply not start.
