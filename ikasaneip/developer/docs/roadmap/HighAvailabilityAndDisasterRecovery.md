![Problem Domain](../quickstart-images/Ikasan-title-transparent.png)

## Theme 5: High Availability and Disaster Recovery

This theme focuses on enhancing Ikasan's capabilities for high availability (HA) and disaster recovery (DR) to ensure continuous operation and data integrity in critical environments.

*   **Goal: Robust Persistence with SOLR Cloud**
    *   **Action:** Explore and implement integration with SOLR Cloud for highly available and scalable persistence of Ikasan's operational data (e.g., event life cycle, error reporting, and metrics).
    *   **Why:** Provides a distributed, fault-tolerant, and scalable search and storage solution for critical operational data, essential for HA/DR.

    ```mermaid
    graph TD
        subgraph SolrCloud
            Z1[ZooKeeper 1] --- S1(Solr Node 1)
            Z2[ZooKeeper 2] --- S2(Solr Node 2)
            Z3[ZooKeeper 3] --- S3(Solr Node 3)
            S1 --- S2
            S2 --- S3
        end
        subgraph Ikasan Dashboard
            SC1[Solr Cloud Client] --> S1
            SC1 --> S2
            SC1 --> S3 
            ID1[Ikasan Dashboard 1] --> SC1
            ID2[Ikasan Dashboard 2] --> SC1
            ID3[Ikasan Dashboard 3] --> SC1
        end
        LB[Sticky Session Load Balancer]
        LB --> ID1
        LB --> ID2
        LB --> ID3
    ```
    ```mermaid
    graph TD
        subgraph SolrCloud
            Z1[ZooKeeper 1] --- S1(Solr Node 1)
            Z2[ZooKeeper 2] --- S2(Solr Node 2)
            Z3[ZooKeeper 3] --- S3(Solr Node 3)
            S1 --- S2
            S2 --- S3
        end
        subgraph Ikasan Dashboard
            DS[Distributed Session Cache] --> ID1
            DS --> ID2
            DS --> ID3 
            SC1[Solr Cloud Client] --> S1
            SC1 --> S2
            SC1 --> S3 
            ID1[Ikasan Dashboard 1] --> SC1
            ID2[Ikasan Dashboard 2] --> SC1
            ID3[Ikasan Dashboard 3] --> SC1
        end
        LB[Load Balancer] --> DS
        
    ```    

*   **Goal: Decoupled Dashboard Services**
    *   **Action:** Refactor the `ikasan-dashboard` REST services into separate, standalone, stateless runtimes.
    *   **Why:** Improves scalability, resilience, and independent deployability of dashboard functionalities, allowing for better resource utilization and easier maintenance in HA setups.

*   **Goal: Distributed Job Orchestration**
    *   **Action:** Decouple the `ikasan-dashboard`'s job orchestration engine into separate, distributed runtimes.
    *   **Why:** Enhances the scalability and fault tolerance of job scheduling and execution, preventing single points of failure and improving overall system reliability.

*   **Goal: H2 Clustering**
    *   **Action:** Investigate and implement H2 database clustering for high availability.
    *   **Why:** Provides a lightweight database solution with HA capabilities for smaller deployments or specific use cases where external databases are not feasible.

*   **Goal: Active-Active Deployment Model**
    *   **Action:** Develop support for an active-active deployment model for Ikasan modules, allowing multiple instances of a module to process messages concurrently.
    *   **Why:** Improves throughput, reduces latency, and provides seamless failover in case of instance failure.

*   **Goal: Disaster Recovery Orchestration**
    *   **Action:** Provide tooling and guidance for orchestrating Ikasan module deployments across geographically dispersed data centers for disaster recovery.
    *   **Why:** Ensures business continuity and minimizes downtime in the event of a regional outage.

*   **Goal: Cloud-Native HA/DR Patterns**
    *   **Action:** Document and provide examples of deploying Ikasan modules with cloud-native HA/DR services (e.g., Kubernetes, AWS Auto Scaling Groups, Azure Availability Zones).
    *   **Why:** Leverages cloud infrastructure for automated scaling, self-healing, and resilience.

