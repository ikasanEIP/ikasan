package org.ikasan.dashboard;

import org.ikasan.dashboard.dto.FlowStateImpl;
import org.ikasan.dashboard.dto.SearchResultsImpl;
import org.ikasan.dashboard.dto.error.ErrorOccurrenceImpl;
import org.ikasan.dashboard.dto.exclusion.ExclusionEventImpl;
import org.ikasan.dashboard.dto.metadata.configuration.ConfigurationMetaDataImpl;
import org.ikasan.dashboard.dto.metadata.configuration.ConfigurationParameterMetaDataImpl;
import org.ikasan.dashboard.dto.metadata.module.*;
import org.ikasan.dashboard.dto.replay.ReplayEventImpl;
import org.ikasan.dashboard.dto.wiretap.WiretapEventImpl;
import org.ikasan.spec.dashboard.DataSharingRestService;
import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.flow.FlowState;
import org.ikasan.spec.metadata.model.*;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.search.SearchResults;
import org.ikasan.spec.search.model.IkasanESBDocument;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST client implementation for ESB data sharing APIs.
 * Provides methods to query wiretap, errors, exclusions, replays, topology, and configuration data
 * from the upstream dashboard for data sharing purposes.
 */
public class DataSharingRestServiceImpl extends AbstractRestServiceImpl implements DataSharingRestService {

    public static final String DASHBOARD_BASE_URL_PROPERTY = "ikasan.dashboard.base.url";
    public static final String DASHBOARD_USERNAME_PROPERTY = "ikasan.dashboard.rest.username";
    public static final String DASHBOARD_PASSWORD_PROPERTY = "ikasan.dashboard.rest.password";
    public static final String DASHBOARD_REST_USERAGENT = "ikasan.dashboard.rest.useragent";

    public static final String DATA_SHARING_PATH = "/rest/data-sharing";

    // Query endpoints
    public static final String WIRETAP_PATH = DATA_SHARING_PATH + "/wiretap";
    public static final String ERRORS_PATH = DATA_SHARING_PATH + "/errors";
    public static final String EXCLUSIONS_PATH = DATA_SHARING_PATH + "/exclusions";
    public static final String REPLAYS_PATH = DATA_SHARING_PATH + "/replays";
    public static final String MODULE_METADATA_PATH = DATA_SHARING_PATH + "/module-metadata";
    public static final String CONFIGURATION_PATH = DATA_SHARING_PATH + "/configuration";

    // Count endpoints
    public static final String WIRETAP_COUNT_PATH = WIRETAP_PATH + "/count";
    public static final String ERRORS_COUNT_PATH = ERRORS_PATH + "/count";
    public static final String EXCLUSIONS_COUNT_PATH = EXCLUSIONS_PATH + "/count";
    public static final String REPLAYS_COUNT_PATH = REPLAYS_PATH + "/count";

    // Flow states endpoint
    public static final String FLOW_STATES_PATH = DATA_SHARING_PATH + "/flowstates";

    private final String userAgent;
    private final JsonMapper mapper;

    public DataSharingRestServiceImpl(Environment environment, HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory) {
        restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);

        JsonMapper mapper = JsonMapper.builder()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .build();

        JacksonJsonHttpMessageConverter jsonHttpMessageConverter = new JacksonJsonHttpMessageConverter(mapper);
        restTemplate.getMessageConverters().addFirst(jsonHttpMessageConverter);

