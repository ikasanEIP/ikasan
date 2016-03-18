#!/bin/bash
PROFILE=$1
JBOSS_EAP_CONF=$2

source ${JBOSS_EAP_CONF}

$JBOSS_HOME/bin/jboss-cli.sh --connect <<EOF

if (outcome != success) of /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-${dashed.version.activemq}:read-resource
    /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${dashed.version.activemq}:add(module=org.apache.activemq.ra.${dashed.version.activemq},transaction-support=XATransaction)
    /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${dashed.version.activemq}/config-properties=ServerUrl:add(value=${activemq.url})
    /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${dashed.version.activemq}/config-properties=UseSessionArgs:add(value=true)
    /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${dashed.version.activemq}/connection-definitions=ActiveMQConnectionFactory:add(class-name=org.apache.activemq.ra.ActiveMQManagedConnectionFactory,jndi-name=java:/ra/activeMQ/ActiveMQConnectionFactory,enabled=true,use-java-context=true,pool-name=ActiveMQConnectionFactory,min-pool-size=1,max-pool-size=20,same-rm-override=false,pool-prefill=false,recovery-username=admin,recovery-password=admin)
    /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${dashed.version.activemq}:activate
end-if

#
# Rollback
#
#   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${dashed.version.activemq}:remove()

exit
EOF
