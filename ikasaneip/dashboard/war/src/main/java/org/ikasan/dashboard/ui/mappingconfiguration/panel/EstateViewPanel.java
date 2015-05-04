 /*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.module.IkasanModuleService;
import org.ikasan.module.model.Component;
import org.ikasan.module.model.Flow;
import org.ikasan.module.model.Module;

import com.vaadin.event.Action;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;

/**
 * @author Ikasan Development Team
 *
 */
public class EstateViewPanel extends Panel implements View, Action.Handler
{
    private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = Logger.getLogger(EstateViewPanel.class);

    private static final Action START = new Action("Start");
    private static final Action STOP = new Action("Stop");
    private static final Action[] startAction = new Action[] { START };
    private static final Action[] stopAction = new Action[] { STOP };
    private static final Action[] actionsEmpty = new Action[]{};

    private IkasanModuleService ikasanModuleService;
    private Tree tree = new Tree("Middleware Estate");

    private ThemeResource stopResource = new ThemeResource("images/stop.png");
    private ThemeResource checkMarkResource = new ThemeResource("images/check_mark.png");
    private ThemeResource warningResource = new ThemeResource("images/warning.png");

    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public EstateViewPanel(IkasanModuleService ikasanModuleService)
    {
        super();
        this.ikasanModuleService = ikasanModuleService;
        init();
    }

    protected void init()
    {
        this.setWidth("100%");
        this.setHeight("100%");
        this.setStyleName("dashboard");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setHeight("100%");
        horizontalLayout.setMargin(true);

        tree.addActionHandler(this);

        List<Module> modules = this.ikasanModuleService.getResolvedModules();

        for(Module module: modules)
        {
            ArrayList<Flow> flows = module.getFlows();
            tree.addItem(module);
            tree.setItemCaption(module, module.getModuleName());
            tree.setChildrenAllowed(module, true);

            for(Flow flow: flows)
            {
                String status = this.ikasanModuleService.getInitiatorStatus(module.getServerName()
                    , module.getModuleName(), flow.getInitiatorName());

                tree.addItem(flow);
                tree.setItemCaption(flow, flow.getFlowName());
                if(status.equals("running"))
                {
                    tree.setItemIcon(flow, checkMarkResource);
                }
                else if(status.equals("runningInRecovery"))
                {
                    tree.setItemIcon(flow, warningResource);
                }
                if(status.equals("stopped"))
                {
                    tree.setItemIcon(flow, stopResource);
                }
                
                tree.setParent(flow, module);
                tree.setChildrenAllowed(flow, true);
                ArrayList<Component> components = flow.getComponents();

                for(Component component: components)
                {
                    tree.addItem(component.getComponentName());
                    tree.setParent(component.getComponentName(), flow);
                    tree.setChildrenAllowed(component.getComponentName(), false);
                }
            }
        }

        horizontalLayout.addComponent(tree);
        this.setContent(horizontalLayout);
    }

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see com.vaadin.event.Action.Handler#getActions(java.lang.Object, java.lang.Object)
     */
    @Override
    public Action[] getActions(Object target, Object sender)
    {
//        if(target != null)
//            logger.info("Target: " + target.getClass().getName());
//
//        if(sender != null)
//            logger.info("Sender: " + sender.getClass().getName());

        if(target instanceof Flow)
        {
            Flow flow = (Flow)target;
            String status = this.ikasanModuleService.getInitiatorStatus(flow.getModule().getServerName()
                , flow.getModule().getModuleName(), flow.getInitiatorName());

            if(status.equals("running"))
            {
                return stopAction;
            }
            else if(status.equals("runningInRecovery"))
            {
                return stopAction;
            }
            else
            {
                return startAction;
            }
        }
        else
        {
            return actionsEmpty;
        }
    }

    /* (non-Javadoc)
     * @see com.vaadin.event.Action.Handler#handleAction(com.vaadin.event.Action, java.lang.Object, java.lang.Object)
     */
    @Override
    public void handleAction(Action action, Object sender, Object target)
    {
        logger.info("Action: " + action.getCaption());
        logger.info("Target: " + target.getClass().getName());
        logger.info("Sender: " + sender.getClass().getName());

        if(action.getCaption().equals("Start"))
        {
            Flow flow = (Flow)target;
            this.ikasanModuleService.startInitiator(flow.getModule().getServerName(), 
                flow.getModule().getModuleName(), flow.getInitiatorName());

            String status = this.ikasanModuleService.getInitiatorStatus(flow.getModule().getServerName()
                , flow.getModule().getModuleName(), flow.getInitiatorName());

            if(status.equals("running"))
            {
                tree.setItemIcon(flow, checkMarkResource);
            }
            else if(status.equals("runningInRecovery"))
            {
                tree.setItemIcon(flow, warningResource);
                Notification.show("Could not start the initiator!",
                    "The initiator went into recovery.",
                    Notification.Type.WARNING_MESSAGE);
            }
            else
            {
                tree.setItemIcon(flow, stopResource);
                Notification.show("Could not start initiator!",
                    "The initiator remained stopped. It may be disabled.",
                    Notification.Type.WARNING_MESSAGE);
            }
        }
        if(action.getCaption().equals("Stop"))
        {
            Flow flow = (Flow)target;
            this.ikasanModuleService.stopInitiator(flow.getModule().getServerName(), 
                flow.getModule().getModuleName(), flow.getInitiatorName());

            String status = this.ikasanModuleService.getInitiatorStatus(flow.getModule().getServerName()
                , flow.getModule().getModuleName(), flow.getInitiatorName());

            if(status.equals("running"))
            {
                tree.setItemIcon(flow, checkMarkResource);
                Notification.show("Could not stop the initiator!",
                    "The initiator remained running.",
                    Notification.Type.WARNING_MESSAGE);
            }
            else if(status.equals("runningInRecovery"))
            {
                Notification.show("Could not stop the initiator!",
                    "The initiator remained running in recovery.",
                    Notification.Type.WARNING_MESSAGE);
            }
            else
            {
                tree.setItemIcon(flow, stopResource);
            }
        }
    }
}
