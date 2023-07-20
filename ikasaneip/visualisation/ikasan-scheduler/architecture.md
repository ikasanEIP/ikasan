![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Architecture
## Introduction

## What is a Distributed Software System
A distributed software system is a collection of interconnected software components or applications that work together as a unified system to perform tasks or deliver services. Unlike traditional monolithic or centralized systems where all the processing happens on a single server or computer, a distributed software system splits the workload among multiple nodes, which can be geographically dispersed, interconnected through a network.

In a distributed software system, each node or server operates independently and communicates with other nodes to exchange data, share resources, and collaborate on tasks. The system's architecture is designed to distribute the processing and computing load across various nodes, which offers several benefits, including:

- **Scalability**: Distributed systems can easily accommodate increasing demand by adding more nodes to handle the workload, allowing for horizontal scaling.
- **Fault Tolerance**: Due to the decentralized nature of distributed systems, they can continue functioning even if some nodes fail or become unavailable, reducing the risk of system downtime.
- **Performance**: By distributing tasks across multiple nodes, distributed systems can achieve higher processing speeds and improved overall performance.
- **Flexibility**: Different components of the system can be developed and updated independently, making it easier to adapt and evolve the system over time.

## Understanding the Ikasan Enterprise Scheduler Architecture
The Ikasan Enterprise Scheduler Architecture comprises two distinct software elements.

### Ikasan Enterprise Scheduler Dashboard
The [Ikasan Enterprise Scheduler Dashboard](./dashboard/readme.md) 

### Ikasan Enterprise Scheduler Agent
[Ikasan Enterprise Scheduler Agent](./agent/readme.md)

![architecture](../images/architecture.png)