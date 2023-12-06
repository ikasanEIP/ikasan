package org.ikasan.security.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class XAHibernateSecurityDao extends HibernateSecurityDao {

    @PersistenceContext(unitName = "security-xa")
    protected EntityManager entityManager;
}
