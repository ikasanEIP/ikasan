![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Visualisation Dashboard Flow Start Up Type Management

Ikasan support 3 different types of flow start up types:

- Manual
- Automatic
- Disabled

### Manual
<img src="../../developer/docs/quickstart-images/flow-manual.png" width="300px" align="left">Flows set with a start up type of 'Manual' are required to be started manually. When the module JVM is restarted any flows with a 'Manual' restart
will remained stopped until they are manually restarted.

### Automatic
<img src="../../developer/docs/quickstart-images/flow-automatic.png" width="300px" align="left">Flows set with a start up type of 'Automatic' will restart automatically when the module JVM is restarted.

### Disabled
<img src="../../developer/docs/quickstart-images/flow-disabled.png" width="300px" align="left">Flows set with a start up type of 'Disabled' cannot be started. A comment is required when a flow's start up is set to disabled.



