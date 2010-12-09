/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.framework.flow.invoker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ikasan.core.component.endpoint.Endpoint;
import org.ikasan.core.component.endpoint.EndpointException;
import org.ikasan.core.flow.FlowElement;
import org.ikasan.core.flow.InvalidFlowException;
import org.ikasan.core.flow.invoker.FlowInvocationContext;
import org.ikasan.core.flow.invoker.VisitingFlowElementInvoker;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.flow.event.listener.FlowEventListener;
import org.ikasan.spec.routing.Router;
import org.ikasan.spec.routing.RouterException;
import org.ikasan.spec.sequencing.Sequencer;
import org.ikasan.spec.sequencing.SequencerException;
import org.ikasan.spec.transformation.TransformationException;
import org.ikasan.spec.transformation.Translator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for VisitingFlowElementInvoker
 * 
 * @author Ikasan Development Team
 * 
 */
public class VisitingFlowElementInvokerTest
{
    /**
     * Mockery for classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    /** Mocked Event object */
    Event originalEvent = classMockery.mock(Event.class, "originalEvent");
    /** Mocked Event object */
    Event constitutentEventA = classMockery.mock(Event.class, "constituentEventA");
    /** Mocked Event object */
    Event constitutentEventB = classMockery.mock(Event.class, "constituentEventB");

    /**
     * Mocked Transformer
     */
    Translator transformer = classMockery.mock(Translator.class);
    /**
     * Mocked Endpoint
     */
    Endpoint endpoint = classMockery.mock(Endpoint.class);
    /**
     * Mocked Router
     */
    Router router = classMockery.mock(Router.class);
    /**
     * Mocked Sequencer
     */
    Sequencer sequencer = classMockery.mock(Sequencer.class);
    /** Mocked FlowElement representing a downstream element */
    FlowElement downstreamElementA = classMockery.mock(FlowElement.class, "downstreamElementA");
    /** Mocked FlowElement representing a downstream element */
    FlowElement downstreamElementB = classMockery.mock(FlowElement.class, "downstreamElementB");
    /** Mocked FlowEventListener */
    FlowEventListener flowEventListener = classMockery.mock(FlowEventListener.class);
    /** Name of the module */
    String moduleName = "moduleName";
    /** Name of the flow */
    String flowName = "flowName";
    
    FlowInvocationContext flowInvocationContext = classMockery.mock(FlowInvocationContext.class);
    
    /** Class under test */
    private VisitingFlowElementInvoker visitingFlowElementInvoker;

    /**
     * Constructor
     */
    public VisitingFlowElementInvokerTest()
    {
        visitingFlowElementInvoker = new VisitingFlowElementInvoker();
        visitingFlowElementInvoker.setFlowEventListener(flowEventListener);
    }

    /**
     * Tests that execution with a null FlowElement simply skips out
     */
    @Test
    public void testInvoke_withNullFlowElementWillDoNothing()
    {
        visitingFlowElementInvoker.invoke(flowInvocationContext, null, moduleName,
            flowName, null);
    }

    /**
     * Tests that execution with a FlowElement containing a Transformer, invokes
     * the transformer, then looks for the default transition
     * 
     * @throws TransformationException
     * @throws EndpointException
     */
    @Test
    public void testInvoke_withWrappedTransformerWillInvokeTransformerAndLookForDefaultTransition() throws TransformationException, EndpointException
    {
        final FlowElement transformerWrappingElement = classMockery.mock(FlowElement.class);
        final FlowElement terminatingElement = classMockery.mock(FlowElement.class);
        // set up a transformer followed by a terminating endpoint as default
        // transition, both expecting to be called
        mockTransformerVisitingExpectations(transformerWrappingElement, transformer, "myTransformer", originalEvent, terminatingElement);
        mockTerminatingEndpointElement(endpoint, "myEndpoint", terminatingElement, originalEvent, null);
        visitingFlowElementInvoker.invoke(flowInvocationContext, originalEvent, moduleName, flowName, transformerWrappingElement);
        classMockery.assertIsSatisfied();
    }

    


    /**
     * Tests that execution with a FlowElement containing a Transformer, but no
     * default transition, invokes the transformer, then throws a runtime
     * exception due to the missing default transition
     * 
     * @throws TransformationException
     */
    @Test
    public void testInvoke_withWrappedTransformerWillThrowRuntimeExceptionIfNoDefaultTransition() throws TransformationException
    {
        final FlowElement transformerWrappingElement = classMockery.mock(FlowElement.class);
        mockTransformerVisitingExpectations(transformerWrappingElement, transformer, "myTransformer", originalEvent, null);
        RuntimeException re = null;
        try
        {
            visitingFlowElementInvoker.invoke(flowInvocationContext, originalEvent, moduleName, flowName, transformerWrappingElement);
            Assert.fail("RuntimeException should have been thrown, when Transformer is last component");
        }
        catch (RuntimeException r)
        {
            re = r;
        }
        Assert.assertNotNull("RuntimeException should have been thrown ", re);
        Assert.assertTrue("Caught exception should be InvalidFlowException", re instanceof InvalidFlowException);
        classMockery.assertIsSatisfied();
    }

