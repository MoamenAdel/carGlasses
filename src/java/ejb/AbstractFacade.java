/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import org.primefaces.model.SortOrder;

/**
 *
 * @author hp
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public T edit(T entity) {
        return getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public List<T> findRange(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        EntityType entityType = getEntityManager().getMetamodel().entity(entityClass);
        Root<T> entity = cq.from(entityClass);
        cq.select(entity);
        Predicate filterCondition = cb.conjunction();

        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            if (!filter.getValue().equals("")) {
//                if (filter.getValue() instanceof String) {
//                    filterCondition = cb.and(filterCondition, cb.like(entity.get(filter.getKey()), "%" + filter.getValue() + "%"));
//                    System.out.println(filter.getValue());
//                } else {
                Path pathFilter = entity;
                for (String object : filter.getKey().split("\\.")) {
                    pathFilter = pathFilter.get(object);
                }
                if (filter.getValue() instanceof Date) {
                    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                    filterCondition = cb.and(filterCondition, cb.like(pathFilter, "%" + date.format((Date) filter.getValue()) + "%"));
                    System.out.println(date.format((Date) filter.getValue()));
                } else {
                    filterCondition = cb.and(filterCondition, cb.like(pathFilter, "%" + filter.getValue() + "%"));
                }
//                }
            }
        }
        cq.where(filterCondition);
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return q.getResultList();

    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
