package com.ikasan.sample.spring.boot.flow;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.builder.RouteBuilder;
import org.ikasan.builder.Route;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;

import com.ikasan.sample.spring.boot.component.ComponentFactory;
@Configuration
public class RecipientListFLowConfig
{
    @Value("${module.name}")
    private String moduleName;
    @Resource
    private BuilderFactory builderFactory;
    @Resource
    private ComponentFactory componentFactory;

    /**
    * Create flow bean for flow Recipient List FLow.

    * @return the flow bean.
    */
    @Bean(name = "recipientListFLow")
    public Flow RecipientListFLow()
    {
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder(moduleName);
        return moduleBuilder.getFlowBuilder("Recipient List FLow")
        .consumer("My Recipient Flow JMS Consumer", componentFactory.getMyRecipientFlowJMSConsumer())
        .converter("My Recipient Flow Converter", componentFactory.getMyRecipientFlowConverter())
        .multiRecipientRouter("My Recipient Flow Router", componentFactory.getMyRecipientFlowRouter())
            .when("1", routeMyRecipientFlowRouter1(builderFactory.getRouteBuilder()))
            .when("2", routeMyRecipientFlowRouter2(builderFactory.getRouteBuilder()))
            .when("3", routeMyRecipientFlowRouter3(builderFactory.getRouteBuilder()))
        .build();
    }

    /**
    * Route for path 1 for router My Recipient Flow Router.
    *
    * @param routeBuilder the RouteBuilder used to configure the route.
    * @return the configured Route for path 1 for router My Recipient Flow Router.
    */
    private Route routeMyRecipientFlowRouter1(RouteBuilder routeBuilder) {
        return routeBuilder
        .filter("My Recipient Flow Filter", componentFactory.getMyRecipientFlowFilter())
        .producer("My Recipient Flow JMS Producer 1", componentFactory.getMyRecipientFlowJMSProducer1());
    }

    /**
    * Route for path 2 for router My Recipient Flow Router.
    *
    * @param routeBuilder the RouteBuilder used to configure the route.
    * @return the configured Route for path 2 for router My Recipient Flow Router.
    */
    private Route routeMyRecipientFlowRouter2(RouteBuilder routeBuilder) {
        return routeBuilder
        .singleRecipientRouter("MySingleRecipientRouter", componentFactory.getMySingleRecipientRouter())
            .when("true", routeMySingleRecipientRouterTrue(builderFactory.getRouteBuilder()))
            .when("false", routeMySingleRecipientRouterFalse(builderFactory.getRouteBuilder()))
        .build();
    }

    /**
    * Route for path true for router MySingleRecipientRouter.
    *
    * @param routeBuilder the RouteBuilder used to configure the route.
    * @return the configured Route for path true for router MySingleRecipientRouter.
    */
    private Route routeMySingleRecipientRouterTrue(RouteBuilder routeBuilder) {
        return routeBuilder
        .producer("My Recipient Flow JMS Producer 4", componentFactory.getMyRecipientFlowJMSProducer4());
    }

    /**
    * Route for path false for router MySingleRecipientRouter.
    *
    * @param routeBuilder the RouteBuilder used to configure the route.
    * @return the configured Route for path false for router MySingleRecipientRouter.
    */
    private Route routeMySingleRecipientRouterFalse(RouteBuilder routeBuilder) {
        return routeBuilder
        .producer("My Recipient Flow JMS Producer 5", componentFactory.getMyRecipientFlowJMSProducer5());
    }

    /**
    * Route for path 3 for router My Recipient Flow Router.
    *
    * @param routeBuilder the RouteBuilder used to configure the route.
    * @return the configured Route for path 3 for router My Recipient Flow Router.
    */
    private Route routeMyRecipientFlowRouter3(RouteBuilder routeBuilder) {
        return routeBuilder
        .producer("My Recipient Flow JMS Producer 3", componentFactory.getMyRecipientFlowJMSProducer3());
    }

}
