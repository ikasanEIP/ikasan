![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Scheduler Agent Design
                                                                                 
                                                                           
![IKASAN](../../../developer/docs/quickstart-images/scheduler-agent-flow.png)

## Deploying a Scheduler Agent
All Ikasan binaries are available for download at [Maven Central](https://search.maven.org/search?q=org.ikasan)

In order to deploy a scheduler agent search for scheduler-agent-distribution and download the desired version which is bundled as a zip file.

| Filename | Description  |
| ---  | --- |
| simple-env.sh | Unix/Linux environment file in which environment variables required by the agent can be set. |
| simple-env.bat | Windows environment file in which environment variables required by the agent can be set. |
| config-service-env.bat | Windows environment file in which environment variables required by the agent can be set when running the agent in combination with [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/reference/html/). |
| config-service-env.sh | Unix/Linux environment file in which environment variables required by the agent can be set when running the agent in combination with [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/reference/html/). |
| ikasan-config-service.bat | Windows bat file that allows the agent to be started and stopped in a Windows environment when using [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/reference/html/). |
| ikasan-config-service.sh | Shell script that allows the agent to be started and stopped in a Unix/Linux environment when using [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/reference/html/). |
| ikasan-simple.bat  | Windows bat file that allows the agent to be started and stopped in a Windows environment. |
| ikasan-simple.sh  | Shell script that allows the agent to be started and stopped in a Unix/Linux environment |
| config/logback-spring.xml | Logging configuration file |
| config/application.properties | Scheduler agent properties file |
| lib/scheduler-agent-3.2.0.jar | Uber jar containing all libraries required by the scheduler agent | 
| lib/h2-1.4.200.jar | H2 database libraries used by the agent |
| lib/ikasan-shell-3.2.0.jar | The [Ikasan cli shell library](../../../cli/shell/jar/Readme.md) |

                                                                            

