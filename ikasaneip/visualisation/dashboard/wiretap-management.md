![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Visualisation Dashboard Wiretap Management

A wiretap represents an EAI concept in which data events can be captured for the purpose of testing, monitoring or troubleshooting. Ikasan 
supports dynamic wiretaps that can be added to a flow of data, at runtime, from within the Ikasan Dashboard. Wiretaps are added and removed 
from within the visualisation screens within the dashboard.
 
Ikasan supports 2 different kinds of wiretaps. 
<br/>
<br/>
<img src="../../developer/docs/quickstart-images/wiretap.png" width="100px" align="left"> Indexed wiretaps that are written to Ikasan's underlying text index, which can subsequently be searched for in the Ikasan Dashboard.
<br/>
<br/> 
<br/>
<br/>
<img src="../../developer/docs/quickstart-images/log-wiretap.png" width="100px" align="left">Log wiretaps that record the data event in the module log file, of the module that the wiretap was added to.
<br/>
<br/>
<br/>
<br/>
<br/>
<img src="../../developer/docs/quickstart-images/wiretap-icons.png" width="200px" align="left">Wiretaps are added to components and can be added 'BEFORE' or 'AFTER' the component. Icons appear along side the component with those
appearing to the left of the component, denoting a wiretap 'BEFORE' the component execution, and those to the right denoting a wiretap 'AFTER' the component execution. 
<br/>
<br/>
<br/>
<br/>

### Adding a wiretap
<img src="../../developer/docs/quickstart-images/component-options.png" width="300px" align="left">In order to add a wiretap to a component, navigate to the visualisation screen of the component you would like to add the wiretap to. Double click on the component and you will be presented with the 'Component Options' screen. You will be able to choose to add: 
<br/>- An indexed wiretap before the component.
<br/>- An indexed wiretap after the component.
<br/>- A log wiretap before the component.
<br/>- A log wiretap after the component.
<br/><br/>Once a wiretap is added an icon will appear next to the component.
<br/><br/><br/>
<br/>
### Removing a wiretap
<img src="../../developer/docs/quickstart-images/wiretap-management.png" width="300px" align="left"> In order to remove a wiretap, navigate to the visualisation screen of the component you would like to remove the wiretap from. Double click on the wiretap icon that you wish to remove and you will be presented with the 'Wiretap Management' screen. Click the 'Remove Wiretap' button and the wiretap will be removed.


