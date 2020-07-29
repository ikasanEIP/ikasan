package org.ikasan.dashboard.notification.service;

import org.ikasan.business.stream.metadata.model.BusinessStream;
import org.ikasan.business.stream.metadata.model.Flow;
import org.ikasan.dashboard.notification.model.BusinessStreamExclusion;
import org.ikasan.dashboard.notification.model.BusinessStreamExclusions;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.metadata.BusinessStreamMetaData;
import org.ikasan.spec.metadata.BusinessStreamMetaDataService;
import org.ikasan.spec.solr.SolrGeneralService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BusinessStreamNotificationService {
    private BusinessStreamMetaDataService<BusinessStreamMetaData> businessStreamMetaDataService;
    private ErrorReportingService<ErrorOccurrence<byte[]>, ErrorOccurrence> errorReportingService;
    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrGeneralService;

    public BusinessStreamNotificationService(BusinessStreamMetaDataService businessStreamMetaDataService
        , ErrorReportingService errorReportingService, SolrGeneralService solrGeneralService) {
        this.businessStreamMetaDataService = businessStreamMetaDataService;
        this.errorReportingService = errorReportingService;
        this.solrGeneralService = solrGeneralService;
    }

    public Optional<BusinessStreamExclusions> getBusinessStreamExclusions(String businessStreamName, Long startTimestamp, Integer resultSize) {
        BusinessStreamMetaData<BusinessStream> businessStreamMetaData = this.businessStreamMetaDataService
            .findById("businessStream-" + businessStreamName);

        if(businessStreamMetaData == null) {
            return Optional.empty();
        }

        Set<String> moduleNames = businessStreamMetaData.getBusinessStream().getFlows()
            .stream()
            .map(Flow::getModuleName)
            .collect(Collectors.toSet());

        Set<String> flowNames = businessStreamMetaData.getBusinessStream().getFlows()
            .stream()
            .map(Flow::getFlowName)
            .collect(Collectors.toSet());

        IkasanSolrDocumentSearchResults results = this.solrGeneralService.search(moduleNames, flowNames
            , null, startTimestamp, System.currentTimeMillis(), resultSize, List.of("exclusion")
            ,false, null, null);

        if(results.getTotalNumberOfResults() == 0) {
            return Optional.empty();
        }

        return Optional.of(new BusinessStreamExclusions(businessStreamMetaData, this.getBusinessStreamExclusions(results)));
    }

    private List<BusinessStreamExclusion> getBusinessStreamExclusions(IkasanSolrDocumentSearchResults results) {
        if(results.getResultList().size() > 0) {
            Map<String, ErrorOccurrence> errorOccurrencesMap = results.getResultList()
                .stream()
                .map(ikasanDoc -> errorReportingService.find(ikasanDoc.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ErrorOccurrence::getUri, Function.identity()));

            List<BusinessStreamExclusion> businessStreamExclusionsList = new ArrayList<>();

            results.getResultList().forEach(ikasanSolrDocument -> {
                BusinessStreamExclusion businessStreamExclusion = new BusinessStreamExclusion(ikasanSolrDocument,
                    errorOccurrencesMap.get(ikasanSolrDocument.getId()));

                businessStreamExclusionsList.add(businessStreamExclusion);
            });

            return businessStreamExclusionsList;
        }

        return new ArrayList<>();
    }
}