        super.url = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY);
        super.authenticateUrl = environment.getProperty(DASHBOARD_BASE_URL_PROPERTY) + "/authenticate";
        super.username = environment.getProperty(DASHBOARD_USERNAME_PROPERTY);
        super.password = environment.getProperty(DASHBOARD_PASSWORD_PROPERTY);
        this.userAgent = environment.getProperty(DASHBOARD_REST_USERAGENT);

        final var simpleModule = new SimpleModule()
            .addAbstractTypeMapping(ConfigurationParameterMetaData.class, ConfigurationParameterMetaDataImpl.class)
            .addAbstractTypeMapping(FlowMetaData.class, FlowMetaDataImpl.class)
            .addAbstractTypeMapping(FlowElementMetaData.class, FlowElementMetaDataImpl.class)
            .addAbstractTypeMapping(Transition.class, TransitionImpl.class)
            .addAbstractTypeMapping(DecoratorMetaData.class, DecoratorMetaDataImpl.class);

        this.mapper = JsonMapper.builder()
            .addModule(simpleModule)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();
    }

    /**
     * Query wiretap data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    @Override
    public SearchResults<WiretapEvent> queryWiretap(long fromTimestamp, long toTimestamp, List<String> moduleNames, int limit, int offset) {
        return queryDataBase(WIRETAP_PATH, fromTimestamp, toTimestamp, moduleNames, limit, offset
            , true, WiretapEventImpl.class);
    }

    /**
     * Query error data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    @Override
    public SearchResults<ErrorOccurrence> queryErrors(long fromTimestamp, long toTimestamp, List<String> moduleNames, int limit, int offset) {
        return queryDataBase(ERRORS_PATH, fromTimestamp, toTimestamp, moduleNames, limit, offset
            , true, ErrorOccurrenceImpl.class);
    }

    /**
     * Query exclusion data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    @Override
    public SearchResults<ExclusionEvent> queryExclusions(long fromTimestamp, long toTimestamp, List<String> moduleNames, int limit, int offset) {
        return queryDataBase(EXCLUSIONS_PATH, fromTimestamp, toTimestamp, moduleNames, limit, offset
            , true, ExclusionEventImpl.class);
    }

    /**
     * Query replay data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    @Override
    public SearchResults<ReplayEvent> queryReplays(long fromTimestamp, long toTimestamp, List<String> moduleNames, int limit, int offset) {
        return queryDataBase(REPLAYS_PATH, fromTimestamp, toTimestamp, moduleNames, limit, offset
            , true, ReplayEventImpl.class);
    }

    /**
     * Query module metadata for data sharing
     *
     * @param moduleNames filter by module names (required)
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    @Override
    public SearchResults<ModuleMetaData> queryModuleMetadata(List<String> moduleNames, int limit, int offset) {
        return queryModuleMetadataBase(MODULE_METADATA_PATH, moduleNames, limit, offset, true);
    }

    /**
     * Query configuration data for data sharing
     *
     * @param configurationIdentifiers filter by configuration identifiers (required)
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    @Override
    public SearchResults<ConfigurationMetaData> queryConfiguration(List<String> configurationIdentifiers, int limit, int offset) {
        return queryConfigurationBase(CONFIGURATION_PATH, configurationIdentifiers, limit, offset, true);
    }

    /**
     * Count wiretap data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @return count of records
     */
    @Override
    public long countWiretap(long fromTimestamp, long toTimestamp, List<String> moduleNames) {
        return countDataBase(WIRETAP_COUNT_PATH, fromTimestamp, toTimestamp, moduleNames, true);
    }

    /**
     * Count error data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @return count of records
     */
    @Override
    public long countErrors(long fromTimestamp, long toTimestamp, List<String> moduleNames) {
        return countDataBase(ERRORS_COUNT_PATH, fromTimestamp, toTimestamp, moduleNames, true);
    }

    /**
     * Count exclusion data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @return count of records
     */
    @Override
    public long countExclusions(long fromTimestamp, long toTimestamp, List<String> moduleNames) {
        return countDataBase(EXCLUSIONS_COUNT_PATH, fromTimestamp, toTimestamp, moduleNames, true);
    }

    /**
     * Count replay data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @return count of records
     */
    @Override
    public long countReplays(long fromTimestamp, long toTimestamp, List<String> moduleNames) {
        return countDataBase(REPLAYS_COUNT_PATH, fromTimestamp, toTimestamp, moduleNames, true);
    }

    /**
     * Get flow states for data sharing
     *
     * @param moduleNames optional filter by module names
     * @return List of FlowState objects
     */
    @Override
    public List<FlowState> getFlowStates(List<String> moduleNames) {
        return getFlowStatesBase(FLOW_STATES_PATH, moduleNames, true);
    }

    /**
     * Base method for querying data from data sharing endpoints
     *
     * @param path the API endpoint path
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @param limit maximum records to return
     * @param offset pagination offset
     * @param isFirst flag indicating if this is the first attempt (for re-authentication)
     * @return SearchResults containing search results and metadata
     * @throws RuntimeException if there are issues with the HTTP request or response parsing
     */
    private SearchResults queryDataBase(String path, long fromTimestamp, long toTimestamp,
                                        List<String> moduleNames, int limit, int offset, boolean isFirst,
                                        Class entityClass) {
        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity entity = new HttpEntity(headers);

        try {
            // Build URI with query parameters
            StringBuilder uri = new StringBuilder(url + path);
            uri.append("?fromTimestamp=").append(fromTimestamp);
            uri.append("&toTimestamp=").append(toTimestamp);
            uri.append("&limit=").append(limit);
            uri.append("&offset=").append(offset);

            if (moduleNames != null && !moduleNames.isEmpty()) {
                for (String moduleName : moduleNames) {
                    uri.append("&moduleNames=").append(moduleName);
                }
            }

            ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);

            // Parse the response Map
            Map<String, Object> responseMap = this.mapper.readValue(response.getBody(),
                mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));

            // Extract data and convert to IkasanESBDocument list
            List<IkasanESBDocument> documents = this.mapper.convertValue(responseMap.get("data"),
                mapper.getTypeFactory().constructCollectionType(List.class, entityClass));

            long totalCount = ((Number) responseMap.get("totalCount")).longValue();

            // Create and return SearchResults
            return new SearchResultsImpl(documents, totalCount, 0);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatusCode.valueOf(401)) && isFirst) {
                this.token = null;
                if (authenticate(this.userAgent)) {
                    return queryDataBase(path, fromTimestamp, toTimestamp, moduleNames, limit, offset, false, entityClass);
                }
            }

            logger.warn("Issue querying data for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue querying data for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]", e);
        } catch (RestClientException | JacksonException e) {
            logger.warn("Issue querying data for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue querying data for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]", e);
        }
    }

    /**
     * Base method for counting data from data sharing endpoints
     *
     * @param path the API endpoint path
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @param isFirst flag indicating if this is the first attempt (for re-authentication)
     * @return count of records
     * @throws RuntimeException if there are issues with the HTTP request or response parsing
     */
    private long countDataBase(String path, long fromTimestamp, long toTimestamp,
                               List<String> moduleNames, boolean isFirst) {
        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity entity = new HttpEntity(headers);

        try {
            // Build URI with query parameters
            StringBuilder uri = new StringBuilder(url + path);
            uri.append("?fromTimestamp=").append(fromTimestamp);
            uri.append("&toTimestamp=").append(toTimestamp);

            if (moduleNames != null && !moduleNames.isEmpty()) {
                for (String moduleName : moduleNames) {
                    uri.append("&moduleNames=").append(moduleName);
                }
            }

            ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);

            Map<String, Object> result = this.mapper.readValue(response.getBody(),
                mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));

            return ((Number) result.get("count")).longValue();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatusCode.valueOf(401)) && isFirst) {
                this.token = null;
                if (authenticate(this.userAgent)) {
                    return countDataBase(path, fromTimestamp, toTimestamp, moduleNames, false);
                }
            }

            logger.warn("Issue counting data for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue counting data for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]", e);
        } catch (Exception e) {
            logger.warn("Issue counting data for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue counting data for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]", e);
        }
    }

    /**
     * Base method for querying module metadata from data sharing endpoints
     *
     * @param path the API endpoint path
     * @param moduleNames filter by module names (required)
     * @param limit maximum records to return
     * @param offset pagination offset
     * @param isFirst flag indicating if this is the first attempt (for re-authentication)
     * @return SearchResults containing search results and metadata
     * @throws RuntimeException if there are issues with the HTTP request or response parsing
     */
    private SearchResults queryModuleMetadataBase(String path, List<String> moduleNames,
                                                  int limit, int offset, boolean isFirst) {
        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity entity = new HttpEntity(headers);

        try {
            // Build URI with query parameters
            StringBuilder uri = new StringBuilder(url + path);
            uri.append("?limit=").append(limit);
            uri.append("&offset=").append(offset);

            if (moduleNames != null && !moduleNames.isEmpty()) {
                for (String moduleName : moduleNames) {
                    uri.append("&moduleNames=").append(moduleName);
                }
            }

            ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);

            // Parse the response Map
            Map<String, Object> responseMap = this.mapper.readValue(response.getBody(),
                mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));

            // Extract data and convert to IkasanESBDocument list
            List<IkasanESBDocument> documents = this.mapper.convertValue(responseMap.get("data"),
                mapper.getTypeFactory().constructCollectionType(List.class, ModuleMetaDataImpl.class));

            long totalCount = ((Number) responseMap.get("totalCount")).longValue();

            // Create and return SearchResults
            return new SearchResultsImpl(documents, totalCount, 0);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatusCode.valueOf(401)) && isFirst) {
                this.token = null;
                if (authenticate(this.userAgent)) {
                    return queryModuleMetadataBase(path, moduleNames, limit, offset, false);
                }
            }

            logger.warn("Issue querying module metadata for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue querying module metadata for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]", e);
        } catch (RestClientException | JacksonException e) {
            logger.warn("Issue querying module metadata for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue querying module metadata for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]", e);
        }
    }

    /**
     * Base method for querying configuration data from data sharing endpoints
     *
     * @param path the API endpoint path
     * @param configurationIdentifiers filter by configuration identifiers (required)
     * @param limit maximum records to return
     * @param offset pagination offset
     * @param isFirst flag indicating if this is the first attempt (for re-authentication)
     * @return SearchResults containing search results and metadata
     * @throws RuntimeException if there are issues with the HTTP request or response parsing
     */
    private SearchResults queryConfigurationBase(String path, List<String> configurationIdentifiers,
                                                 int limit, int offset, boolean isFirst) {
        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity entity = new HttpEntity(headers);

        try {
            // Build URI with query parameters
            StringBuilder uri = new StringBuilder(url + path);
            uri.append("?limit=").append(limit);
            uri.append("&offset=").append(offset);

            if (configurationIdentifiers != null && !configurationIdentifiers.isEmpty()) {
                for (String configId : configurationIdentifiers) {
                    uri.append("&configurationIdentifiers=").append(configId);
                }
            }

            ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);

            // Parse the response Map
            Map<String, Object> responseMap = this.mapper.readValue(response.getBody(),
                mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));

            // Extract data and convert to IkasanESBDocument list
            List<IkasanESBDocument> documents = this.mapper.convertValue(responseMap.get("data"),
                mapper.getTypeFactory().constructCollectionType(List.class, ConfigurationMetaDataImpl.class));

            long totalCount = ((Number) responseMap.get("totalCount")).longValue();

            // Create and return SearchResults
            return new SearchResultsImpl(documents, totalCount, 0);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatusCode.valueOf(401)) && isFirst) {
                this.token = null;
                if (authenticate(this.userAgent)) {
                    return queryConfigurationBase(path, configurationIdentifiers, limit, offset, false);
                }
            }

            logger.warn("Issue querying configuration for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue querying configuration for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]", e);
        } catch (RestClientException | JacksonException e) {
            logger.warn("Issue querying configuration for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue querying configuration for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]", e);
        }
    }

    /**
     * Base method for querying flow states from data sharing endpoints
     *
     * @param path the API endpoint path
     * @param moduleNames optional filter by module names
     * @param isFirst flag indicating if this is the first attempt (for re-authentication)
     * @return List of FlowState objects
     * @throws RuntimeException if there are issues with the HTTP request or response parsing
     */
    private List<FlowState> getFlowStatesBase(String path, List<String> moduleNames, boolean isFirst) {
        HttpHeaders headers = super.createHttpHeaders(userAgent);
        HttpEntity entity = new HttpEntity(headers);

        try {
            // Build URI with query parameters
            StringBuilder uri = new StringBuilder(url + path);
            boolean firstParam = true;

            if (moduleNames != null && !moduleNames.isEmpty()) {
                for (String moduleName : moduleNames) {
                    if (firstParam) {
                        uri.append("?");
                        firstParam = false;
                    } else {
                        uri.append("&");
                    }
                    uri.append("moduleNames=").append(moduleName);
                }
            }

            ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);

            // Parse the response as a list of FlowState objects
            List<FlowState> flowStates = this.mapper.readValue(response.getBody(),
                mapper.getTypeFactory().constructCollectionType(List.class, FlowStateImpl.class));

            return flowStates;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatusCode.valueOf(401)) && isFirst) {
                this.token = null;
                if (authenticate(this.userAgent)) {
                    return getFlowStatesBase(path, moduleNames, false);
                }
            }

            logger.warn("Issue querying flow states for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue querying flow states for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]", e);
        } catch (RestClientException | JacksonException e) {
            logger.warn("Issue querying flow states for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]");
            throw new RuntimeException("Issue querying flow states for url [" + url + path + "] with response [{" + e.getLocalizedMessage() + "}]", e);
        }
    }

}
