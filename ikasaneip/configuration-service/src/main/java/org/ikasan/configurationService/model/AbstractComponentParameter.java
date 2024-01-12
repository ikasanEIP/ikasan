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
package org.ikasan.configurationService.model;

import jakarta.persistence.*;
import org.ikasan.spec.configuration.ConfigurationParameter;

import java.io.Serializable;

/**
 * Abstract Configuration Parameter for modelling common properties of a configuration parameter
 * Ikasan Development Team
 */
@Entity(name = "ConfigurationParameter")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractComponentParameter<T> implements Serializable, ConfigurationParameter<T>
{
    /** required by ORM */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    protected Long id;

    /**
     * Getter for id
     * @return
     */
    public Long getId()
    {
        return this.id;
    }

    /**
     * Setter for id
     * @param id
     */
    public void setId(Long id)
    {
        this.id = id;
    }

//    /**
//     * Getter for name
//     * @return
//     */
//    public String getName()
//    {
//        return this.name;
//    }
//
//    /**
//     * Setter for name
//     * @param name
//     */
//    public void setName(String name)
//    {
//        this.name = name;
//    }

    /**
     * Getter for value
     * @return
     */
    public abstract T getValue();

    /**
     * Setter for value
     * @param value
     */
    public abstract void setValue(T value);

//    /**
//     * Getter for description
//     * @return
//     */
//    public String getDescription()
//    {
//        return this.description;
//    }
//
//    /**
//     * Setter for description
//     * @param description
//     */
//    public void setDescription(String description)
//    {
//        this.description = description;
//    }

}
