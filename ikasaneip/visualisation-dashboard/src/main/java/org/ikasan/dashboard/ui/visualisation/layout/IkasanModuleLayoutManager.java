package org.ikasan.dashboard.ui.visualisation.layout;


import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Logo;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by stewmi on 08/11/2018.
 */
public class IkasanModuleLayoutManager extends LayoutManagerBase implements LayoutManager
{
    Logger logger = LoggerFactory.getLogger(IkasanModuleLayoutManager.class);

    private Module module;

    protected int flowSpacing = 300;

    public IkasanModuleLayoutManager(Module module, NetworkDiagram networkDiagram, Logo logo)
    {
        super(networkDiagram, logo);
        this.module = module;
    }

    public void layout()
    {
        int x = xStart;
        int y = yStart;

        logger.debug("Laying out module [{}]. ", module.getName());

        for(Flow flow: module.getFlows())
        {
            logger.debug("Laying out flow [{}]. ", module.getName());

            flow.getConsumer().setX(x);
            flow.getConsumer().setY(y);

            logger.debug("Adding consumer [{}] for flow [{}]. ", flow.getConsumer().getLabel(), flow.getName());

            nodeList.add(flow.getConsumer());

            addEdge(flow.getConsumer().getId(), flow.getConsumer().getTransition().getId(), flow.getConsumer().getTransitionLabel());

            manageTransition(flow.getConsumer().getTransition(), x, y);

            this.networkDiagram.drawFlow(x - 100, y - 100, xExtent + 200 - x , yExtent + 200 - y , flow.getName());

            x = xStart;
            xExtent = x;
            y = yExtent + flowSpacing;
        }

        this.networkDiagram.setNodes(this.nodeList);
        this.networkDiagram.setEdges(this.edgeList);

        this.channels.forEach(messageChannel -> messageChannel.setX(xExtentFinal + 300));

        this.networkDiagram.drawModule(xStart - 200, yStart - 200, xExtentFinal + 600, yExtent + 400, module.getName());

    }

}
