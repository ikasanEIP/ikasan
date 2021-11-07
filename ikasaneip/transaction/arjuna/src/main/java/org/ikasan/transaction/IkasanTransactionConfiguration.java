package org.ikasan.transaction;

import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule;
import com.arjuna.ats.jbossatx.jta.RecoveryManagerService;
import me.snowdrop.boot.narayana.autoconfigure.NarayanaBeanFactoryPostProcessor;
import me.snowdrop.boot.narayana.core.jdbc.GenericXADataSourceWrapper;
import me.snowdrop.boot.narayana.core.jdbc.PooledXADataSourceWrapper;
import me.snowdrop.boot.narayana.core.jms.NarayanaXAConnectionFactoryWrapper;
import me.snowdrop.boot.narayana.core.properties.NarayanaProperties;
import me.snowdrop.boot.narayana.core.properties.NarayanaPropertiesInitializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.autoconfigure.transaction.jta.JtaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.StringUtils;

import javax.jms.Message;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.io.File;

@Configuration
@EnableConfigurationProperties({
                                   JtaProperties.class,
                                   NarayanaProperties.class
                               })
public class IkasanTransactionConfiguration
{
    private final JtaProperties jtaProperties;

    private final TransactionManagerCustomizers transactionManagerCustomizers;

    public IkasanTransactionConfiguration(JtaProperties jtaProperties,
                                          ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        this.jtaProperties = jtaProperties;
        this.transactionManagerCustomizers = transactionManagerCustomizers.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public static NarayanaBeanFactoryPostProcessor narayanaBeanFactoryPostProcessor() {
        return new NarayanaBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public NarayanaPropertiesInitializer narayanaPropertiesInitializer(NarayanaProperties properties) {
        initLogDir(properties);
        initTransactionManagerId(properties);
        return new NarayanaPropertiesInitializer(properties);
    }


    @Bean
    @DependsOn("narayanaPropertiesInitializer")
    @ConditionalOnMissingBean
    public UserTransaction narayanaUserTransaction() {
        return com.arjuna.ats.jta.UserTransaction.userTransaction();
    }

    /**
     *  <bean id="arjunaTransactionManager" class="com.arjuna.ats.internal.jta.transaction.arjunacore
     *  .TransactionManagerImple">
     *         <property name="transactionTimeout" value="${ikasan.default.transaction.timeout.seconds:300}"/>
     *     </bean>
     *
     * @return
     */

    @Bean
    @DependsOn("narayanaPropertiesInitializer")
    @ConditionalOnMissingBean
    public TransactionManager narayanaTransactionManager() {
         return com.arjuna.ats.jta.TransactionManager.transactionManager();
    }

    /**
     *     <bean id="transactionManager" class="org.springframework.transaction.jta.JtaTransactionManager">
     *         <property name="transactionManager"  ref="arjunaTransactionManager" />
     *         <property name="userTransaction" >
     *             <bean class="com.arjuna.ats.internal.jta.transaction.arjunacore.UserTransactionImple"/>
     *         </property>
     *         <property name="autodetectUserTransaction" value="true"/>
     *         <property name="defaultTimeout" value="${ikasan.default.transaction.timeout.seconds:300}"/>
     *
     *     </bean>
     */
     @Bean
    @ConditionalOnMissingBean
    public JtaTransactionManager transactionManager(UserTransaction userTransaction,
                                                    TransactionManager transactionManager,
                                                   @Value("${ikasan.default.transaction.timeout.seconds:300}") Integer timeout) {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction, transactionManager);
        if (this.transactionManagerCustomizers != null) {
            this.transactionManagerCustomizers.customize(jtaTransactionManager);
        }
        jtaTransactionManager.setAutodetectTransactionManager(true);
        jtaTransactionManager.setDefaultTimeout(timeout);
        return jtaTransactionManager;
    }

    @Bean(destroyMethod = "stop")
    @DependsOn("narayanaPropertiesInitializer")
    @ConditionalOnMissingBean
    public RecoveryManagerService recoveryManagerService() {
        RecoveryManager.delayRecoveryManagerThread();
        RecoveryManagerService recoveryManagerService = new RecoveryManagerService();
        recoveryManagerService.create();
        recoveryManagerService.start();
        return recoveryManagerService;
    }

    @Bean
    @DependsOn("recoveryManagerService")
    @ConditionalOnMissingBean
    public XARecoveryModule xaRecoveryModule() {
        return XARecoveryModule.getRegisteredXARecoveryModule();
    }

    private void initLogDir(NarayanaProperties properties) {
        if (!StringUtils.isEmpty(properties.getLogDir())) {
            return;
        }

        if (!StringUtils.isEmpty(this.jtaProperties.getLogDir())) {
            properties.setLogDir(this.jtaProperties.getLogDir());
        } else {
            properties.setLogDir(getLogDir().getAbsolutePath());
        }
    }

    private void initTransactionManagerId(NarayanaProperties properties) {
        if (!StringUtils.isEmpty(properties.getTransactionManagerId())) {
            return;
        }

        if (!StringUtils.isEmpty(this.jtaProperties.getTransactionManagerId())) {
            properties.setTransactionManagerId(this.jtaProperties.getTransactionManagerId());
        }
    }

    private File getLogDir() {
        if (StringUtils.hasLength(this.jtaProperties.getLogDir())) {
            return new File(this.jtaProperties.getLogDir());
        }
        File home = new ApplicationHome().getDir();
        return new File(home, "transaction-logs");
    }

    /**
     * Generic data source wrapper configuration.
     */
    @ConditionalOnProperty(name = "narayana.dbcp.enabled", havingValue = "false", matchIfMissing = true)
    static class GenericJdbcConfiguration {

        @Bean
        @ConditionalOnMissingBean(XADataSourceWrapper.class)
        public XADataSourceWrapper xaDataSourceWrapper(NarayanaProperties narayanaProperties,
                                                       XARecoveryModule xaRecoveryModule) {
            return new GenericXADataSourceWrapper(narayanaProperties, xaRecoveryModule);
        }

    }

    /**
     * Pooled data source wrapper configuration.
     */
    @ConditionalOnProperty(name = "narayana.dbcp.enabled", havingValue = "true")
    static class PooledJdbcConfiguration {

        @Bean
        @ConditionalOnMissingBean(XADataSourceWrapper.class)
        public XADataSourceWrapper xaDataSourceWrapper(NarayanaProperties narayanaProperties,
                                                       XARecoveryModule xaRecoveryModule, TransactionManager transactionManager) {
            return new PooledXADataSourceWrapper(narayanaProperties, xaRecoveryModule, transactionManager);
        }

    }

    /**
     * JMS connection factory wrapper configuration.
     */
    @Configuration
    @ConditionalOnClass(Message.class)
    static class NarayanaJmsConfiguration {

        @Bean
        @ConditionalOnMissingBean(XAConnectionFactoryWrapper.class)
        public XAConnectionFactoryWrapper xaConnectionFactoryWrapper(TransactionManager transactionManager,
                                                                     XARecoveryModule xaRecoveryModule, NarayanaProperties narayanaProperties) {
            return new NarayanaXAConnectionFactoryWrapper(transactionManager, xaRecoveryModule, narayanaProperties);
        }

    }
}
