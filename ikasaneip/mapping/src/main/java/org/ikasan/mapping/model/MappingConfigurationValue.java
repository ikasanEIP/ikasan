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
package org.ikasan.mapping.model;

import java.util.ArrayList;

 /**
  * @author Ikasan Development Team
  *
  */
 public class MappingConfigurationValue
 {

     private TargetConfigurationValue targetConfigurationValue;
     private ArrayList<SourceConfigurationValue> sourceConfigurationValues = new ArrayList<SourceConfigurationValue>();

     /**
      * Default Constructor
      */
     public MappingConfigurationValue()
     {
         super();
     }

     /**
      * Constructor
      *
      * @param targetConfigurationValue
      * @param sourceConfigurationValues
      */
     public MappingConfigurationValue(TargetConfigurationValue targetConfigurationValue,
                                      ArrayList<SourceConfigurationValue> sourceConfigurationValues)
     {
         super();
         this.targetConfigurationValue = targetConfigurationValue;
         this.sourceConfigurationValues = sourceConfigurationValues;
     }

     /**
      * @return the targetConfigurationValue
      */
     public TargetConfigurationValue getTargetConfigurationValue()
     {
         return targetConfigurationValue;
     }

     /**
      * @param targetConfigurationValue the targetConfigurationValue to set
      */
     public void setTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue)
     {
         this.targetConfigurationValue = targetConfigurationValue;
     }

     /**
      * @return the sourceConfigurationValues
      */
     public ArrayList<SourceConfigurationValue> getSourceConfigurationValues()
     {
         return sourceConfigurationValues;
     }

     /**
      * @param sourceConfigurationValues the sourceConfigurationValues to set
      */
     public void setSourceConfigurationValues(ArrayList<SourceConfigurationValue> sourceConfigurationValues)
     {
         this.sourceConfigurationValues = sourceConfigurationValues;
     }

     /**
      * @param sourceConfigurationValues the sourceConfigurationValues to set
      */
     public void addSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
     {
         this.sourceConfigurationValues.add(sourceConfigurationValue);
     }
 }
