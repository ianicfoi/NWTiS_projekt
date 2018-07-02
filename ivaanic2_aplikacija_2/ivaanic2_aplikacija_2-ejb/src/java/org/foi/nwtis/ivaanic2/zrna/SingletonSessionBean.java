/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.zrna;

import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import org.foi.nwtis.ivaanic2.dretve.ObradaPoruka;

/**
 * Pokrece dretvu obrada poruka.
 *
 * @author Ivan
 */
@Singleton
@LocalBean
public class SingletonSessionBean {
    
    public ObradaPoruka op;
    
    public SingletonSessionBean() {
        
        op = new ObradaPoruka();
         op.start();
    }
    
    
    

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
 
}
