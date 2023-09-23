![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Dashboard

The Ikasan Enterprise Scheduler Dashboard is a [Spring Boot](https://spring.io/projects/spring-boot) web application. It 
provides management and monitoring capabilities for all scheduler related features. These features include:
- [Job plan template management](./job-plans/job-plan-templates.md)
- [Job plan instance monitoring and management](./job-plans/job-plan-templates.md)
- Scheduler agent monitoring and management
- LDAP integration
- [Security management](../../dashboard/security.md)
- Core Ikasan service ([hospital](../../../hospital/Readme.md), [replay](../../../replay/Readme.md), [wiretap](../../../wiretap/Readme.md), [configuration](../../../configuration-service/Readme.md), [mapping](../../../mapping/Readme.md))
- [Various rest services related to scheduler features](../rest/scheduler-dashboard-rest-services.md)
- [Context machine cache](../job-orchestration/core/context-machine-cache.md) managing access to all running job plan instances


![img.png](../../images/ikasan-scheduler-dashboard-with-context-machine-cache-white.png)

*Ikasan Enterprise Scheduler Dashboard and associated components*

The Ikasan Enterprise Scheduler Dashboard also delegates to the following software elements to provide the following features:

- [Solr](https://solr.apache.org/) providing scheduler artefact persistence, indexing and search features
- [H2](https://www.h2database.com/html/main.html) providing relational persistence of dashboard security features
- [BigQueue](https://github.com/ikasanEIP/bigqueue) which provides disk backed messaging and persistence, providing reliable interprocess between the scheduler dashboard and the scheduler agents


