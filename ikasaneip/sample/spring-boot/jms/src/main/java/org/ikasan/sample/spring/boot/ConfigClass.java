package org.ikasan.sample.spring.boot;

import org.springframework.boot.jta.narayana.NarayanaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource( {

//        classpath:ikasan-datasource-conf.xml
//        classpath:ikasan-transaction-conf.xml
//        classpath:ikasan-transaction-manager-conf.xml
//        classpath:serialiser-service-conf.xml
//        classpath:platform-service-conf.xml
//        classpath:scheduler-service-conf.xml
//        classpath:recoveryManager-service-conf.xml
//        classpath:module-service-conf.xml
//        classpath:topology-conf.xml
//        classpath:topology-tx-conf.xml
//        classpath:ikasan-module-bootstrap-conf.xml
//        classpath:wiretap-service-conf.xml
//        classpath:exclusion-service-conf.xml
//        classpath:error-reporting-service-conf.xml
//        classpath:systemevent-service-conf.xml
//        classpath:configuration-service-conf.xml
//        classpath:hospital-conf.xml
//        classpath:rest-service-conf.xml
//
        "classpath:transaction-conf.xml",
        "classpath:ikasan-transaction-conf.xml",
        "classpath:serialiser-service-conf.xml",
      //  "classpath:platform-service-conf.xml",
        "classpath:scheduler-service-conf.xml",
        "classpath:error-reporting-service-conf.xml",
        "classpath:recoveryManager-service-conf.xml",
        "classpath:module-service-conf.xml",
        "classpath:configuration-service-conf.xml",
        "classpath:systemevent-service-conf.xml",
        //"classpath:replay-service-conf.xml ",
        //"classpath:rest-service-conf.xml",
        //"classpath:rest-service.xml",
        "classpath:wiretap-service-conf.xml",
        "classpath:hospital-conf.xml",

        "classpath:exclusion-service-conf.xml",
        "classpath:ikasan-module-bootstrap-conf.xml",
        "classpath:topology-conf.xml",
        "classpath:topology-tx-conf.xml",

        "classpath:user-conf.xml",
        //   "classpath:sybase-datasource-conf.xml",
        "classpath:h2db-datasource-conf.xml",

        "classpath:security-service-local-conf.xml",
        "classpath:springapp-servlet-local.xml",

} )

@ComponentScan({"org.ikasan.web.*","org.ikasan.rest.*"})
public class ConfigClass {
    @Bean("narayanaProperties")
    public NarayanaProperties getNarayanaProperties(){
        NarayanaProperties narayanaProperties =  new NarayanaProperties();
        return narayanaProperties;
   }


}
