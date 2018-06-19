/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import Entity.Partation;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Moamenovic
 */
@Stateless
public class PartationFacade extends AbstractFacade<Partation> {
    @PersistenceContext(unitName = "POSPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PartationFacade() {
        super(Partation.class);
    }
    
}
