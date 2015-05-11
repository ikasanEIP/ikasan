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
package org.ikasan.module;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.mappingconfiguration.panel.MappingConfigurationPanel;
import org.ikasan.module.model.Component;
import org.ikasan.module.model.Flow;
import org.ikasan.module.model.Module;
import org.ikasan.module.util.HttpTransport;


/**
 * @author Ikasan Development Team
 *
 */
public class IkasanModuleServiceImpl implements IkasanModuleService
{
    enum InitiatorAction
    {
        START, STOP;
    }

    private Logger logger = Logger.getLogger(MappingConfigurationPanel.class);

    private static final String IKASAN_URL_SUFFIX = "?moduleName=%s&initiatorName=%s";

    private static final String IKASAN_MODULE_NAME_SUFFIX = "?moduleName=%s";

    private static final String IKASAN_FLOW_COMPONENT_SUFFIX = "?moduleName=%s&flowName=%s";

    private static final String IKASAN_ACTION_SUFFIX = "&initiatorAction=%s";

    private static final String IKASAN_ACTION_PREFIX = "/modules/initiator.htm";

    private static final String IKASAN_VIEW_INITIATOR_PREFIX = "/modules/viewInitiator.htm";

    private static final String IKASAN_VIEW_PREFIX = "/modules/view.htm";

    private static final String IKASAN_VIEW_FLOW_PREFIX = "/modules/viewFlow.htm";

    private static final String STATUS_TAG = "id=\"initiatorStateControl\"";

    private static final String VIEW_INITIATOR_TAG = "<a href=\"viewInitiator.htm?moduleName";

    private static final String VIEW_FLOW_TAG = "<a href=\"viewFlow.htm?moduleName=";
    
    private static final String VIEW_FLOW_ELEMENT_TAG = "<a href=\"viewFlowElement.htm?moduleName=";

    private HttpTransport httpTransport;

    private List<Module> modules;

    /**
     * @param httpTransport
     */
    public IkasanModuleServiceImpl(HttpTransport httpTransport, String username, String password, List<Module> modules)
    {
        super();
        this.httpTransport = httpTransport;
        this.modules = modules;
        httpTransport.setCredentials(username, password);
    }

    /* (non-Javadoc)
     * @see org.ikasan.module.IkasanModuleService#getAllInitiators(java.lang.String)
     */
    @Override
    public List<String> getAllInitiators(String targetUrl, String moduleName)
    {
        String actionUrl = String.format(IKASAN_MODULE_NAME_SUFFIX, moduleName);
        String executeActionUrl = targetUrl + "/cmi2-" + moduleName + IKASAN_VIEW_PREFIX + actionUrl;
//        String executeActionUrl = targetUrl + "/" + moduleName + IKASAN_VIEW_PREFIX + actionUrl;

        String result = this.httpTransport.executePost(executeActionUrl, "");

        return this.getValuesFromResponseHtml(result, VIEW_INITIATOR_TAG);
    }

    /* (non-Javadoc)
     * @see org.ikasan.module.IkasanModuleService#getAllFlows(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getAllFlows(String targetUrl, String moduleName)
    {
        String actionUrl = String.format(IKASAN_MODULE_NAME_SUFFIX, moduleName);
        String executeActionUrl = targetUrl + "/cmi2-" + moduleName + IKASAN_VIEW_PREFIX + actionUrl;
//        String executeActionUrl = targetUrl + "/" + moduleName + IKASAN_VIEW_PREFIX + actionUrl;

        String result = this.httpTransport.executePost(executeActionUrl, "");

        return this.getValuesFromResponseHtml(result, VIEW_FLOW_TAG);
    }

    /* (non-Javadoc)
     * @see org.ikasan.module.IkasanModuleService#getAllComponents(java.lang.String)
     */
    @Override
    public List<String> getAllComponents(String targetUrl, String moduleName, String flowName)
    {
        String actionUrl = String.format(IKASAN_FLOW_COMPONENT_SUFFIX, moduleName, flowName);
        String executeActionUrl = targetUrl + "/cmi2-" + moduleName + IKASAN_VIEW_FLOW_PREFIX + actionUrl;
//        String executeActionUrl = targetUrl + "/" + moduleName + IKASAN_VIEW_FLOW_PREFIX + actionUrl;
        String result = this.httpTransport.executePost(executeActionUrl, "");

        return this.getValuesFromResponseHtml(result, VIEW_FLOW_ELEMENT_TAG);
    }

    /* (non-Javadoc)
     * @see org.ikasan.module.IkasanModuleService#startInitiator(java.lang.String, java.lang.String)
     */
    @Override
    public void startInitiator(String targetUrl, String moduleName, String initiatorName)
    {
        InitiatorAction initiatorAction = InitiatorAction.START;

        String actionUrl = String.format(IKASAN_URL_SUFFIX, moduleName, initiatorName);
        actionUrl = actionUrl + String.format(IKASAN_ACTION_SUFFIX, initiatorAction.name().toLowerCase());
        String executeActionUrl = targetUrl + "/cmi2-" + moduleName + IKASAN_ACTION_PREFIX + actionUrl;

        this.httpTransport.executePost(executeActionUrl, "");
    }

