package org.ikasan.dashboard.ui.search.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;
import org.ikasan.dashboard.ui.general.component.NotificationHelper;
import org.ikasan.dashboard.ui.general.component.ProgressIndicatorDialog;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.search.model.hospital.ExclusionEventActionImpl;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.hospital.model.ExclusionEventAction;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public abstract class HospitalEventActionListener extends IkasanEventActionListener {
    private Logger logger = LoggerFactory.getLogger(HospitalEventActionListener.class);

    private String translatedEventActionMessage;
    private ErrorReportingService errorReportingService;
    private ResubmissionRestServiceImpl resubmissionRestService;
    private IkasanAuthentication ikasanAuthentication;

    public HospitalEventActionListener(String translatedEventActionMessage, ErrorReportingService errorReportingService,
                                       ModuleMetaDataService moduleMetadataService, ResubmissionRestServiceImpl resubmissionRestService,
                                       SolrSearchFilteringGrid searchResultsGrid, HashMap<String, Checkbox> selectionBoxes,
                                       HashMap<String, IkasanSolrDocument> selectionItems, IkasanAuthentication ikasanAuthentication) {
        super(moduleMetadataService, searchResultsGrid, selectionBoxes, selectionItems);
        this.translatedEventActionMessage = translatedEventActionMessage;
        if (this.translatedEventActionMessage == null) {
            throw new IllegalArgumentException("translatedEventActionMessage cannot be null!");
        }
        this.errorReportingService = errorReportingService;
        if (this.errorReportingService == null) {
            throw new IllegalArgumentException("errorReportingService cannot be null!");
        }
        this.resubmissionRestService = resubmissionRestService;
        if (this.resubmissionRestService == null) {
            throw new IllegalArgumentException("resubmissionRestService cannot be null!");
        }
        this.ikasanAuthentication = ikasanAuthentication;
        if (this.ikasanAuthentication == null) {
            throw new IllegalArgumentException("ikasanAuthentication cannot be null!");
        }
    }

    protected List<ExclusionEventAction> actionHospitalEvents(List<IkasanSolrDocument> exclusionEvents, ExclusionEventAction exclusionEventAction, ProgressIndicatorDialog progressIndicatorDialog
        , String action, String username, UI current) throws JsonProcessingException {
        ExclusionEventAction eventAction;
        ObjectMapper mapper = new ObjectMapper();
        List<ExclusionEventAction> exclusionEventActions = new ArrayList<>();

        for (IkasanSolrDocument document : exclusionEvents) {

            if (progressIndicatorDialog.isCancelled()) {
                break;
            }

            if (this.shouldActionEvent(document)) {
                ModuleMetaData moduleMetaData = super.getModuleMetaData(document.getModuleName());
                boolean result = this.resubmissionRestService.resubmit(moduleMetaData.getUrl(), document.getModuleName(),
                    document.getFlowName(), action, this.getErrorUri(document.getId()));

                if (!result) {
                    current.access(() ->
                    {
                        progressIndicatorDialog.cancel();
                        progressIndicatorDialog.close();
                        if (action.equals("resubmit")) {
                            NotificationHelper.showErrorNotification(getTranslation("message.error-bulk-resubmit-exclusions", UI.getCurrent().getLocale()));
                        } else {
                            NotificationHelper.showErrorNotification(getTranslation("message.error-bulk-ignore-exclusions", UI.getCurrent().getLocale()));
                        }
                    });

                    return exclusionEventActions;
                }

                eventAction = getExclusionEventAction(exclusionEventAction.getComment(), ExclusionEventActionImpl.RESUBMIT
                    , document, username);

                eventAction.setEvent(mapper.writeValueAsString(eventAction));

                exclusionEventActions.add(eventAction);

                logger.info("User[{{}]. Excluded event[{}]. Excluded event action[{}]. Comment[{}].", this.ikasanAuthentication.getName()
                    , document.getId(), action, exclusionEventAction.getComment());
            }
        }

        return exclusionEventActions;
    }

    /**
     * Helper method to get an initialised exclusion event action.
     *
     * @param comment
     * @param action
     * @param document
     * @param user
     * @return
     */
    protected ExclusionEventAction getExclusionEventAction(String comment, String action, IkasanSolrDocument document, String user) {
        ErrorOccurrence errorOccurrence = (ErrorOccurrence<String>) this.errorReportingService.find(this.getErrorUri(document.getId()));
        ExclusionEventAction exclusionEventAction = new ExclusionEventActionImpl();
        exclusionEventAction.setComment(comment);
        exclusionEventAction.setActionedBy(user);
        exclusionEventAction.setAction(String.format(translatedEventActionMessage, comment, action, user, errorOccurrence.getEventAsString()));
        // the error uri is in fact the id of excluded events
        exclusionEventAction.setErrorUri(document.getId());
        exclusionEventAction.setModuleName(document.getModuleName());
        exclusionEventAction.setFlowName(document.getFlowName());
        exclusionEventAction.setTimestamp(System.currentTimeMillis());
        exclusionEventAction.setEvent(document.getEvent());

        return exclusionEventAction;
    }

    protected int getNumberOfSeletedItems() {
        int count = 0;
        for (Checkbox checkbox : super.selectionBoxes.values()) {
            if (checkbox.getValue()) {
                count++;
            }
        }

        return count;
    }

    public String getTranslation(String key, Locale locale) {
        I18NProvider provider = VaadinService.getCurrent().getInstantiator().getI18NProvider();

        if (provider != null) {
            return provider.getTranslation(key, locale);
        }

        return "";
    }

    private String getErrorUri(String id){
        if(id.contains(":")) {
            id = id.substring(id.lastIndexOf(":") + 1);
        }
        return id;
    }
}
