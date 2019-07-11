package org.ikasan.dashboard.ui.component;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionListener;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class ErrorListDialog extends Dialog
{
    Logger logger = LoggerFactory.getLogger(ErrorListDialog.class);

    private Grid<ErrorOccurrence> grid = new Grid<>();

    public ErrorListDialog(List<ErrorOccurrence> errorOccurrences, ErrorReportingService solrErrorReportingService)
    {
        // Create a grid bound to the list
//        grid.setWidth("1400px");
//        grid.setHeight("1000px");
//        grid.setItems(errorOccurrences);
//        grid.addColumn(ErrorOccurrence::getModuleName).setHeader("Module Name").setSortable(true);
//        grid.addColumn(ErrorOccurrence::getFlowElementName).setHeader("Component Name").setSortable(true);
//        grid.addColumn(ErrorOccurrence::getFlowName).setHeader("Flow Name").setSortable(true);
//        grid.addColumn(ErrorOccurrence::getUri).setHeader("Error URI").setSortable(true);
//        grid.addColumn(error -> new Date(error.getTimestamp())).setHeader("Timestamp").setSortable(true);
//        grid.addSelectionListener((SelectionListener<Grid<ErrorOccurrence>, ErrorOccurrence>) selectionEvent ->
//        {
//            if(selectionEvent.getFirstSelectedItem().isPresent())
//            {
//                logger.info("Error message:" + selectionEvent.getFirstSelectedItem().get());
//                ErrorOccurrence errorOccurrence = (ErrorOccurrence) solrErrorReportingService.find(selectionEvent.getFirstSelectedItem().get().getUri());
//                EventViewDialog eventViewDialog = new EventViewDialog(errorOccurrence.getErrorMessage() + "\r\n" + errorOccurrence.getErrorDetail());
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
