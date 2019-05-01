![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Event Generating Consumer

Utility consumer for the generation of ad-hoc events for demonstration or test of flows.

##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| identifier | String | Identifier to be set on the generated FlowEvent |
| payload | String | Payload to be set on the generated FlowEvent |
| eventGenerationInterval | long | Event generation inrerval in milliseconds.0 = immediate and continuous event generation. This is the default value. |
| batchSize | int | Used in conjunction with the eventGenerationInterval to determine number of generated events per interval. |
| eventLimit | String | Allow a limit to be set on the total number of events generated. Default -1 = unlimited. |

##### Sample Usage


# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | April 2019 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |
