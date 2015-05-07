Example use of FTP Consumer and FTP Producer
========================

The below instructions were checked on JBoss EAP 6.2

Pre-requisit:
JBoss 6.2 configured for running Ikasan

- Build
 mvn clean install a
- Create a private topic test.file - by running create-private-topics.cli script
- Deploy ftp resource adapter module
- Create a resource adapter - by running ftp-resource-adapter.cli script
- Deploy ftpEndpoint module
- Deploy ftpEndpoint ear