    /**
     * Tests that execution with a FlowElement containing an Endpoint, invokes
     * the endpoint, then looks for the default transition
     * 
     * @throws EndpointException
     */
    @Test
    public void testInvoke_withWrappedEndpointWillInvokeEndpointAndLookForDefaultTransition() throws EndpointException
    {
        final FlowElement endpointWrappingElement = classMockery.mock(FlowElement.class);
        mockTerminatingEndpointElement(endpoint, "myEndpoint", endpointWrappingElement, originalEvent, null);
        visitingFlowElementInvoker.invoke(flowInvocationContext, originalEvent, moduleName, flowName, endpointWrappingElement);

        classMockery.assertIsSatisfied();
    }

    

    /**
     * Tests that execution with a FlowElement containing a Router, invokes the
     * router, then invokes the downstream elements for each branch
     * 
     * @throws RouterException
     * @throws CloneNotSupportedException
     * @throws EndpointException
     */
    @Test
    public void testInvoke_withWrappedRouterWillInvokeRouterAndInvokeAllReferencedTransitions() throws RouterException, CloneNotSupportedException,
            EndpointException
    {
        final FlowElement routerWrappingElement = classMockery.mock(FlowElement.class);
        final Map<String, FlowElement> routerTransitions = new LinkedHashMap<String, FlowElement>();
        routerTransitions.put("transitionA", downstreamElementA);
        routerTransitions.put("transitionB", downstreamElementB);
        // router to be visited
        mockRouterVisitingExpectations(routerWrappingElement, router, "myRouter", originalEvent, routerTransitions);
        // expect downstreamElementA to be visited
        mockTerminatingEndpointElement(endpoint, "downstreamComponentA", downstreamElementA, originalEvent, null);
        // expect downstreamElementB to be visited
        mockTerminatingEndpointElement(endpoint, "downstreamComponentB", downstreamElementB, originalEvent, null);
        visitingFlowElementInvoker.invoke(flowInvocationContext, originalEvent, moduleName, flowName, routerWrappingElement);

        classMockery.assertIsSatisfied();
    }



  



    /**
     * Tests that execution with a FlowElement containing a Router, invokes the
     * router, then throws a RuntimeException if there is no defualt transition
     * 
     * @throws RouterException
     * @throws CloneNotSupportedException
     */
    @Test
    public void testInvoke_withWrappedRouterWillThrowRuntimeExceptionIfNoDefaultTransition() throws RouterException, CloneNotSupportedException
    {
        final FlowElement routerWrappingElement = classMockery.mock(FlowElement.class, "routerElement");

        final Map<String, FlowElement> routerTransitions = new LinkedHashMap<String, FlowElement>();
        routerTransitions.put("nonExistantTransition", null);
        // mock router returns non existant transition - results in an
        // InvalidFlowException being thrown.
        mockRouterVisitingExpectations(routerWrappingElement, router, "myRouter", originalEvent, routerTransitions);

        
        InvalidFlowException invalidFlowException = null;
        try{
        	visitingFlowElementInvoker.invoke(flowInvocationContext, originalEvent, moduleName, flowName, routerWrappingElement);
        	Assert.fail("should have thrown InvalidFlow exception for Router with unmapped transition");
        }catch(InvalidFlowException i){
        	invalidFlowException = i;
        }
        Assert.assertNotNull("should have thrown InvalidFlow exception for Router with unmapped transition", invalidFlowException);
        classMockery.assertIsSatisfied();
    }

