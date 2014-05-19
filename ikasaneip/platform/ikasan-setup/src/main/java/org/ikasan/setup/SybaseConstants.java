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
package org.ikasan.setup;

/**
 * Sybase constants for creation of required sql tables for persistence.
 * Ikasan Development Team.
 */
public interface SybaseConstants
{
    public static String CREATE_WIRETAP_TABLE = "CREATE TABLE IkasanWiretap\n" +
            "(\n" +
            "    Id                  NUMERIC IDENTITY NOT NULL,\n" +
            "    ModuleName          VARCHAR(128)  NOT NULL,\n" +
            "    FlowName            VARCHAR(128)  NOT NULL,\n" +
            "    ComponentName       VARCHAR(128)  NOT NULL,\n" +
            "    EventId             VARCHAR(255)  NOT NULL,\n" +
            "    RelatedEventId      VARCHAR(255)  NULL,\n" +
            "    EventTimestamp      NUMERIC DEFAULT 0 NOT NULL,\n" +
            "    PayloadContent      TEXT    NOT NULL,\n" +
            "    CreatedDateTime     NUMERIC NOT NULL,\n" +
            "    Expiry              NUMERIC NOT NULL\n" +
            ")\n" +
            "LOCK DATAROWS\n" +
            "WITH IDENTITY_GAP=1\n" +
            "\n" +
            "CREATE UNIQUE INDEX IkasanWiretap01u ON IkasanWiretap(Id)";

    public static String CREATE_FLOW_EVENT_TRIGGER_TABLE = "CREATE TABLE FlowEventTrigger\n" +
            "(\n" +
            "    Id                  NUMERIC IDENTITY NOT NULL,\n" +
            "    ModuleName          VARCHAR(128)  NOT NULL,\n" +
            "    FlowName            VARCHAR(128)  NOT NULL,\n" +
            "    Relationship        VARCHAR(32)  NOT NULL,\n" +
            "    FlowElementName     VARCHAR(128),\n" +
            "    JobName           \tVARCHAR(64)   NOT NULL\n" +
            ")\n" +
            "LOCK DATAROWS\n" +
            "WITH IDENTITY_GAP=1\n" +
            "\n" +
            "CREATE UNIQUE INDEX FlowEventTrigger01u ON FlowEventTrigger(Id)";

    public static String CREATE_FLOW_EVENT_TRIGGER_PARAMS_TABLE = "CREATE TABLE FlowEventTriggerParameters\n" +
            "(\n" +
            "    TriggerId          NUMERIC NOT NULL,\n" +
            "    ParamName          VARCHAR(128)  NOT NULL,\n" +
            "    ParamValue            VARCHAR(128) \n" +
            ")\n" +
            "  ALTER TABLE FlowEventTriggerParameters\n" +
            "    ADD CONSTRAINT FlowEventTriggerParam_Id_FK\n" +
            "    FOREIGN KEY (TriggerId)\n" +
            "    REFERENCES FlowEventTrigger (Id)  \n";

    public static String CREATE_CONFIGURATION_TABLE = "CREATE TABLE Configuration\n" +
            "(\n" +
            "    ConfigurationId         VARCHAR(256) NOT NULL,\n" +
            "    Description             VARCHAR(256) DEFAULT NULL NULL\n" +
            ")\n" +
            "LOCK DATAROWS\n" +
            "WITH IDENTITY_GAP=1\n" +
            "\n" +
            "CREATE UNIQUE INDEX Configuration01u ON Configuration(ConfigurationId)\n";

    public static String CREATE_CONFIGURATION_PARAMS_TABLE = "CREATE TABLE ConfigurationParameter\n" +
            "(\n" +
            "    ConfigurationIdentifier     VARCHAR(256) NOT NULL,\n" +
            "    PositionRef                 NUMERIC,\n" +
            "    Name                        VARCHAR(128) NOT NULL,\n" +
            "    Value                       VARCHAR(256) DEFAULT NULL NULL,\n" +
            "    Description                 VARCHAR(256) DEFAULT NULL NULL\n" +
            ")\n" +
            "LOCK DATAROWS\n" +
            "WITH IDENTITY_GAP=1\n" +
            "\n" +
            "CREATE UNIQUE INDEX ConfigurationParameter01u ON ConfigurationParameter(ConfigurationIdentifier, PositionRef)\n";

