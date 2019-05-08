[<< Topology](./Topology.md)
![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
## Topology Tree

The topology tree provides a hierarchical view on all integration modules, flows, and component elements and provides functionality to allow for the control and administration of these elements. The following functionality is available within the tree (dependant on the permissions you have within the application):

ESB wide functionality:
- Discover new modules.

At the module level:
- The ability to categorise an error.

At the flow level:
- The ability to control the flow (Start, Stop, Pause, Resume).
- The ability to configure the start-up type.
- The ability to categorise an error.

At the component level:
- The ability to configure a component.
- The ability to add a wiretap to a component.
- The ability to categorise an error.

There are a number of icons in the topology tree which represent the following:

<img src="../developer/docs/sample-images/module.png" align="left">Represents an Ikasan module.

<img src="../developer/docs/sample-images/flow.png" align="left">Represents a flow within an Ikasan module.

<img src="../developer/docs/sample-images/component-configurable.png" align="left">Represents a component within an Ikasan flow which can be configured.

<img src="../developer/docs/sample-images/component.png" align="left">Represents a component within an Ikasan flow which cannot be configured.