[../](../../Readme.md)
![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# REST Dashboard Client

The REST Dashboard Client provides a collection of service clients to interact with the Ikasan Dashboard REST API endpoints. These clients enable programmatic access to dashboard functionality including metrics, data harvesting, and security provisioning.

## Service Clients

### 1. DashboardRestServiceImpl

REST client for publishing harvest events to the dashboard.

**Interface**: `DashboardRestService<T>`

**Key Features**:
- Publishes harvest events (wiretap, errors, metrics) to the dashboard
- Supports optional data transformation via `Converter`
- Configurable enable/disable via properties
- Automatic authentication with JWT token support
- Exception bubbling control for error handling

**Configuration Properties**:
```properties
# Enable/disable dashboard data extraction (default: false)
ikasan.dashboard.extract.enabled=true

# Dashboard base URL
ikasan.dashboard.base.url=http://localhost:9080

# Authentication credentials
ikasan.dashboard.rest.username=admin
ikasan.dashboard.rest.password=admin

# Module name (used as user agent)
module.name=MyModule

# Bubble exceptions to caller (default: false)
ikasan.dashboard.extract.exceptions=false
```

**Usage Example**:
```java
DashboardRestService<List<HarvestEvent>> dashboardService =
    new DashboardRestServiceImpl<>(environment, httpFactory, "/rest/harvest", converter);

List<HarvestEvent> events = // ... create events
dashboardService.publish(events);
```

---

### 2. MetricsRestServiceImpl

REST client for retrieving flow invocation metrics from the dashboard.

**Interface**: `MetricsService<FlowInvocationMetric>`

**Key Features**:
- Retrieve metrics by time range
- Filter metrics by module name and/or flow name
- Support for paginated results
- Count metrics without fetching full data
- Automatic authentication with JWT token support
- Re-authentication on 401 responses

**Configuration Properties**:
```properties
# Dashboard base URL
ikasan.dashboard.base.url=http://localhost:9080

# Authentication credentials
ikasan.dashboard.rest.username=admin
ikasan.dashboard.rest.password=admin

# User agent identifier
ikasan.dashboard.rest.useragent=MyApplication
```

**API Methods**:
```java
// Get all metrics in time range
List<FlowInvocationMetric> getMetrics(long startTime, long endTime)

// Get metrics for specific module
List<FlowInvocationMetric> getMetrics(String moduleName, long startTime, long endTime)

// Get metrics for specific module and flow
List<FlowInvocationMetric> getMetrics(String moduleName, String flowName, long startTime, long endTime)

// Paginated queries
List<FlowInvocationMetric> getMetricsPaged(long startTime, long endTime, int offset, int limit)
List<FlowInvocationMetric> getMetricsPaged(String moduleName, long startTime, long endTime, int offset, int limit)
List<FlowInvocationMetric> getMetricsPaged(String moduleName, String flowName, long startTime, long endTime, int offset, int limit)

// Count queries
long countMetrics(long startTime, long endTime)
long countMetrics(String moduleName, long startTime, long endTime)
long countMetrics(String moduleName, String flowName, long startTime, long endTime)
```

**Usage Example**:
```java
MetricsService<FlowInvocationMetric> metricsService =
    new MetricsRestServiceImpl(environment, httpFactory);

// Get last hour's metrics
long endTime = System.currentTimeMillis();
long startTime = endTime - 3600000; // 1 hour ago
List<FlowInvocationMetric> metrics = metricsService.getMetrics(startTime, endTime);

// Get paginated results
List<FlowInvocationMetric> page = metricsService.getMetricsPaged(startTime, endTime, 0, 100);

// Get count
long count = metricsService.countMetrics(startTime, endTime);
```

---

### 3. EntitySecurityProvisionServiceImpl

REST client for provisioning Ikasan entity security (assigning modules and job plans to roles).

**Interface**: `EntitySecurityProvisionService`

**Key Features**:
- Assign modules to multiple roles
- Assign job plans to multiple roles
- Automatic authentication with JWT token support
- Re-authentication on 401 responses
- Comprehensive input validation
- Detailed error messages and logging

**Configuration Properties**:
```properties
# Dashboard base URL
ikasan.dashboard.base.url=http://localhost:9080

# Authentication credentials
ikasan.dashboard.rest.username=admin
ikasan.dashboard.rest.password=admin

# User agent identifier
ikasan.dashboard.rest.useragent=SecurityProvisioner
```

**API Methods**:
```java
// Assign a module to multiple roles
void assignModuleToRoles(String moduleName, List<String> roleNames)
    throws EntitySecurityProvisionServiceException

// Assign a job plan to multiple roles
void assignJobPlanToRoles(String jobPlanName, List<String> roleNames)
    throws EntitySecurityProvisionServiceException
```

**Usage Example**:
```java
EntitySecurityProvisionService securityService =
    new EntitySecurityProvisionServiceImpl(environment, httpFactory);

// Assign module to roles
List<String> roles = Arrays.asList("ADMIN", "OPERATOR", "VIEWER");
securityService.assignModuleToRoles("PaymentModule", roles);

// Assign job plan to roles
securityService.assignJobPlanToRoles("NightlyBatch", Arrays.asList("SCHEDULER_ADMIN"));
```

**Validation**:
- Module/job plan names cannot be null or empty
- Role names list cannot be null or empty
- Throws `EntitySecurityProvisionServiceException` on errors

---

## Common Features

### Authentication

All service clients extend `AbstractRestServiceImpl` which provides:

- **JWT Token Authentication**: Automatic authentication using username/password
- **Token Caching**: Tokens are cached and reused across requests
- **Auto Re-authentication**: Automatic re-authentication on 401 responses
- **Configurable Credentials**: Username and password via Spring Environment properties

### HTTP Configuration

HTTP connection timeouts can be configured using the following properties:

```properties
# Read timeout (milliseconds)
module.rest.connection.readTimeout=5000

# Connect timeout (milliseconds)
module.rest.connection.connectTimeout=5000

# Connection request timeout (milliseconds)
module.rest.connection.connectionRequestTimeout=5000
```

### Error Handling

All clients provide:
- Comprehensive exception handling
- Detailed error logging
- Specific exception types for different error scenarios
- HTTP status code checking and appropriate responses

### Spring Integration

All service clients are designed for Spring Boot integration:

```java
@Configuration
public class DashboardClientConfig {

    @Bean
    public HttpComponentsClientHttpRequestFactory httpFactory() {
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setConnectionRequestTimeout(5000);
        return factory;
    }

    @Bean
    public MetricsService<FlowInvocationMetric> metricsService(
            Environment environment,
            HttpComponentsClientHttpRequestFactory httpFactory) {
        return new MetricsRestServiceImpl(environment, httpFactory);
    }

    @Bean
    public EntitySecurityProvisionService securityProvisionService(
            Environment environment,
            HttpComponentsClientHttpRequestFactory httpFactory) {
        return new EntitySecurityProvisionServiceImpl(environment, httpFactory);
    }

    @Bean
    public DashboardRestService<List<HarvestEvent>> dashboardService(
            Environment environment,
            HttpComponentsClientHttpRequestFactory httpFactory,
            Converter converter) {
        return new DashboardRestServiceImpl<>(
            environment, httpFactory, "/rest/harvest", converter);
    }
}
```

## Security Considerations

- **Credentials**: Store credentials securely using encrypted properties or secret management systems
- **HTTPS**: Always use HTTPS in production environments
- **Token Management**: Tokens are managed automatically but are stored in memory
- **Authentication Failures**: Failed authentication attempts are logged for security monitoring

## Dependencies

```xml
<dependency>
    <groupId>org.ikasan</groupId>
    <artifactId>ikasan-rest-dashboard-client</artifactId>
    <version>${ikasan.version}</version>
</dependency>
```

## See Also

- [Ikasan Dashboard REST API Documentation](../rest-dashboard/Readme.md)
- [Ikasan Security Configuration](../../spec/service/security/Readme.md)
- [Ikasan Metrics Documentation](../../spec/service/metrics/Readme.md)
