[../](./Readme.md)
[![Build Status](https://travis-ci.org/ikasanEIP/ikasan.svg?branch=master)](https://travis-ci.org/ikasanEIP/ikasan)

![Problem Domain](ikasaneip/developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Open Source Enterprise Integration Platform

The Ikasan Enterprise Integration Platform (EIP) addresses the problem 
domain most commonly known as Enterprise Application Integration (EAI). 

Enterprise application integration can be, and already has been, 
approached a number of different ways by a number of projects/vendors, 
both Open Source and closed commercial frameworks. 

It is the intention of the Ikasan Enterprise Integration Platform 
to address this domain as commoditised configurable solutions rather 
than another development framework.


# Problem Domain

* System integration can be exponentially complex
* It is commonly agreed that “spaghetti integration” is bad
* It results in a bleed out of APIs; data syntax; and business semantics
* Ripple effect of change is massive
![Problem Domain](ikasaneip/developer/docs/quickstart-images/problem-domain.png) 


# Solution

* Use Standard Enterprise Application Integration (EAI) approach
* Provision of an Event/Service backbone
* Single point of integration for Enterprise Information System (EIS) business flows
* Ripple effect of change is localised and well understood (assuming best practice)
* Ikasan adopts the standard EAI approach and helps users solve integration problems by building applications constructed of modules/flows/components
![Problem Domain](ikasaneip/developer/docs/quickstart-images/solution.png) 
#  Integration Module

* An Integration Module is a high level logical construct
* Provides a logical grouping of business operations as a single integration point
* Provide either a source, target, or bi-directional business flow
![Integration Modules](ikasaneip/developer/docs/quickstart-images/integration-module.png) 

#  Flows

 * Integration modules comprise of one or more related flows
 * Flows are cohesive operations on a business artifact as a synchronous operation
 * Multiple flows can be chained to isolate concerns
 * Standard event container allows any data type to be transported
 <img src="ikasaneip/developer/docs/quickstart-images/flows.png" width="90%">

#  Components

 * Flows comprise of flow components which have implementation injected as POJOs
 * Components are individual operations acting on events within a flow
 * There are different types of components see the full list below
 * Consumer Component is a POJO with injected tech API for application integration to source events
 * Core services automatically bound to each flow
 * Service APIs support management of the flow, runtime status, resubmission, and replay
 ![Detail view](ikasaneip/developer/docs/quickstart-images/ikasan-anatomy-detail.png)
 
 # Document Info
 
 | Authors | Ikasan Development Team |
 | --- | --- |
 | Contributors | n/a |
 | Date | April 2019 |
 | Email | info@ikasan.org |
 | WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |


