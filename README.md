[![Build Status](https://travis-ci.org/ikasanEIP/ikasan.svg?branch=master)](https://travis-ci.org/ikasanEIP/ikasan)
ikasan
======

Open Source Enterprise Integration Platform

The Ikasan Enterprise Integration Platform (EIP) addresses the problem 
domain most commonly known as Enterprise Application Integration (EAI). 

Enterprise application integration can be, and already has been, 
approached a number of different ways by a number of projects/vendors, 
both Open Source and closed commercial frameworks. 

It is the intention of the Ikasan Enterprise Integration Platform 
to address this domain as commoditised configurable solutions rather 
than another development framework.

Using Eclipse
-------------
1. Install the latest version of eclipse
2. Launch eclipse and install the m2e plugin, make sure it uses your repo configs 
   (get it from: http://www.eclipse.org/m2e/download/ or install "Maven Integration for Eclipse" from the Eclipse Marketplace)
3. In eclipse preferences Java->Code Style, import the cleanup, templates, and
   formatter configs in [ikasaneip/ikasan-developer/eclipse](https://github.com/ikasanEIP/ikasan/tree/master/ikasaneip/developer/eclipse) in the ikasanEIP repository.
4. In eclipse preferences Java->Code Style->Code Templates enable the "Automatically add comments"
   checkbox to ensure the standard copyright notice gets added at the top of classes. 
5. Also in code template under Comments -> Types ensure you add your name to the @author tag   
6. In eclipse preferences Java->Editor->Save Actions enable "Additional Actions"
7. Use import on the root pom.xml which will pull in all modules
8. Wait (m2e takes awhile on initial import)