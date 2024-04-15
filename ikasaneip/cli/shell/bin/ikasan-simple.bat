@echo off
rem /*
rem  * $Id$
rem  * $URL$
rem  *
rem  * Do not change this script. For environment specifics change the env.sh script.
rem  *
rem  * ====================================================================
rem  * Ikasan Enterprise Integration Platform
rem  *
rem  * Distributed under the Modified BSD License.
rem  * Copyright notice: The copyright for this software and a full listing
rem  * of individual contributors are as shown in the packaged copyright.txt
rem  * file.
rem  *
rem  * All rights reserved.
rem  *
rem  * Redistribution and use in source and binary forms, with or without
rem  * modification, are permitted provided that the following conditions are met:
rem  *
rem  *  - Redistributions of source code must retain the above copyright notice,
rem  *    this list of conditions and the following disclaimer.
rem  *
rem  *  - Redistributions in binary form must reproduce the above copyright notice,
rem  *    this list of conditions and the following disclaimer in the documentation
rem  *    and/or other materials provided with the distribution.
rem  *
rem  *  - Neither the name of the ORGANIZATION nor the names of its contributors may
rem  *    be used to endorse or promote products derived from this software without
rem  *    specific prior written permission.
rem  *
rem  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
rem  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
rem  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
rem  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
rem  * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
rem  * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
rem  * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
rem  * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
rem  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
rem  * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
rem  * ====================================================================
rem  */

rem Do not change this script, change the env.sh script fpr you custom environment settings.
rem this script assumes you are using the filesystem for application.properties
set currentDir=%CD%

set localEnv=%currentDir%\simple-env.bat

IF EXIST %localEnv% (
    CALL %localEnv%
)
"%JAVA_HOME%\bin\java" -Dspring.cloud.config.enabled=false -jar %currentDir%\lib\ikasan-shell-${project.version}.jar %*