    public static String CREATE_STARTUP_MODULE_TABLE = "CREATE TABLE StartupControl\n" +
            "(\n" +
            "    Id                  NUMERIC IDENTITY NOT NULL,\n" +
            "    ModuleName          VARCHAR(128)  NOT NULL,\n" +
            "    FlowName            VARCHAR(128)  NOT NULL,\n" +
            "    StartupType         VARCHAR(16)   NOT NULL,\n" +
            "    Comment             VARCHAR(256)  NULL\n" +
            ")\n" +
            "LOCK DATAROWS\n" +
            "WITH IDENTITY_GAP=1\n" +
            "\n" +
            "CREATE UNIQUE INDEX StartupControl01u ON StartupControl(Id)";

    public static String CREATE_CONSOLE_MODULE_TABLE = "CREATE TABLE Module(\n" +
            "    Id               NUMERIC IDENTITY NOT NULL PRIMARY KEY,\n" +
            "    Name             VARCHAR(255) NOT NULL,\n" +
            "    Description      VARCHAR(255) NOT NULL,\n" +
            "    DesignDiagramURL VARCHAR(255) NULL,\n" +
            "    UNIQUE (Name)\n" +
            ")";
    public static String CREATE_CONSOLE_POINT_TO_POINT_PROFILE_TABLE = "CREATE TABLE PointToPointFlowProfile(\n" +
            "    Id          NUMERIC IDENTITY NOT NULL PRIMARY KEY,\n" +
            "    Name        VARCHAR(255) NOT NULL\n" +
            ")";
    public static String CREATE_CONSOLE_POINT_TO_POINT_TABLE = "CREATE TABLE PointToPointFlow(\n" +
            "    Id                        NUMERIC IDENTITY NOT NULL PRIMARY KEY,\n" +
            "    PointToPointFlowProfileId NUMERIC NOT NULL,\n" +
            "    FromModuleId              NUMERIC NULL,\n" +
            "    ToModuleId                NUMERIC NULL,\n" +
            "    CONSTRAINT PTPP_ID_FK FOREIGN KEY(PointToPointFlowProfileId) REFERENCES PointToPointFlowProfile(Id)\n" +
            ")";
    public static String CREATE_SECURITY_USERS_TABLE = "CREATE TABLE Users\n" +
            "(\n" +
            "    Id       NUMERIC IDENTITY NOT NULL PRIMARY KEY,\n" +
            "    Username VARCHAR(50) NOT NULL UNIQUE,\n" +
            "    Password VARCHAR(50) NOT NULL,\n" +
            "    Email    VARCHAR(255) NULL,\n" +
            "    Enabled  BIT NOT NULL\n" +
            ")\n" +
            "LOCK DATAROWS\n" +
            "WITH IDENTITY_GAP=1\n";

    public static String CREATE_SECURITY_AUTHORITIES_TABLE = "CREATE TABLE Authorities\n" +
            "(\n" +
            "    Id          NUMERIC IDENTITY NOT NULL PRIMARY KEY,\n" +
            "    Authority   VARCHAR(50) NOT NULL UNIQUE,\n" +
            "    Description VARCHAR(512)\n" +
            ")\n" +
            "LOCK DATAROWS\n" +
            "WITH IDENTITY_GAP=1";

    public static String CREATE_SECURITY_USERS_AUTHORITIES_TABLE = "CREATE TABLE UsersAuthorities\n" +
            "(\n" +
            "    UserId NUMERIC NOT NULL,\n" +
            "    AuthorityId NUMERIC NOT NULL,\n" +
            "    PRIMARY KEY (UserId,AuthorityId),\n" +
            "    CONSTRAINT USER_AUTH_USER_FK FOREIGN KEY(UserId) REFERENCES Users(Id),\n" +
            "    CONSTRAINT USER_AUTH_AUTH_FK FOREIGN KEY(AuthorityId) REFERENCES Authorities(Id)\n" +
            ")";
}
