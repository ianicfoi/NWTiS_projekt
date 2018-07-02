/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.slusaci;

import org.foi.nwtis.ivaanic2.dretve.Database; 
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import java.io.File;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ivaanic2.dretve.RadnaDretva;
import org.foi.nwtis.ivaanic2.dretve.ServerSocketDretva;
import org.foi.nwtis.ivaanic2.konfiguracije.Konfiguracija;
import org.foi.nwtis.ivaanic2.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.ivaanic2.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.ivaanic2.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.ivaanic2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.ivaanic2.rest.korisnici.UsersServersResource;
import org.foi.nwtis.ivaanic2.rest.ws.MeteoRESTResource;
import org.foi.nwtis.ivaanic2.rest.ws.MeteoRESTResourceContainer;
import org.foi.nwtis.ivaanic2.ws.GeoMeteoWS;
import org.foi.nwtis.ivaanic2.zrna.PregledKorisnika;
import org.foi.nwtis.ivaanic2.zrna.PregledZahtjeva;
import org.foi.nwtis.ivaanic2.zrna.PregledDnevnika;

/**
 * Slušač web aplikacije
 *
 * @author Ivan
 */
public class SlusacAplikacije implements ServletContextListener {
    
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        
        System.out.println("Kontekst inicijaliziran");
        ServletContext context = sce.getServletContext();
        
        String datoteka = context.getRealPath("/WEB-INF")
                + File.separator
                + context.getInitParameter("konfiguracija");

        BP_Konfiguracija bp_konf = new BP_Konfiguracija(datoteka);
        context.setAttribute("BP_Konfig", bp_konf); 

        Konfiguracija konf = null;
        
        try {
            
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            context.setAttribute("Baza_Konfig", konf); 
            
            Database.sc=context;
            Database.spojiNaBazu();
            
            sleep(1000);
            
            ServerSocketDretva ssd = new ServerSocketDretva();
            ssd.start();

            RadnaDretva radDr = new RadnaDretva(context);
            radDr.start();

            GeoMeteoWS.sc = context;
           
            
            MeteoRESTResourceContainer.sc = context;
            MeteoRESTResource.sc = context;
            UsersServersResource.sc = context;
            PregledKorisnika.sc = context;
            PregledDnevnika.sc = context;
            ServerSocketDretva.context = context;
            PregledZahtjeva.sc=context;

            
            
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            System.err.println(ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
