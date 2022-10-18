![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Visualisation Dashboard

- [ESB Dashboard](./dashboard.md)
- [Scheduler Dashboard](../ikasan-scheduler/scheduler-dashboard.md)
- [Search](./search.md)
- [Module, Flow and Business Stream Visualisation](./visualisation-screens.md)
- [Scheduled Jobs](./scheduler.md)
- [Security](./security.md)
- [Notifications](./notifications.md)
- [Business Stream Designer](./business-stream-designer.md)

## Getting started

1. Download the Ikasan Visualisation Dashboard distribution from [Maven Central](https://search.maven.org/search?q=org.ikasan). Search for the distribution `org.ikasan:ikasan-dashboard-distrbution:<version>`. 
2. Unzip the distribution to the desired installation location.
3. Run `ikasan.sh start` to start the dashboard. Note the dashboard required JDK11+. This will start the solr index and H2 database.
4. Open http://localhost:9090/ in browser
5. In order to stop the dashboard `ikasan.sh stop`

It is possible to control H2 and Solr individually using commands `ikasan.sh start-h2`, `ikasan.sh stop-h2`, `ikasan.sh start-solr` and `ikasan.sh stop-solr`. It is also possible to get details of the running processes `ikasan.sh ps`.

Unit tests are written with the assistance of (Karibu)(https://github.com/mvysny/karibu-testing/tree/master/karibu-testing-v10). This framework 
allows UI components to be tested without the need for browser of and related drivers.

For documentation on using Vaadin Flow and Spring, visit [vaadin.com/docs](https://vaadin.com/docs/v10/flow/spring/tutorial-spring-basic.html)

For more information on Vaadin Flow, visit https://vaadin.com/flow.

