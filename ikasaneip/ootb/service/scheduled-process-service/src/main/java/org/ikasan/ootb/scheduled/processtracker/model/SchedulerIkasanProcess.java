package org.ikasan.ootb.scheduled.processtracker.model;

import org.ikasan.cli.shell.operation.model.IkasanProcess;

import java.util.Objects;

public class SchedulerIkasanProcess extends IkasanProcess {
    String resultOutput;
    String errorOutput;
    long fireTme;

    private SchedulerIkasanProcess() {
        // For Kryo to work, the Parameterless Constructor of IkasanProcess must be private ?!
        super("", "", 0L, "");
    }

    public SchedulerIkasanProcess(String type, String name, long pid, String user, String resultOutput, String errorOutput, long fireTime) {
        super(type, name, pid, user);
        this.resultOutput = resultOutput;
        this.errorOutput = errorOutput;
        this.fireTme = fireTime;
    }

    public String getResultOutput() {
        return resultOutput;
    }
    public void setResultOutput(String resultOutput) {
        this.resultOutput = resultOutput;
    }

    public String getErrorOutput() {
        return errorOutput;
    }

    public long getFireTme() {
        return fireTme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SchedulerIkasanProcess that = (SchedulerIkasanProcess) o;
        return fireTme == that.fireTme && Objects.equals(resultOutput, that.resultOutput) && Objects.equals(errorOutput, that.errorOutput);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resultOutput, errorOutput, fireTme);
    }

    @Override
    public String toString() {
        return "SchedulerIkasanProcess{" +
            "resultOutput='" + resultOutput + '\'' +
            ", errorOutput='" + errorOutput + '\'' +
            ", fireTme=" + fireTme +
            '}';
    }
}
