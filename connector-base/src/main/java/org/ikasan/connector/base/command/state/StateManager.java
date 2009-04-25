/*
 * $Id: StateManager.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/base/command/state/StateManager.java $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.connector.base.command.state;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimal state manager for governing command state
 * 
 * @author Ikasan Development Team
 */
public class StateManager
{
    /**
     * Map of all States and their allowable transitions
     */
    private Map<State, Map<String, State>> allowableTransitions = new HashMap<State, Map<String, State>>();
    /**
     * Adds a valid transition to the state manager
     * 
     * @param t
     */
    /**
     * A map of all known states
     */
    private Map<String, State> allStates = new HashMap<String, State>();

    /**
     * Add a transition from one state to the next
     * 
     * @param t The transition to store
     */
    public void addTransition(Transition t)
    {
        Map<String, State> stateValidTransitions = getStateValidTransitions(t
            .getStartState());
        stateValidTransitions.put(t.getAction(), t.getEndState());
        allowableTransitions.put(t.getStartState(), stateValidTransitions);
        // Keep a hash of all the known states
        addState(t.getStartState());
        addState(t.getEndState());
    }

    /**
     * Adds a state to the map of known states, keyed by name
     * @param state
     */
    private void addState(State state)
    {
        allStates.put(state.getName(), state);
    }

    /**
     * Looks up a state by name
     * 
     * @param name
     * @return The state
     */
    public State getState(String name)
    {
        return allStates.get(name);
    }

    /**
     * Retrieves a Map of all valid transitions for the given start state
     * 
     * @param startState
     * @return Map of end states keyed by actions
     */
    private Map<String, State> getStateValidTransitions(State startState)
    {
        Map<String, State> result = allowableTransitions.get(startState);
        if (result == null)
        {
            result = new HashMap<String, State>();
        }
        return result;
    }

    /**
     * Determines if a proposed action is valid for the given state
     * 
     * @param startState
     * @param action
     * @return true if action is valid
     */
    public boolean isValidTransition(State startState, String action)
    {
        boolean result = false;
        State endState = getEndState(startState, action);
        if (endState != null)
        {
            result = true;
        }
        return result;
    }

    /**
     * Retrieves the end state given a start state and an action
     * 
     * @param startState
     * @param action
     * @return end state, null if transition is invalid
     */
    public State getEndState(State startState, String action)
    {
        State result = null;
        Map<String, State> stateValidTransitions = getStateValidTransitions(startState);
        if (stateValidTransitions != null)
        {
            State endState = stateValidTransitions.get(action);
            if (endState != null)
            {
                result = endState;
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        return allowableTransitions.toString();
    }
}

