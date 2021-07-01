[../](../../../Readme.md)
![Ikasan](quickstart-images/Ikasan-title-transparent.png)
# Release

- Create new branch 'release/3.1.1'
  ```
  git branch -a -vvv
  warning: ignoring broken ref refs/remotes/origin/HEAD
    3.0.x-h2                                                                         376512eec [origin/3.0.x: ahead 5, behind 43] IKASAN-1751 - fixing cherry pick issues
  * 3.1.1  
  
  git checkout -b release/3.1.1
  
  Switched to a new branch 'release/3.1.1'
  
  git branch -a -vvv
  warning: ignoring broken ref refs/remotes/origin/HEAD
    3.1.x                                                                            93af4076d [origin/3.1.x] IKASAN-1827 removing commented out code.
  * release/3.1.1                                                             93af4076d IKASAN-1827 removing commented out code.
  
  ```
- Update all references to 3.1.1-SNAPSHOT to version you are releasing ie. 3.1.1
   - references in pom.xml
   - references in md files
   - check status 
   ```
  git status
  On branch release/3.1.1
  Changes to be committed:
    (use "git reset HEAD <file>..." to unstage)
  
  	new file:   ikasaneip/developer/docs/Release.md
  
  Changes not staged for commit:
    (use "git add <file>..." to update what will be committed)
    (use "git checkout -- <file>..." to discard changes in working directory)
  
  	modified:   ikasaneip/builder/jar/pom.xml
  	modified:   ikasaneip/builder/pom.xml
  	modified:   ikasaneip/builder/spring/pom.xml
  	modified:   ikasaneip/compatibility/0.8.x/pom.xml
  	modified:   ikasaneip/compatibility/pom.xml
  	modified:   ikasaneip/component/converter/pom.xml
  	modified:   ikasaneip/component/endpoint/email-endpoint/pom.xml
  	modified:   ikasaneip/component/endpoint/filetransfer/common/pom.xml
  	modified:   ikasaneip/component/endpoint/filetransfer/connector-base/pom.xml
  	modified:   ikasaneip/component/endpoint/filetransfer/connector-basefiletransfer/pom.xml
  	modified:   ikasaneip/component/endpoint/filetransfer/ftp/pom.xml
  	modified:   ikasaneip/component/endpoint/filetransfer/pom.xml
  	modified:   ikasaneip/component/endpoint/filetransfer/sftp/pom.xml
  	modified:   ikasaneip/component/endpoint/jms-client/pom.xml
  	modified:   ikasaneip/component/endpoint/jms-spring-arjuna/pom.xml
  	modified:   ikasaneip/component/endpoint/jms-spring/pom.xml
  	modified:   ikasaneip/component/endpoint/mongo-endpoint-test/pom.xml
  	modified:   ikasaneip/component/endpoint/mongo-endpoint/pom.xml
  	modified:   ikasaneip/component/endpoint/pom.xml
  	modified:   ikasaneip/component/endpoint/quartz-schedule/pom.xml
  	modified:   ikasaneip/component/endpoint/test-endpoint/pom.xml
  	modified:   ikasaneip/component/endpoint/utility-endpoint/pom.xml
  	modified:   ikasaneip/component/filter/pom.xml
  	modified:   ikasaneip/component/pom.xml
  	modified:   ikasaneip/component/router/multirecipient/pom.xml
  	modified:   ikasaneip/component/router/pom.xml
  	modified:   ikasaneip/component/splitter/pom.xml
  	modified:   ikasaneip/component/validator/pom.xml
  	modified:   ikasaneip/configuration-service/pom.xml
  	modified:   ikasaneip/developer/docs/Release.md
  	modified:   ikasaneip/developer/docs/StandaloneDeveloperGuide.md
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-db-jms-im/Readme.md
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-db-jms-im/pom.xml
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-filesystem-im/Readme.md
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-filesystem-im/pom.xml
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-ftp-jms-im/Readme.md
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-ftp-jms-im/pom.xml
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-hibernate-jms-im/Readme.md
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-hibernate-jms-im/pom.xml
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-jms-im/Readme.md
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-jms-im/pom.xml
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-sftp-jms-im/Readme.md
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-sftp-jms-im/pom.xml
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-vanilla-im/Readme.md
  	modified:   ikasaneip/developer/mvn-archetype/ikasan-standalone-vanilla-im/pom.xml
  	modified:   ikasaneip/developer/mvn-archetype/pom.xml
  	modified:   ikasaneip/developer/pom.xml
  	modified:   ikasaneip/error-reporting/pom.xml
  	modified:   ikasaneip/exclusion/pom.xml
  	modified:   ikasaneip/flow/pom.xml
  	modified:   ikasaneip/flow/visitorPatternFlow/pom.xml
  	modified:   ikasaneip/harvesting/pom.xml
  	modified:   ikasaneip/hospital/pom.xml
  	modified:   ikasaneip/housekeeping/core/pom.xml
  	modified:   ikasaneip/housekeeping/dashboard/pom.xml
  	modified:   ikasaneip/housekeeping/module/pom.xml
  	modified:   ikasaneip/housekeeping/pom.xml
  	modified:   ikasaneip/mapping/pom.xml
  	modified:   ikasaneip/marshaller/pom.xml
  	modified:   ikasaneip/marshaller/xml-marshaller/pom.xml
  	modified:   ikasaneip/module/pom.xml
  	modified:   ikasaneip/monitor/pom.xml
  	modified:   ikasaneip/persistence/pom.xml
  	modified:   ikasaneip/platform/ikasan-eip-standalone-bom/pom.xml
  	modified:   ikasaneip/platform/ikasan-eip-standalone/pom.xml
  	modified:   ikasaneip/platform/ikasan-h2-standalone-persistence/pom.xml
  	modified:   ikasaneip/platform/ikasan-mysql-standalone-persistence/pom.xml
  	modified:   ikasaneip/platform/ikasan-setup/pom.xml
  	modified:   ikasaneip/platform/ikasan-spring-resource/pom.xml
  	modified:   ikasaneip/platform/ikasan-standalone-persistence/pom.xml
  	modified:   ikasaneip/platform/pom.xml
  	modified:   ikasaneip/pom.xml
  	modified:   ikasaneip/recovery-manager/pom.xml
  	modified:   ikasaneip/replay/pom.xml
  	modified:   ikasaneip/rest/pom.xml
  	modified:   ikasaneip/rest/rest-dashboard-client/pom.xml
  	modified:   ikasaneip/rest/rest-dashboard/pom.xml
  	modified:   ikasaneip/rest/rest-module-client/pom.xml
  	modified:   ikasaneip/rest/rest-module/pom.xml
  	modified:   ikasaneip/sample/non-functional/component-performance/pom.xml
  	modified:   ikasaneip/sample/non-functional/h2-server-component-fail/pom.xml
  	modified:   ikasaneip/sample/non-functional/h2-server-txn-fail-commit-amq-resource/pom.xml
  	modified:   ikasaneip/sample/non-functional/h2-server-txn-fail-commit-db-resource/pom.xml
  	modified:   ikasaneip/sample/non-functional/h2-server-txn-fail-precommit-amq-resource/pom.xml
  	modified:   ikasaneip/sample/non-functional/h2-server-txn-fail-precommit-db-resource/pom.xml
  	modified:   ikasaneip/sample/non-functional/nft-util/pom.xml
  	modified:   ikasaneip/sample/non-functional/pom.xml
  	modified:   ikasaneip/sample/pom.xml
  	modified:   ikasaneip/sample/spring-boot/builder-pattern/pom.xml
  	modified:   ikasaneip/sample/spring-boot/file/pom.xml
  	modified:   ikasaneip/sample/spring-boot/ftp-jms/pom.xml
  	modified:   ikasaneip/sample/spring-boot/jms/pom.xml
  	modified:   ikasaneip/sample/spring-boot/pom.xml
  	modified:   ikasaneip/sample/spring-boot/sftp-jms/pom.xml
  	modified:   ikasaneip/scheduler/pom.xml
  	modified:   ikasaneip/security/db/pom.xml
  	modified:   ikasaneip/security/ldap/pom.xml
  	modified:   ikasaneip/security/pom.xml
  	modified:   ikasaneip/security/rest/pom.xml
  	modified:   ikasaneip/serialiser/pom.xml
  	modified:   ikasaneip/solr/pom.xml
  	modified:   ikasaneip/solr/solr-client/pom.xml
  	modified:   ikasaneip/solr/solr-distribution/pom.xml
  	modified:   ikasaneip/solr/solr-security/pom.xml
  	modified:   ikasaneip/spec/component/pom.xml
  	modified:   ikasaneip/spec/event/pom.xml
  	modified:   ikasaneip/spec/flow/pom.xml
  	modified:   ikasaneip/spec/metadata/pom.xml
  	modified:   ikasaneip/spec/module/pom.xml
  	modified:   ikasaneip/spec/monitor/pom.xml
  	modified:   ikasaneip/spec/pom.xml
  	modified:   ikasaneip/spec/recoveryManager/pom.xml
  	modified:   ikasaneip/spec/service/cache/pom.xml
  	modified:   ikasaneip/spec/service/configuration/pom.xml
  	modified:   ikasaneip/spec/service/dashboard-client/pom.xml
  	modified:   ikasaneip/spec/service/deployment/pom.xml
  	modified:   ikasaneip/spec/service/error-reporting/pom.xml
  	modified:   ikasaneip/spec/service/exclusion/pom.xml
  	modified:   ikasaneip/spec/service/history/pom.xml
  	modified:   ikasaneip/spec/service/hospital/pom.xml
  	modified:   ikasaneip/spec/service/housekeeping/pom.xml
  	modified:   ikasaneip/spec/service/management/pom.xml
  	modified:   ikasaneip/spec/service/mapping/pom.xml
  	modified:   ikasaneip/spec/service/module-client/pom.xml
  	modified:   ikasaneip/spec/service/persistence/pom.xml
  	modified:   ikasaneip/spec/service/pom.xml
  	modified:   ikasaneip/spec/service/replay/pom.xml
  	modified:   ikasaneip/spec/service/resubmission/pom.xml
  	modified:   ikasaneip/spec/service/search/pom.xml
  	modified:   ikasaneip/spec/service/serialiser/pom.xml
  	modified:   ikasaneip/spec/service/solr/pom.xml
  	modified:   ikasaneip/spec/service/system-event/pom.xml
  	modified:   ikasaneip/spec/service/wiretap/pom.xml
  	modified:   ikasaneip/spec/uber/pom.xml
  	modified:   ikasaneip/system-event/pom.xml
  	modified:   ikasaneip/test/pom.xml
  	modified:   ikasaneip/topology/pom.xml
  	modified:   ikasaneip/transaction/arjuna/pom.xml
  	modified:   ikasaneip/transaction/pom.xml
  	modified:   ikasaneip/transaction/spring/pom.xml
  	modified:   ikasaneip/visualisation/dashboard-dist/pom.xml
  	modified:   ikasaneip/visualisation/dashboard/pom.xml
  	modified:   ikasaneip/visualisation/pom.xml
  	modified:   ikasaneip/visualisation/vis.js/pom.xml
  	modified:   ikasaneip/webconsole/boot-war/pom.xml
  	modified:   ikasaneip/webconsole/jar/pom.xml
  	modified:   ikasaneip/webconsole/pom.xml
  	modified:   ikasaneip/wiretap/pom.xml
  	modified:   pom.xml
   ```
- Commit all changes 
  ```
    git commit -a -m 'Update references to 3.1.1'
    [release/3.1.X-branch 6a7d68b76] Update references to 3.1.1
     149 files changed, 386 insertions(+), 187 deletions(-)
  
   ``` 
- Tag your changes 
  ```
    git tag -a ikasaneip-3.1.1 -m "tag 3.1.1"
    git tag --list
    git push origin ikasaneip-3.1.1
   ``` 
- Update all references to 3.1.1 to version you are releasing ie. 3.2.0-SNAPSHOT
    - references in pom.xml
    - references in md files
    - check status 
    ```
    git status
    ```
- commit all changes
  ```
  git commit -a -m 'Update references to 3.2.0-SNAPSHOT'
      
  ```
- merge branch back to main branch 3.1.x and rename it to 3.2.x  
