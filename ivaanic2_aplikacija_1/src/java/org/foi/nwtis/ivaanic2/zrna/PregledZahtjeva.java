/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.zrna;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.foi.nwtis.ivaanic2.konfiguracije.Konfiguracija;
import org.foi.nwtis.ivaanic2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.ivaanic2.rest.ws.MeteoRESTResourceContainer;
import org.foi.nwtis.ivaanic2.web.podaci.Korisnik;

/**
 * Zrno za jsf za pregled zahtjeva.
 *
 * @author Ivan
 */
public class PregledZahtjeva {

    
    public static ServletContext sc;
    Connection c;
    
    
    private List<Korisnik> listaSvihKorisnika = new ArrayList<>();
    private List<Korisnik> listaTrenutnihKorisnika = new ArrayList<>();
    
    private int brojPrikaza;
    private boolean init = false;
    private int pocetakKorisnika = 0;
    private int krajKorisnika = 0;
    
    
    
    
    /**
     * Creates a new instance of PregledZahtjeva
     */
    public PregledZahtjeva() {
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
        this.brojPrikaza = Integer.parseInt(konf.dajPostavku("broj.prikaza"));
        preuzmiKorisnike();
    }
    
    
    
    
    public static ServletContext getSc() {
        return sc;
    }

    public static void setSc(ServletContext sc) {
        PregledKorisnika.sc = sc;
    }

    public Connection getC() {
        return c;
    }

    public void setC(Connection c) {
        this.c = c;
    }

    public List<Korisnik> getListaSvihKorisnika() {
        return listaSvihKorisnika;
    }

    public void setListaSvihKorisnika(List<Korisnik> listaSvihKorisnika) {
        this.listaSvihKorisnika = listaSvihKorisnika;
    }

    public List<Korisnik> getListaTrenutnihKorisnika() {
        return listaTrenutnihKorisnika;
    }

    public void setListaTrenutnihKorisnika(List<Korisnik> listaTrenutnihKorisnika) {
        this.listaTrenutnihKorisnika = listaTrenutnihKorisnika;
    }

    public int getBrojPrikaza() {
        return brojPrikaza;
    }

    public void setBrojPrikaza(int brojPrikaza) {
        this.brojPrikaza = brojPrikaza;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public int getPocetakKorisnika() {
        return pocetakKorisnika;
    }

    public void setPocetakKorisnika(int pocetakKorisnika) {
        this.pocetakKorisnika = pocetakKorisnika;
    }

    public int getKrajKorisnika() {
        return krajKorisnika;
    }

    
    public void setKrajKorisnika(int krajKorisnika) {
        this.krajKorisnika = krajKorisnika;
    }

  
    /**
     * preuzima sve zahtjeve iz baze
     */
    public void preuzmiKorisnike()
    {
        try {
            listaSvihKorisnika.clear();
            spojiNaBazu();
            String query = "select * from zahtjevi";
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            int brojac = 0;
            listaTrenutnihKorisnika.clear();
            System.err.println("Broj prikaza: "+this.brojPrikaza+"pocetak: "+this.pocetakKorisnika+" max: "+this.brojPrikaza+this.pocetakKorisnika);
            while(rs.next()){
                 Korisnik novi = new Korisnik(rs.getString("id"),rs.getString("zahtjev"),rs.getString("korisnik"),rs.getString("vrijeme"));
                if(brojac>=this.pocetakKorisnika && this.brojPrikaza+this.pocetakKorisnika>brojac){
                
                  listaSvihKorisnika.add(novi);
                }
                     listaTrenutnihKorisnika.add(novi);
               brojac++;
            }
                    } catch (SQLException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * stranicenje unazad
     */
    public void prethodniKorisnici(){
       
        if(this.pocetakKorisnika-this.brojPrikaza<0){
            return;
        }
            this.pocetakKorisnika=this.pocetakKorisnika-this.brojPrikaza;
            preuzmiKorisnike();
    }
    
        /**
         * stranicenje unaprijed
         */
        public void sljedeciKorisnici(){
            
             if(this.pocetakKorisnika+this.brojPrikaza>listaTrenutnihKorisnika.size()){
            return;
        }
            this.pocetakKorisnika=this.pocetakKorisnika+this.brojPrikaza;
             preuzmiKorisnike();
    }
    
    /**
     * Konektiranje na bazu na temelju postavki iz datoteke konfiguracije.
     */
       public void spojiNaBazu() {
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
        
        try {
            c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                    bp_konf.getUserUsername(),
                    bp_konf.getUserPassword());
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
       
       /**
        * Zapisuje argumente u tablicu dnevnika u bazu.
        * @param trajanje
        * @param status
        * @param url 
        */
        public void zapisiUDnevnik(int trajanje, int status,String url) {
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String strDate = sdfDate.format(now);
            spojiNaBazu();
            String query="insert into dnevnik values(default,'ivaanic2','"+url+"','localhost','"+strDate+"', "+trajanje+", "+status+")";
            Statement s = c.createStatement();
            s.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(MeteoRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
