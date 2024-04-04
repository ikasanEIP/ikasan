[![Build Status](https://travis-ci.org/ikasanEIP/ikasan.svg?branch=3.1.x)](https://travis-ci.org/ikasanEIP/ikasan)

![Problem Domain](ikasaneip/developer/docs/quickstart-images/Ikasan-title-transparent.png)

- Introduction
    - [A Quick Overview](./QuickOverview.md)
    
- Services
    - [Ikasan Hospital Service](ikasaneip/hospital/Readme.md)
    - [Replay Service](ikasaneip/replay/Readme.md)
    - [Wiretap Service](ikasaneip/wiretap/Readme.md)
    - [Configuration Service](ikasaneip/configuration-service/Readme.md)
    - [Monitoring Service](ikasaneip/monitor/Readme.md)
    
- Developer
    - [Developer Pre-Requisiites](ikasaneip/developer/docs/DeveloperPreRequisites.md) 
    - [Developer Guide](ikasaneip/developer/docs/DeveloperGuide.md) 
    - [Component Guide](ikasaneip/component/Readme.md)
    - [Dashboard Guide](ikasaneip/visualisation/dashboard/README.md)
    - [Upgrade Path](./UpgradePath.md) - steps required to upgrade from the previous Ikasan version
    
 - Samples overview
    - [spring-boot-builder-pattern](ikasaneip/sample/spring-boot/builder-pattern/README.md)
    - [spring-boot-file](ikasaneip/sample/spring-boot/file/README.md)
    - [spring-boot-jms](ikasaneip/sample/spring-boot/jms/README.md)
    - [spring-boot-ftp-jms](ikasaneip/sample/spring-boot/ftp-jms/README.md)
    - [spring-boot-sftp-jms](ikasaneip/sample/spring-boot/sftp-jms/README.md)
---------------------


Contributor Best Practices
--------------------------
1. Ensure logging output from performing ```mvn clean install``` is kept to an absolute minimum, 
   as we have a 4MB limit on log output on our ci builds. 
2. Ensure the max log level is INFO for all code
3. Likewise ```hibernate.show_sql``` should always be ```false``` 
2. If adding a new component do add a README.md page to explain its configuration and use
3. For each new component ensure this is demonstrated in a sample module / flow. 
   
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

