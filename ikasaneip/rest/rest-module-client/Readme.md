[../](../../Readme.md)
![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# REST Dashboard Client
 
The REST Module Client provide default DashboardRestService implementation responsible 
for exporting various data types to Ikasan Dashboard. Client is able to export following types

- errors events
- exclusions events
- metrics events
- replay events 
- wiretaps events
- metadata representation of module runtime
- configuration metadata 

<br/>
Dashboard REST endpoints do require authentication in order to publish data, which is handled internaly by the client.

<br/>

By default data extraction of the module is disabled. In order to enable data extraction following properties need to be defined.
<br/>

```properties
# dashboard data extraction settings
ikasan.dashboard.extract.enabled=true
ikasan.dashboard.extract.base.url=http://localhost:9080/ikasan-dashboard
ikasan.dashboard.extract.username=
ikasan.dashboard.extract.password=

```

<br/>