    /**
     * Tests that execution with a FlowElement containing a Sequencer, invokes
     * the sequencer, then invokes the default transition with each produced
     * event
     * 
     * Note that this is the happy path for a Sequencer
     * 
     * @throws SequencerException
     * @throws CloneNotSupportedException
     * @throws EndpointException
     */
    @Test
    public void testInvoke_withWrappedSequencerWillInvokeSequencerAndInvokeDefaultTransitionWithAllReturnedEvents() throws SequencerException,
            CloneNotSupportedException, EndpointException
    {
        final FlowElement sequencerWrappingElement = classMockery.mock(FlowElement.class, "mySequencer");
        final List<Event> sequencerResults = new ArrayList<Event>();
        sequencerResults.add(constitutentEventA);
        sequencerResults.add(constitutentEventB);
        // sequencer expects to be called, returns constituent events, and
        // routes downstreamElementA which is its default transition
        mockSequencerVistingExpectations(sequencerWrappingElement, sequencer, "mySequencerComponent", originalEvent, downstreamElementA, sequencerResults);
        // downstream component gets visited with first constituent event
        mockTerminatingEndpointElement(endpoint, "myDownstreamComponent", downstreamElementA, constitutentEventA, null);
        // downstream component gets visited with second constituent event
        mockTerminatingEndpointElement(endpoint, "myDownstreamComponent", downstreamElementA, constitutentEventB, null);
        visitingFlowElementInvoker.invoke(flowInvocationContext, originalEvent, moduleName, flowName,
            sequencerWrappingElement);
        classMockery.assertIsSatisfied();
    }

    /**
     * Tests that execution with a FlowElement containing a Sequencer, but no
     * default transition, invokes the sequencer, then throws a RuntimeException
     * because of the missing transition
     * 
     * @throws SequencerException
     * @throws CloneNotSupportedException
     */
    @Test
    public void testInvoke_withWrappedSequencerWillThrowRuntimeExceptionIfNoDefaultTransition() throws SequencerException, CloneNotSupportedException
    {
        final FlowElement sequencerWrappingElement = classMockery.mock(FlowElement.class);
        final List<Event> sequencerResults = new ArrayList<Event>();

        sequencerResults.add(constitutentEventA);
        sequencerResults.add(constitutentEventB);
        // expect the sequencer to get visited, but have a null transition
        FlowElement defaultTransition = null;
        mockSequencerVistingExpectations(sequencerWrappingElement, sequencer, "mySequencerComponent", originalEvent, defaultTransition, sequencerResults);

        InvalidFlowException invalidFlowException = null;
        try{
        	visitingFlowElementInvoker.invoke(flowInvocationContext, originalEvent, moduleName, flowName, sequencerWrappingElement);
        	Assert.fail("InvalidFlowException should have been thrown when a Sequencer is encountered with no default transition");
        }catch(InvalidFlowException i){
        	invalidFlowException = i;
        }
        Assert.assertNotNull("InvalidFlowException should have been thrown when a Sequencer is encountered with no default transition", invalidFlowException);
        
        classMockery.assertIsSatisfied();
    }

 

 

 

    /**
     * Setup mock expectations for Event
     * 
     * @param thisEvent
     * @param noOfClones
     * @throws CloneNotSupportedException
     */
    void expectEventClone(final Event thisEvent, final int noOfClones) throws CloneNotSupportedException
    {
        classMockery.checking(new Expectations()
        {
            {
                exactly(noOfClones).of(thisEvent).clone();
                will(returnValue(thisEvent));
                // this is just mocking the duplication of Event
            }
        });
    }

