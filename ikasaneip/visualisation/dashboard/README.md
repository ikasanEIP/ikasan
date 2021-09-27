![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Visualisation Dashboard

- [Dashboard](./dashboard.md)
- [Search](./search.md)
- [Scheduled Jobs](./scheduler.md)
- [Security](./security.md)
- [Notifications](./notifications.md)

Import the project to the IDE of your choosing as a Maven project. 

Run application using `mvn spring-boot:run` or directly running Application class from your IDE.

Open http://localhost:9080/ in browser

In the slider in the right hands side of the the screen titled Tools, there are controls
to change the state of each node, which in turn is reflected by the pulsing colour behind 
the node. It is also possible to upload JSON businessStream files to this application which are
then reflected on the businessStream visualisation screen.

There are 2 example files bundled with the application:
- [Bond Flows](src/test/resources/data/businessStream/bondFlowsGraph.json)
- [Reference Data Flows](src/test/resources/data/businessStream/referenceDataGraph.json)

```json
{
  "flows": [
    {
      "id": "brokertec-trade",
      "name": "brokertec-trade",
      "x": 200,
      "y": 0
    },
    {
      "id": "espeed-trade",
      "name": "espeed-trade",
      "x": 200,
      "y": 200
    },
    {
      "id": "tradeweb-trade",
      "name": "tradeweb-trade",
      "x": 200,
      "y": 400
    },
    {
      "id": "ion-trade",
      "name": "ion-trade",
      "x": 0,
      "y": 600
    },
    {
      "id": "ion-derivativeTrade",
      "name": "ion-derivativeTrade",
      "x": 200,
      "y": 500
    },
    {
      "id": "ion-debtTrade",
      "name": "ion-debtTrade",
      "x": 200,
      "y": 700
    },
    {
      "id": "blbgToms-mhiTrade",
      "name": "blbgToms-mhiTrade",
      "x": 800,
      "y": 300
    }
  ],
  "integratedSystems": [
    {
      "id": "Brokertec",
      "name": "Brokertec",
      "x": 0,
      "y": 0
    },
    {
      "id": "Espeed",
      "name": "Espeed",
      "x": 0,
      "y": 200
    },
    {
      "id": "Tradeweb",
      "name": "TradeWeb",
      "x": 0,
      "y": 400
    },
    {
      "id": "ION",
      "name": "ION",
      "x": -200,
      "y": 600
    },
    {
      "id": "Bloomberg",
      "name": "Bloomberg TOMS",
      "x": 600,
      "y": 300
    }
  ],
  "edges": [
    {
      "from": "Brokertec",
      "to": "brokertec-trade"
    },
    {
      "from": "Tradeweb",
      "to": "tradeweb-trade"
    },
    {
      "from": "Espeed",
      "to": "espeed-trade"
    },
    {
      "from": "ION",
      "to": "ion-trade"
    },
    {
      "from": "brokertec-trade",
      "to": "Bloomberg"
    },
    {
      "from": "espeed-trade",
      "to": "Bloomberg"
    },
    {
      "from": "tradeweb-trade",
      "to": "Bloomberg"
    },
    {
      "from": "ion-trade",
      "to": "ion-debtTrade"
    },
    {
      "from": "ion-trade",
      "to": "ion-derivativeTrade"
    },
    {
      "from": "ion-derivativeTrade",
      "to": "Bloomberg"
    },
    {
      "from": "ion-debtTrade",
      "to": "Bloomberg"
    },
    {
      "from": "Bloomberg",
      "to": "blbgToms-mhiTrade"
    }
  ]
}
```

Unit tests are written with the assistance of (Karibu)(https://github.com/mvysny/karibu-testing/tree/master/karibu-testing-v10). This framework 
allows UI components to be tested without the need for browser of and related drivers.

For documentation on using Vaadin Flow and Spring, visit [vaadin.com/docs](https://vaadin.com/docs/v10/flow/spring/tutorial-spring-basic.html)

For more information on Vaadin Flow, visit https://vaadin.com/flow.

