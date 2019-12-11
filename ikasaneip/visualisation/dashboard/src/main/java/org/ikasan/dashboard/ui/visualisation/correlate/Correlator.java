package org.ikasan.dashboard.ui.visualisation.correlate;

public interface Correlator<INPUT, OUTPUT>
{
    public OUTPUT correlate(INPUT input);
}
