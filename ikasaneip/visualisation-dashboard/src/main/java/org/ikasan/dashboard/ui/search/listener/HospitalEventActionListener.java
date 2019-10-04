package org.ikasan.dashboard.ui.search.listener;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import org.ikasan.dashboard.ui.search.model.hospital.ExclusionEventActionImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.spec.hospital.model.ExclusionEventAction;

public abstract class HospitalEventActionListener extends IkasanEventActionListener
{
    /**
     * Helper method to get an initialised exclusion event action.
     *
     * @param comment
     * @param action
     * @param document
     * @param user
     * @return
     */
    protected ExclusionEventAction getExclusionEventAction(String comment, String action, IkasanSolrDocument document, String user)
    {
        ExclusionEventAction recordedEventAction = new ExclusionEventActionImpl();
        recordedEventAction.setComment(comment);
        recordedEventAction.setAction(action);
        recordedEventAction.setActionedBy(user);
        // the error uri is in fact the id of excluded events
        recordedEventAction.setErrorUri(document.getId());
        recordedEventAction.setEvent(document.getEvent());
        recordedEventAction.setModuleName(document.getModuleName());
        recordedEventAction.setFlowName(document.getFlowName());
        recordedEventAction.setTimestamp(System.currentTimeMillis());

        return recordedEventAction;
    }

    protected int getNumberOfSeletedItems()
    {
        int count = 0;
        for(Checkbox checkbox: super.selectionBoxes.values())
        {
           if(checkbox.getValue())
           {
               count++;
           }
        }

        return count;
    }
}
