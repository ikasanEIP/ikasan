#!/bin/bash

#Usage
#cd target
#CLI_FILE=`find . -name "*sftp-resource-adapter.sh"`
#echo $CLI_FILE
#dos2unix $CLI_FILE
#bash $CLI_FILE eai-default . $JBOSS_HOME/../conf/jboss-as.conf

PROFILE=$1
JBOSS_EAP_CONF=$2

source ${JBOSS_EAP_CONF}

$JBOSS_HOME/bin/jboss-cli.sh --connect <<EOF
#
# Creates the SFTP resource-adapter if it doesnt already exist
#

if (outcome != success) of /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=ikasan-sftp-ra-${dashed.ikasan.version}/connection-definitions=ConsumerSftpConnectionFactory:read-resource
    /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=ikasan-sftp-ra-${dashed.ikasan.version}/connection-definitions=ConsumerSftpConnectionFactory:add(class-name=org.ikasan.connector.sftp.outbound.SFTPManagedConnectionFactory,jndi-name=java:/sftp/moduleName/flowName/ConsumerSftpConnectionFactory,pad-xid=false,min-pool-size=1,max-pool-size=1,pool-prefill=false,pool-use-strict-min=false,flush-strategy=FailingConnectionOnly,wrap-xa-resource=true,interleaving=false)
end-if

if (outcome != success) of /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=ikasan-sftp-ra-${dashed.ikasan.version}/connection-definitions=ProducerSftpConnectionFactory:read-resource
    /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=ikasan-sftp-ra-${dashed.ikasan.version}/connection-definitions=ProducerSftpConnectionFactory:add(class-name=org.ikasan.connector.sftp.outbound.SFTPManagedConnectionFactory,jndi-name=java:/sftp/moduleName/flowName/ProducerSftpConnectionFactory,pad-xid=false,min-pool-size=1,max-pool-size=1,pool-prefill=false,pool-use-strict-min=false,flush-strategy=FailingConnectionOnly,wrap-xa-resource=true,interleaving=false)
end-if

#
# Rollback
#
#
#   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=ikasan-sftp-ra-${dashed.ikasan.version}/connection-definitions=ConsumerSftpConnectionFactory:remove()
#   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=ikasan-sftp-ra-${dashed.ikasan.version}/connection-definitions=ProducerSftpConnectionFactory:remove()

exit
EOF
