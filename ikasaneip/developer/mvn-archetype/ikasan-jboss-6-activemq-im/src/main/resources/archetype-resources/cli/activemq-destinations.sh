#!/bin/bash
PROFILE=$1
JBOSS_EAP_CONF=$2

source ${JBOSS_EAP_CONF}

$JBOSS_HOME/bin/jboss-cli.sh --connect <<EOF

if (outcome != success) of /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-${version.dashed.activemq}:read-resource
   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=topicPool1:add(class-name=org.apache.activemq.command.ActiveMQTopic,jndi-name=java:/activemq/topic/topic1,use-java-context=true)
   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=topicPool1/config-properties=PhysicalName:add(value=activemq/topic/topic1)
   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=topicPool2:add(class-name=org.apache.activemq.command.ActiveMQTopic,jndi-name=java:/activemq/topic/topic2,use-java-context=true)
   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=topicPool2/config-properties=PhysicalName:add(value=activemq/topic/topic2)
   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=queuePool1:add(class-name=org.apache.activemq.command.ActiveMQQueue,jndi-name=java:/activemq/queue/queue1,use-java-context=true)
   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=queuePool1/config-properties=PhysicalName:add(value=activemq/queue/queue1)
   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=queuePool2:add(class-name=org.apache.activemq.command.ActiveMQQueue,jndi-name=java:/activemq/queue/queue2,use-java-context=true)
   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=queuePool2/config-properties=PhysicalName:add(value=activemq/queue/queue2)
end-if

#
# Rollback
#
#   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=topicPool1:remove()
#   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=topicPool2:remove()
#   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=queuePool1:remove()
#   /profile=$PROFILE/subsystem=resource-adapters/resource-adapter=activemq-ra-${version.dashed.activemq}/admin-objects=queuePool2:remove()

exit
EOF