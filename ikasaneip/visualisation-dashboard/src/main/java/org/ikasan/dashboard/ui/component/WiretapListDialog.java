package org.ikasan.dashboard.ui.component;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionListener;
import org.ikasan.spec.wiretap.WiretapEvent;

import java.util.Date;
import java.util.List;

public class WiretapListDialog extends Dialog
{
    private Grid<WiretapEvent> grid = new Grid<>();

    public WiretapListDialog(List<WiretapEvent> events)
    {
//        // Create a grid bound to the list
//        grid.setWidth("1400px");
//        grid.setHeight("1000px");
//        grid.setItems(events);
//        grid.addColumn(WiretapEvent::getModuleName).setHeader("Module Name").setSortable(true);
//        grid.addColumn(WiretapEvent::getComponentName).setHeader("Component Name").setSortable(true);
//        grid.addColumn(WiretapEvent::getFlowName).setHeader("Flow Name").setSortable(true);
//        grid.addColumn(WiretapEvent::getEventId).setHeader("Event Identifier").setSortable(true);
//        grid.addColumn(wiretapEvent -> new Date(wiretapEvent.getTimestamp())).setHeader("Timestamp").setSortable(true);
//        grid.addSelectionListener((SelectionListener<Grid<WiretapEvent>, WiretapEvent>) selectionEvent -> {
//            if(selectionEvent.getFirstSelectedItem().isPresent())
//            {
//                EventViewDialog eventViewDialog = new EventViewDialog((String) selectionEvent.getFirstSelectedItem().get().getEvent());
//                eventViewDialog.open();
//            }
//        });
//
//
//        this.setHeight("80%");
//        this.setWidth("80%");
//
//        this.add(grid);
    }
}
