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
package com.ikasan.sample.person.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.time.LocalDate;
import java.time.Period;

/**
 * Person model
 *
 * @author Ikasan Development Team
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "id",
        "name",
        "dobDayOfMonth",
        "dobMonthOfYear",
        "dobYear"
})
@XmlRootElement(name = "person")
public class Person
{
    // surrogate id assigned by persistence
    long id;

    // person name
    String name;

    // date of birth day
    int dobDayOfMonth;

    // date of birth month
    int dobMonthOfYear;

    // date of birth dobYear
    int dobYear;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getDobDayOfMonth()
    {
        return dobDayOfMonth;
    }

    public void setDobDayOfMonth(int dobDayOfMonth)
    {
        this.dobDayOfMonth = dobDayOfMonth;
    }

    public int getDobMonthOfYear()
    {
        return dobMonthOfYear;
    }

    public void setDobMonthOfYear(int dobMonthOfYear)
    {
        this.dobMonthOfYear = dobMonthOfYear;
    }

    public int getDobYear()
    {
        return dobYear;
    }

    public void setDobYear(int dobYear)
    {
        this.dobYear = dobYear;
    }

    public int getAge()
    {
        return Period.between( LocalDate.of(dobYear, dobMonthOfYear, dobDayOfMonth), LocalDate.now()).getYears();
    }

    @Override
    public String toString()
    {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dobDayOfMonth=" + dobDayOfMonth +
                ", dobMonthOfYear=" + dobMonthOfYear +
                ", dobYear=" + dobYear +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;
        return (id == person.id);
    }

    @Override
    public int hashCode()
    {
        return (int) (id ^ (id >>> 32));
    }
}
