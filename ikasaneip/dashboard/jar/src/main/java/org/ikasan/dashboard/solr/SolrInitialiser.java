package org.ikasan.dashboard.solr;

import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.harvesting.HarvestingSchedulerService;
import org.ikasan.dashboard.ui.framework.constants.ConfigurationConstants;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.solr.SolrInitialisationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ikasan Development Team on 03/10/2017.
 */
public class SolrInitialiser
{
    private Logger logger = LoggerFactory.getLogger(SolrInitialiser.class);

    private List<SolrInitialisationService> solrInitialisationServices;
    private PlatformConfigurationService platformConfigurationService;
    private HarvestingSchedulerService solrHarvestingSchedulerService;

    public SolrInitialiser(List<SolrInitialisationService> solrInitialisationServices, PlatformConfigurationService platformConfigurationService,
                           HarvestingSchedulerService solrHarvestingSchedulerService)
    {
        this.solrInitialisationServices = solrInitialisationServices;
        if(this.solrInitialisationServices == null)
        {
            throw new IllegalArgumentException("solrInitialisationServices cannot be null!");
        }
        this.platformConfigurationService = platformConfigurationService;
        if(this.platformConfigurationService == null)
        {
            throw new IllegalArgumentException("platformConfigurationService cannot be null!");
        }
        this.solrHarvestingSchedulerService = solrHarvestingSchedulerService;
        if(this.platformConfigurationService == null)
        {
            throw new IllegalArgumentException("solrHarvestingSchedulerService cannot be null!");
        }
    }

    public boolean initialiseSolr()
    {
        String daysToLiveString = platformConfigurationService.getConfigurationValue(ConfigurationConstants.SOLR_DAYS_TO_KEEP);
        String solrUrls = platformConfigurationService.getConfigurationValue(ConfigurationConstants.SOLR_URLS);

        if (solrUrls == null)
        {
            logger.info("Solr URLs are not configured! Setting solr enabled to false");
            platformConfigurationService.saveConfigurationValue(ConfigurationConstants.SOLR_ENABLED, "false");

            return false;
        }
        else
        {
            List<String> solrUrlsList = this.tokens(solrUrls, ',');

            Integer daysToLive = 7;

            try
            {
                daysToLive = Integer.parseInt(daysToLiveString);
            } catch (Exception e)
            {
                logger.info("Could not initialise solr days to live, Using default of " + daysToLive);
            }

            for (SolrInitialisationService solrInitialisationService : solrInitialisationServices)
            {
                try
                {
                    String operatingMode = platformConfigurationService.getConfigurationValue(ConfigurationConstants.SOLR_OPERATING_MODE);

                    if(operatingMode == null || operatingMode.equals("Standalone"))
                    {
                        solrInitialisationService.initStandalone(solrUrls, daysToLive);
                    }
                    else
                    {
                        solrInitialisationService.initCloud(solrUrlsList, daysToLive);
                    }
                }
                catch (Exception e)
                {
                    logger.warn(String.format("Solr initialisation has been unsuccessful. It appears that Solr is not running. " +
                        "Attempted to connect to the following Solr URLs[s].", solrUrlsList));
                    platformConfigurationService.saveConfigurationValue(ConfigurationConstants.SOLR_ENABLED, "false");

                    return false;
                }
            }

            solrHarvestingSchedulerService.registerJobs();
            solrHarvestingSchedulerService.startScheduler();

            logger.info("Solr has been successfully initialised!");

            return true;
        }
    }

    private List<String> tokens(String tokenString, char tokenChar)
    {
        Splitter splitter = Splitter.on(tokenChar).omitEmptyStrings().trimResults();

        ArrayList<String> results = new ArrayList<String>();

        Iterable<String> tokens = splitter.split(tokenString);

        for(String token: tokens)
        {
            results.add(token);
        }

        return results;
    }
}
