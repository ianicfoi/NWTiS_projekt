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
import org.foi.nwtis.ivaanic2.web.podaci.Dnevnik;

/**
 * Zrno koje sluzi za pregled dnevnika u jsf.
 *
 * @author Ivan
 */
public class PregledDnevnika {
    
    public static ServletContext sc;
    Connection c;
    
    private List<Dnevnik> listaSvihKorisnika = new ArrayList<>();
    private List<Dnevnik> listaTrenutnihKorisnika = new ArrayList<>();
    
    private int brojPrikaza;
    
    private boolean init = false;
    private int pocetakKorisnika = 0;
    private int krajKorisnika = 0;

   public PregledDnevnika() {
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

    public List<Dnevnik> getListaSvihKorisnika() {
        return listaSvihKorisnika;
    }

    public void setListaSvihKorisnika(List<Dnevnik> listaSvihKorisnika) {
        this.listaSvihKorisnika = listaSvihKorisnika;
    }

    public List<Dnevnik> getListaTrenutnihKorisnika() {
        return listaTrenutnihKorisnika;
    }

    public void setListaTrenutnihKorisnika(List<Dnevnik> listaTrenutnihKorisnika) {
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
     * Pomoću sql upita se dohvacaju svi korisnici. Poziva se kod metoda za stranicenje.
     */
    public void preuzmiKorisnike()
    {
        try {
            listaSvihKorisnika.clear();
            spojiNaBazu();
            String query = "select * from dnevnik";
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            int brojac=0;
           listaTrenutnihKorisnika.clear();
            while(rs.next()){
                 Dnevnik novi = new Dnevnik(rs.getInt("id"),rs.getString("korisnik"),rs.getString("url"),rs.getString("ipadresa"),rs.getDate("vrijeme"),rs.getInt("trajanje"),rs.getInt("status"));
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
     * Straničenje prema unazad.
     */
    public void prethodniKorisnici(){
        if(this.pocetakKorisnika-this.brojPrikaza<0){
            return;
        }
            this.pocetakKorisnika=this.pocetakKorisnika-this.brojPrikaza;
            preuzmiKorisnike();
    }
     
        /**
         * Straničenje prema naprijed.
         */
        public void sljedeciKorisnici(){
            
             if(this.pocetakKorisnika+this.brojPrikaza>listaTrenutnihKorisnika.size()){
            return;
        }
            this.pocetakKorisnika=this.pocetakKorisnika+this.brojPrikaza;
             preuzmiKorisnike();
    }
    
        public String korisnici(){
            return "korisnici";
        }
        
        /**
         * Metoda za spajanje na bazu i preuzimanje kor imena i lozinke.
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
        * Zapisuje argumente u dnevnik u bazi.
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
