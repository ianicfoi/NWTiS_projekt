/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.ivaanic2.sesije.SessionUtils;
import org.foi.nwtis.ivaanic2.slusaci.SlusacAplikacije;
import org.foi.nwtis.ivaanic2.web.podaci.Korisnik;
import org.foi.nwtis.ivaanic2.ws.klijenti.MeteoWSKlijent;

/**
 *
 * @author Ivan
 */
@Named(value = "sviKorisniciIAzuriranje")
@SessionScoped
public class PregledKorisnika implements Serializable {

    public static ServletContext sc;
    
    private List<Korisnik> korisnici = new ArrayList<>();
    private String ispis = "";
    private String username = "";
    private String password = "";
    private String email = "";
    
    public static String eml = "";
    private String odgovor = "";

    /**
     * Creates a new instance of PregledKorisnika
     */
    public PregledKorisnika() {
        HttpSession session = SessionUtils.getSession();
        System.out.println("Naziv sesije:" + session.getAttribute("username"));

    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }

    public String getIspis() {
        return ispis;
    }

    public String getUsername() {

        username = SessionUtils.getUserName();
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        password = SessionUtils.getPassword();
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        email = SlusacAplikacije.emailSession;
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIspis(String ispis) {
        this.ispis = ispis;
    }

    public List<Korisnik> getKorisnici() {
        dohvatiKorisnike();
        return korisnici;
    }

    public void setKorisnici(List<Korisnik> korisnici) {
        this.korisnici = korisnici;
    }

    /**
     * Koristenje web servisa za azuriranje korisnika
     */
    public void azurirajKorisnika() {

        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("id", SessionUtils.getUserId());
        job.add("username", this.username);
        job.add("password", this.password);
        job.add("email", this.email);
        String json = MeteoWSKlijent.upodateUserREST(job.build());
        System.err.println("JSON:   " + json);
        if (json.equals("0")) {
            this.odgovor = "A";
            return;
        }
        HttpSession sesija = SessionUtils.getSession();
        sesija.setAttribute("username", this.username);
        sesija.setAttribute("password", this.password);
    }

    /**
     * Koristenje web servisa za dohvacanje korisnika.
     */
    public void dohvatiKorisnike() {
        System.out.println("Naziv:  " + SessionUtils.getUserName());
        System.out.println("Mail: " + SlusacAplikacije.emailSession + PregledKorisnika.eml);
        
        String json = MeteoWSKlijent.dohvatiSveUsereREST();
        System.out.println(json);
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();
        System.out.println("Iznos" + array.size());
        korisnici.clear();
        
        
        for (JsonValue a : array) {
            JsonReader reader = Json.createReader(new StringReader(a.toString()));
            JsonObject jo = reader.readObject();
            int id = (jo.getInt("id"));
            String username = jo.getString("username");
            String pass = jo.getString("password");
            String email = jo.getString("email");
            String id1 = "" + id;
            Korisnik novi = new Korisnik(id1, username, pass, email);
            korisnici.add(novi);
        }
    }

}
