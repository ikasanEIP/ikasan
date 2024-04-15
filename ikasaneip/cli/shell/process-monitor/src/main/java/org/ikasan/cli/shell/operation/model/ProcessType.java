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
package org.ikasan.cli.shell.operation.model;

/**
 * ProcessType model.
 *
 * @author Ikasan Development Team
 */
public class ProcessType
{
    public static ProcessType H2 = getH2Instance();
    public static ProcessType MODULE = getModuleInstance();
    public static ProcessType SOLR = getSolrInstance();
    public static ProcessType GENERIC = getGenericInstance();

    private String name;
    private boolean persist;
    private String outputLog;
    private String errorLog;

    /** unique way to identify a specific process */
    private String commandSignature;

    public static ProcessType getH2Instance()
    {
        return new ProcessType("H2", true, "logs/h2.log", "logs/h2.log", "org.h2.tools.Server");
    }

    public static ProcessType getModuleInstance()
    {
        return new ProcessType("Module", true, "logs/application.log", "logs/application.log", "spring.jta.logDir");
    }

    public static ProcessType getSolrInstance()
    {
        return new ProcessType("Solr", true, "solr/server/logs/solr.log", "solr/server/logs/solr.log", "-Dsolr.default.confdir=");
    }

    public static ProcessType getGenericInstance()
    {
        return new ProcessType("", false);
    }

    ProcessType(String name, boolean persist)
    {
        this(name, persist, null, null, null);
    }

    ProcessType(String name, boolean persist, String outputLog, String errorLog, String commandSignature)
    {
        this.name = name;
        this.persist = persist;
        this.outputLog = outputLog;
        this.errorLog = errorLog;
        this.commandSignature = commandSignature;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isPersist()
    {
        return this.persist;
    }

    public void setOutputLog(String outputLog)
    {
        this.outputLog = outputLog;
    }

    public void setErrorLog(String errorLog)
    {
        this.errorLog = errorLog;
    }

    public String getOutputLog()
    {
        return this.outputLog;
    }

    public String getErrorLog()
    {
        return this.errorLog;
    }

    public String getCommandSignature()
    {
        return commandSignature;
    }

    public void setCommandSignature(String commandSignature)
    {
        this.commandSignature = commandSignature;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessType that = (ProcessType) o;

        if (persist != that.persist) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (outputLog != null ? !outputLog.equals(that.outputLog) : that.outputLog != null) return false;
        if (errorLog != null ? !errorLog.equals(that.errorLog) : that.errorLog != null) return false;
        return commandSignature != null ? commandSignature.equals(that.commandSignature) : that.commandSignature == null;
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (persist ? 1 : 0);
        result = 31 * result + (outputLog != null ? outputLog.hashCode() : 0);
        result = 31 * result + (errorLog != null ? errorLog.hashCode() : 0);
        result = 31 * result + (commandSignature != null ? commandSignature.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "ProcessType{" +
            "name='" + name + '\'' +
            ", persist=" + persist +
            ", outputLog='" + outputLog + '\'' +
            ", errorLog='" + errorLog + '\'' +
            ", commandSignature='" + commandSignature + '\'' +
            '}';
    }
}