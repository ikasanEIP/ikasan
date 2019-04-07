package org.ikasan.persistance.jpa.helper;

import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JPAHelper
{
    public static List getAllEntities(Session session, Class clazz){

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = builder.createQuery(clazz);
        Root root = criteriaQuery.from(clazz);
        criteriaQuery.select(root);
        return session.createQuery(criteriaQuery).getResultList();
    }

    public static Long getAllCount(Session session, Class clazz){

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root root = criteriaQuery.from(clazz);
        criteriaQuery.select(root);

        List<Long> rowCountList = session.createQuery(criteriaQuery).getResultList();
        Long rowCount = new Long(0);
        if (!rowCountList.isEmpty())
        {
            rowCount = rowCountList.get(0);
        }
        return rowCount;
    }
}