    /**
     * Mock the mockTerminatingEndpointElement
     * 
     * @param anEndpoint
     * @param componentName
     * @param terminatingElement
     * @param event
     * @param throwable
     * @throws EndpointException
     */
    private void mockTerminatingEndpointElement(final Endpoint anEndpoint, final String componentName, final FlowElement terminatingElement, final Event event,
            final Throwable throwable) throws EndpointException
    {
        classMockery.checking(new Expectations()
        {
            {
            	
            	one(flowInvocationContext).addInvokedComponentName(componentName);
            	
                one(event).idToString();
                will(returnValue("dummy events ids"));
                exactly(2).of(terminatingElement).getComponentName();
                will(returnValue(componentName));
                exactly(2).of(terminatingElement).getFlowComponent();
                will(returnValue(anEndpoint));
                one(flowEventListener).beforeFlowElement(moduleName, flowName, terminatingElement, event);
                one(anEndpoint).route(event);
                if (throwable != null)
                {
                    will(throwException(throwable));
                }
                else
                {
                    one(flowEventListener).afterFlowElement(moduleName, flowName, terminatingElement, event);
                    one(terminatingElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                    will(returnValue(null));
                }
            }
        });
    }




    /**
     * Mock the TransformerVisitingExpectations
     * 
     * @param transformerElement
     * @param aTransformer
     * @param componentName
     * @param event
     * @param defaultTransition
     * @throws TransformationException
     */
    private void mockTransformerVisitingExpectations(final FlowElement transformerElement, final Translator aTransformer, final String componentName,
            final Event event, final FlowElement defaultTransition) throws TransformationException
    {
        classMockery.checking(new Expectations()
        {
            {
            	one(flowInvocationContext).addInvokedComponentName(componentName);
                one(event).idToString();
                will(returnValue("dummy events ids"));
                allowing(transformerElement).getComponentName();
                will(returnValue(componentName));
                exactly(2).of(transformerElement).getFlowComponent();
                will(returnValue(aTransformer));
                one(flowEventListener).beforeFlowElement(moduleName, flowName, transformerElement, event);
                one(aTransformer).onEvent(originalEvent);
                one(flowEventListener).afterFlowElement(moduleName, flowName, transformerElement, event);
                one(transformerElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(defaultTransition));
            }
        });
    }

    /**
     * mock the RouterVisitingExpectations
     * 
     * @param routerWrappingElement
     * @param aRouter
     * @param componentName
     * @param event
     * @param routerTransitions
     * @throws RouterException
     * @throws CloneNotSupportedException
     */
    private void mockRouterVisitingExpectations(final FlowElement routerWrappingElement, final Router aRouter, final String componentName, final Event event,
            final Map<String, FlowElement> routerTransitions) throws RouterException, CloneNotSupportedException
    {
        classMockery.checking(new Expectations()
        {
            {
            	one(flowInvocationContext).addInvokedComponentName(componentName);
            	
                exactly(1).of(event).idToString();
                will(returnValue("dummy events ids"));
                exactly(2).of(routerWrappingElement).getComponentName();
                will(returnValue(componentName));
                exactly(2).of(routerWrappingElement).getFlowComponent();
                will(returnValue(aRouter));
                one(flowEventListener).beforeFlowElement(moduleName, flowName, routerWrappingElement, event);
                one(aRouter).route(event);
                will(returnValue(new ArrayList<String>(routerTransitions.keySet())));
                one(flowEventListener).afterFlowElement(moduleName, flowName, routerWrappingElement, event);
                // expect each of the transitions to be taken
                for (String transitionName : routerTransitions.keySet())
                {
                    FlowElement transitionableFlowElement = routerTransitions.get(transitionName);
                    one(routerWrappingElement).getTransition(transitionName);
                    will(returnValue(transitionableFlowElement));
                    if (transitionableFlowElement == null)
                    {
                        // if any of the transitions points to a non existant
                        // flowElement, an InvalidFlowException gets thrown, and
                        // thats the end of that
                        // this call supports the creation of the
                        // InvalidFlowException
                        one(routerWrappingElement).getComponentName();
                        will(returnValue(componentName));
                        break;
                    }
                    expectEventClone(event, 1);
                }
            }
        });
    }

    /**
     * Provide Mocks for the SequencerVistingExpectations
     * 
     * @param sequencerWrappingElement
     * @param thisSequencer
     * @param componentName
     * @param parentEvent
     * @param defaultTransition
     * @param sequencerResults
     * @throws SequencerException
     * @throws CloneNotSupportedException
     */
    private void mockSequencerVistingExpectations(final FlowElement sequencerWrappingElement, final Sequencer thisSequencer, final String componentName,
            final Event parentEvent, final FlowElement defaultTransition, final List<Event> sequencerResults) throws SequencerException,
            CloneNotSupportedException
    {
        classMockery.checking(new Expectations()
        {
            {
            	one(flowInvocationContext).addInvokedComponentName(componentName);
            	
                exactly(1).of(parentEvent).idToString();
                will(returnValue("parentEvent"));
                exactly(3).of(sequencerWrappingElement).getComponentName();
                will(returnValue(componentName));
                exactly(2).of(sequencerWrappingElement).getFlowComponent();
                will(returnValue(thisSequencer));
                one(flowEventListener).beforeFlowElement(with(equal(moduleName)), with(equal(flowName)), with(equal(sequencerWrappingElement)),
                    with(equal(parentEvent)));
                one(thisSequencer).onEvent(with(equal(parentEvent)), with(equal(moduleName)), with(equal(componentName)));
                will(returnValue(sequencerResults));
                for (Event constiutentEvent : sequencerResults)
                {
                    one(flowEventListener).afterFlowElement(with(equal(moduleName)), with(equal(flowName)), with(equal(sequencerWrappingElement)),
                        with(equal(constiutentEvent)));
                }
                one(sequencerWrappingElement).getTransition(FlowElement.DEFAULT_TRANSITION_NAME);
                will(returnValue(defaultTransition));
                // TODO, all of the functionality for checking whether a
                // sequencer has a default transition should be moved to the
                // constructor for the FlowElement
                if (defaultTransition == null)
                {
                    // an InvalidFlowException would be thrown, requiring the
                    // following for its construction
                    one(sequencerWrappingElement).getComponentName();
                    will(returnValue(componentName));
                }
                else
                {
                    for (Event constiutentEvent : sequencerResults)
                    {
                        expectEventClone(constiutentEvent, 1);
                    }
                }
            }
        });
    }
}
