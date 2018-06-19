/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import Entity.CarGlass;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Moamenovic
 */
@Stateless
public class CarGlassFacade extends AbstractFacade<CarGlass> {

    @PersistenceContext(unitName = "POSPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<CarGlass> carGlassByIds(Long categoryId, Long inventoryId, Long modelId, Long partationId, Long positionId) {
            StringBuilder sb = new StringBuilder();
        sb.append("SELECT cg FROM CarGlass cg  WHERE 1=1 ");
       
        if (categoryId != null) {
            sb.append(" and cg.category.id = :categoryId");
        }
        if (inventoryId != null) {
            sb.append(" and cg.inventory.id = :inventoryId");
        }
        if (modelId != null) {
            sb.append(" and cg.model.id = :modelId");
        }
        if (partationId != null) {
            sb.append(" and cg.partation.id = :partationId");
        }
        if (positionId != null) {
            sb.append(" and cg.position.id = :positionId");
        }
         TypedQuery<CarGlass> query = em.createQuery(sb.toString(), CarGlass.class);
         if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        if (inventoryId != null) {
            query.setParameter("inventoryId", inventoryId);
        }
        if (modelId != null) {
            query.setParameter("modelId", modelId);
        }
        if (partationId != null) {
            query.setParameter("partationId", partationId);
        }
        if (positionId != null) {
            query.setParameter("positionId", positionId);
        }
        return query.getResultList();
    }

    public CarGlassFacade() {
        super(CarGlass.class);
    }

}
