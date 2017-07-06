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
package org.ikasan.dashboard.ui.mappingconfiguration.component;

import java.util.List;

import org.ikasan.dashboard.ui.framework.group.Refreshable;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.service.MappingManagementService;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.ComboBox;

/**
 * @author CMI2 Development Team
 *
 */
public class ClientComboBox extends ComboBox implements Refreshable, FocusListener
{
    private static final long serialVersionUID = -2820064207169688211L;

    private MappingManagementService mappingConfigurationService;

    /**
     * Constructor
     * 
     * @param mappingConfigurationService
     */
    public ClientComboBox(MappingManagementService mappingConfigurationService)
    {
        this.mappingConfigurationService = mappingConfigurationService;
        this.init();
    }

    @SuppressWarnings("serial")
    protected void init()
    {
        this.setWidth(140, Unit.PIXELS);
//        loadClientSelectValues();
    }

    /**
     * Method to load the values to populate the combo box.
     */
    public void loadClientSelectValues()
    {
        List<ConfigurationServiceClient> clients = this.mappingConfigurationService.getAllConfigurationServiceClients();

        for(ConfigurationServiceClient client: clients)
        {
            this.addItem(client);
            this.setItemCaption(client, client.getName());
        }
    }

    /**
     * Method to refresh the combo box.
     */
    public void refresh()
    {
        ConfigurationServiceClient client = (ConfigurationServiceClient)this.getValue();
        boolean isReadOnly = this.isReadOnly();
        this.setReadOnly(false);
        this.removeAllItems();
        this.loadClientSelectValues();
        this.setValue(client);
        this.setReadOnly(isReadOnly);
    }

    /* (non-Javadoc)
     * @see com.vaadin.event.FieldEvents.FocusListener#focus(com.vaadin.event.FieldEvents.FocusEvent)
     */
    @Override
    public void focus(FocusEvent event)
    {
        this.refresh();
    }
}
