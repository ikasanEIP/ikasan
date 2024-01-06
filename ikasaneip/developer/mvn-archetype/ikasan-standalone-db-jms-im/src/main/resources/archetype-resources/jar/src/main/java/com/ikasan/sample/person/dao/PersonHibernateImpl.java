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
package com.ikasan.sample.person.dao;

import com.ikasan.sample.person.model.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

/**
 * Hibernate implementation for a PersonDAO
 * @author Ikasan Development Team
 */
public class PersonHibernateImpl implements PersonDao
{
    @PersistenceContext(unitName = "person")
    EntityManager entityManager;

    @Override
    public Person findById(long id)
    {
        return this.entityManager.find(Person.class, id);
    }

    @Override
    public void saveOrUpdate(Person person)
    {
        this.entityManager.persist(entityManager.contains(person)
                ? person : entityManager.merge(person));
    }

    @Override
    public void delete(Person person)
    {
        this.entityManager.remove(entityManager.contains(person)
                ? person : entityManager.merge(person));
    }

    @Override
    public List<Person> findAll()
    {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> rootEntry = cq.from(Person.class);
        CriteriaQuery<Person> all = cq.select(rootEntry);
        TypedQuery<Person> allQuery = this.entityManager.createQuery(all);
        return allQuery.getResultList();
    }


}
