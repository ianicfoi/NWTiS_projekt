/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext; 
import org.foi.nwtis.ivaanic2.web.podaci.Izbornik;

/**
 * @Ivan
 * 
 * Bean za visejezicnost.
 */
@Named(value = "lokalizator")
@SessionScoped
public class Lokalizacija implements Serializable {

    private static final ArrayList<Izbornik> izbornikJezika = new ArrayList<>();
    private String odabraniJezik;
      
    static {
        izbornikJezika.add(new Izbornik("hrvatski", "hr"));
        izbornikJezika.add(new Izbornik("engleski", "en"));
    }
    
    /**
     * Creates a new instance of Lokalizacija
     */
    public Lokalizacija() {
    }

    public ArrayList<Izbornik> getIzbornikJezika() {
        return izbornikJezika;
    }

    public String getOdabraniJezik() {
        //FacesContext FC = FacesContext.getCurrentInstance();
        UIViewRoot UIVR = FacesContext.getCurrentInstance().getViewRoot();
        if(UIVR != null){
            Locale lokalniJezik = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            odabraniJezik = lokalniJezik.getLanguage();
        }
        return odabraniJezik;
    }

    public void setOdabraniJezik(String odabraniJezik) {
        this.odabraniJezik = odabraniJezik;
        Locale lokalniJezik = new Locale(odabraniJezik);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(lokalniJezik);
    }
    
     public Object odaberiJezik(){
         setOdabraniJezik(odabraniJezik);
         return "PromjenaJezika";
     }
    
     public Object saljiPoruku(){
         return "saljiPoruku";
     }
     public Object pregledPoruka(){
         return "pregledPoruka";
     }
}
