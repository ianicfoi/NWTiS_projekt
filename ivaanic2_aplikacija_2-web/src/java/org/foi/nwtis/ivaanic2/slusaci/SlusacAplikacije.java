/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.slusaci; 
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import java.io.File;
import javax.ejb.EJB;
import org.foi.nwtis.ivaanic2.dretve.ObradaPoruka;
import org.foi.nwtis.ivaanic2.konfiguracije.Konfiguracija;
import org.foi.nwtis.ivaanic2.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.ivaanic2.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.ivaanic2.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.ivaanic2.konfiguracije.bp.BP_Konfiguracija; 
import org.foi.nwtis.ivaanic2.zrna.PregledMailova;
import org.foi.nwtis.ivaanic2.zrna.PregledKorisnika;

/**
 * Slusac web aplikacije.
 *
 * @author Ivan
 */
public class SlusacAplikacije implements ServletContextListener {
    
    public static int idSession;
    
    public static String usernameSession = "";
    public static String emailSession = "" ;
    public static String passwordSession = "" ;
    
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
            context.setAttribute("Mail_Konfig", konf);
            PregledMailova.sc=context;
            ObradaPoruka.sc=context;
           

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            System.err.println(ex);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
