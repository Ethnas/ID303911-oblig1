package com.erls.innlevering1.fant;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.erls.innlevering1.auth.Group;

/**
 *
 * @author mikael
 */
@Singleton
@Startup
public class RunOnStartup {
    @PersistenceContext
    EntityManager em;

   
    
    @PostConstruct
    public void init() {
        System.out.println("Wrrooom! " + new Date());
        long groups = (long) em.createQuery("SELECT count(g.name) from Group g").getSingleResult();
        if(groups == 0) {
            em.persist(new Group(Group.USER));
            em.persist(new Group(Group.ADMIN));
        }
       
    }
}