    /* (non-Javadoc)
     * @see org.ikasan.module.IkasanModuleService#stopInitiator(java.lang.String, java.lang.String)
     */
    @Override
    public void stopInitiator(String targetUrl, String moduleName, String initiatorName)
    {
        InitiatorAction initiatorAction = InitiatorAction.STOP;
        
        String actionUrl = String.format(IKASAN_URL_SUFFIX, moduleName, initiatorName);
        actionUrl = actionUrl + String.format(IKASAN_ACTION_SUFFIX, initiatorAction.name().toLowerCase());
        String executeActionUrl = targetUrl + "/cmi2-" + moduleName + IKASAN_ACTION_PREFIX + actionUrl;
        
        this.httpTransport.executePost(executeActionUrl, "");
    }

    /* (non-Javadoc)
     * @see org.ikasan.module.IkasanModuleService#getInitiatorStatus(java.lang.String, java.lang.String)
     */
    @Override
    public String getInitiatorStatus(String targetUrl, String moduleName, String initiatorName)
    {
        String viewUrl = String.format(IKASAN_URL_SUFFIX, moduleName, initiatorName);
        String checkStatusUrl = targetUrl + "/cmi2-" + moduleName + IKASAN_VIEW_INITIATOR_PREFIX + viewUrl;
//        String checkStatusUrl = targetUrl + "/" + moduleName + IKASAN_VIEW_INITIATOR_PREFIX + viewUrl;
        String result = getStatusResultFromHtml(this.httpTransport.executePost(checkStatusUrl, ""));
        return result;
    }

    /* (non-Javadoc)
     * @see org.ikasan.module.IkasanModuleService#getResolvedModules()
     */
    @Override
    public List<Module> getResolvedModules()
    {
        for(Module module: this.modules)
        {
            this.getModuleModel(module);
        }

        return this.modules;
    }

    /**
     * Helper method to get the initiator status results from a HTML fragment.
     * 
     * @param resultHtml
     * @return
     */
    private String getStatusResultFromHtml(String resultHtml)
    {
        String statusHtml = resultHtml.substring(resultHtml.indexOf(STATUS_TAG), resultHtml.length());
        String currentStatus = statusHtml.substring(statusHtml.indexOf(">") + 1, statusHtml.indexOf("</span"));

        return currentStatus;
    }

    /**
     * General helper method to get values from within a HTML fragment based on a delimeter.
     * 
     * @param response
     * @param delimeter
     * @return
     */
    private List<String> getValuesFromResponseHtml(String response, String delimeter)
    {
        int index = 0;
        ArrayList<String> results = new ArrayList<String>();

        if(response == null || response.length() == 0)
        {
            return results;
        }

        while(true)
        {
            int markerIndex = response.indexOf(delimeter, index);
            if(markerIndex < 0)
            {
                break;
            }
            int startTagIndex = response.indexOf(">", markerIndex) + 1;
            int endTagIndex = response.indexOf("</a>", startTagIndex);
            String initiatorName = response.substring(startTagIndex, endTagIndex).trim();

            results.add(initiatorName);

            index = endTagIndex;
        }

        return results;
    }

    /**
     * 
     * @param targetUrl
     * @param moduleName
     * @return
     */
    protected Module getModuleModel(Module module)
    {
        List<String> initiators = this.getAllInitiators(module.getServerName(), module.getModuleName());
        List<String> flowNames = this.getAllFlows(module.getServerName(), module.getModuleName());

        ArrayList<Flow> flows = new ArrayList<Flow>();

        for(int i=0; i<initiators.size(); i++)
        {
            Flow flow = new Flow();

            flow.setInitiatorName(initiators.get(i));
            flow.setFlowName(flowNames.get(i));
            flow.setModule(module);

            List<String> componentNames = this.getAllComponents(module.getServerName(), module.getModuleName(), flow.getFlowName());
            ArrayList<Component> components = new ArrayList<Component>();

            for(String componentName: componentNames)
            {
                Component component = new Component();
                component.setComponentName(componentName);

                components.add(component);
            }

            flow.setComponents(components);
            flows.add(flow);
        }

        module.setFlows(flows);
        return module;
    }

    /**
     * @return the modules
     */
    public List<Module> getModules()
    {
        return modules;
    }

    /**
     * @param modules the modules to set
     */
    public void setModules(List<Module> modules)
    {
        this.modules = modules;
    }

    public static final void main(String[] args)
    {
        Module module = new Module();
        module.setServerName("http://svc-fixedincomebondeaip:60000");
        module.setModuleName("blbgToms-mhiCmfTgt");

        List<Module> modules = new ArrayList<Module>();
        modules.add(module);

        IkasanModuleServiceImpl s = new IkasanModuleServiceImpl(new HttpTransport(), "stewmi", "flower", modules);
        List<String> results = s.getAllInitiators("http://svc-fixedincomebondeaip:60000", "blbgToms-mhiCmfTgt");
//        List<String> results = s.getAllInitiators("http://svc-esb01d:8080", "cdw-trade");

        for(String result:results)
        {
            String status = s.getInitiatorStatus("http://svc-fixedincomebondeaip:60000", "blbgToms-mhiCmfTgt", result);
//            String status = s.getInitiatorStatus("http://svc-esb01d:8080", "cdw-trade", result);
            System.out.println(result + ": " + status);
        }

        results = s.getAllFlows("http://svc-fixedincomebondeaip:60000", "blbgToms-mhiCmfTgt");
//        results = s.getAllFlows("http://svc-esb01d:8080", "cdw-trade");

        for(String result:results)
        {
            System.out.println(result);
            
            List<String> components = s.getAllComponents("http://svc-fixedincomebondeaip:60000", "blbgToms-mhiCmfTgt", result);
            
            for(String component: components)
            {
                System.out.println(component);
            }
        }

        modules = s.getResolvedModules();

        System.out.println(modules);
    }

}
