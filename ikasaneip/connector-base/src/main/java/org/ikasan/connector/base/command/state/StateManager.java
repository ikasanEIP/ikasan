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

