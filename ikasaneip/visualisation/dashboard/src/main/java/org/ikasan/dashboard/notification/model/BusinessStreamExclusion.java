package org.ikasan.dashboard.notification.model;

import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.exclusion.ExclusionEvent;

public class BusinessStreamExclusion {
    private IkasanSolrDocument exclusionEvent;
    private IkasanSolrDocument errorOccurrence;

    public BusinessStreamExclusion(IkasanSolrDocument exclusionEvent, IkasanSolrDocument errorOccurrence) {
        this.exclusionEvent = exclusionEvent;
        this.errorOccurrence = errorOccurrence;
    }

    public IkasanSolrDocument getExclusionEvent() {
        return exclusionEvent;
    }

    public IkasanSolrDocument getErrorOccurrence() {
        return errorOccurrence;
    }
}
