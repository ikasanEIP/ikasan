package org.ikasan.dashboard.broadcast;

import java.util.Objects;

public class State
{
    private static final String RUNNING = "running";
    private static final String STOPPED = "stopped";
    private static final String STOPPED_IN_ERROR = "stoppedInError";
    private static final String RECOVERING = "recovering";
    private static final String PAUSED = "paused";
    private static final String START_PAUSE = "startPause";
    private static final String UNKNOWN = "unknown";

    private static final String RUNNING_COLOUR = "rgb(5,227,108)";
    private static final String STOPPED_COLOUR= "rgb(0,0,0)";
    private static final String STOPPED_IN_ERROR_COLOUR = "rgb(255,0,0)";
    private static final String RECOVERING_COLOUR = "rgb(253,185,19)";
    private static final String PAUSED_COLOUR = "rgb(133,181,225)";
    private static final String START_PAUSE_COLOUR = "rgb(133,181,225)";

    public static State RUNNING_STATE = new State(RUNNING, RUNNING_COLOUR);
    public static State STOPPED_STATE = new State(STOPPED, STOPPED_COLOUR);
    public static State STOPPED_IN_ERROR_STATE = new State(STOPPED_IN_ERROR, STOPPED_IN_ERROR_COLOUR);
    public static State RECOVERING_STATE = new State(RECOVERING, RECOVERING_COLOUR);
    public static State PAUSED_STATE = new State(PAUSED, PAUSED_COLOUR);
    public static State START_PAUSE_STATE = new State(START_PAUSE, START_PAUSE_COLOUR);
    public static State UNKNOWN_STATE = new State(UNKNOWN, STOPPED_COLOUR);

    private String flowState;
    private String stateColour;

    public static State getState(String state)
    {
        if(state.equals(RUNNING))
            return RUNNING_STATE;
        else if(state.equals(STOPPED))
            return STOPPED_STATE;
        else if(state.equals(STOPPED_IN_ERROR))
            return STOPPED_IN_ERROR_STATE;
        else if(state.equals(RECOVERING))
            return RECOVERING_STATE;
        else if(state.equals(PAUSED))
            return PAUSED_STATE;
        else if(state.equals(START_PAUSE))
            return START_PAUSE_STATE;

        return UNKNOWN_STATE;
    }

    private State(String flowState, String stateColour)
    {
        this.flowState = flowState;
        this.stateColour = stateColour;
    }

    public String getFlowState()
    {
        return flowState;
    }

    public String getStateColour()
    {
        return stateColour;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State state = (State) o;
        return flowState.equals(state.flowState) &&
            stateColour.equals(state.stateColour);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(flowState, stateColour);
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("State{");
        sb.append("flowState='").append(flowState).append('\'');
        sb.append(", stateColour='").append(stateColour).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
