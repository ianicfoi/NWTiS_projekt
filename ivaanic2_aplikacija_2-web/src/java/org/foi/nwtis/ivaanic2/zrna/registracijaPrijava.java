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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Cookie; 
import org.foi.nwtis.ivaanic2.sesije.SessionUtils;
import org.foi.nwtis.ivaanic2.slusaci.SlusacAplikacije;
import org.foi.nwtis.ivaanic2.web.podaci.Korisnik;
import org.foi.nwtis.ivaanic2.ws.klijenti.MeteoWSKlijent;

/**
 *
 * @author Ivan
 */
@Named(value = "registracijaPrijava")
@SessionScoped
public class registracijaPrijava implements Serializable {

     
    /**
     * Creates a new instance of registracijaPrijava
     */
        
    private String ipAdresa = "";
    private String trajanje = "";
    private String status = ""; 
    
    private boolean filtrirano = false;
    private String reg = "";

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    
    
    
    public String getIpAdresa() {
        return ipAdresa;
    }

    public void setIpAdresa(String ipAdresa) {
        this.ipAdresa = ipAdresa;
    }

    public String getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(String trajanje) {
        this.trajanje = trajanje;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * Dohvacanje svih stavka iz Dnevnika
     * @return 
     */
    

    public boolean isFiltrirano() {
        return filtrirano;
    }

    public void setFiltrirano(boolean filtrirano) {
        this.filtrirano = filtrirano;
    }

 
        
    private String ispis = "";
    private String username = "";
    private String password = "";
    private String email = "";
    private String usernameReg = "";
    private String passwordReg = "";
    private String passwordReg2 = "";
    private String emailReg = "";
    private String regGreska = "";
    public static String usernameSesija = "";
    public static int idSesija;
    public static String emailSesija;
    public static String passwordSesija;
    private String priGreska = "";

    public String getPriGreska() {
        return priGreska;
    }

    public void setPriGreska(String priGreska) {
        this.priGreska = priGreska;
    }

    public String getRegGreska() {
        return regGreska;
    }

    public String getPasswordReg2() {
        return passwordReg2;
    }

    public void setPasswordReg2(String passwordReg2) {
        this.passwordReg2 = passwordReg2;
    }

    public static String getUsernameSesija() {
        return usernameSesija;
    }

    public static void setUsernameSesija(String usernameSesija) {
        registracijaPrijava.usernameSesija = usernameSesija;
    }

    public static int getIdSesija() {
        return idSesija;
    }

    public static void setIdSesija(int idSesija) {
        registracijaPrijava.idSesija = idSesija;
    }

    public static String getEmailSesija() {
        return emailSesija;
    }

    public static void setEmailSesija(String emailSesija) {
        registracijaPrijava.emailSesija = emailSesija;
    }

    public static String getPasswordSesija() {
        return passwordSesija;
    }

    public static void setPasswordSesija(String passwordSesija) {
        registracijaPrijava.passwordSesija = passwordSesija;
    }

    public void setRegGreska(String regGreska) {
        this.regGreska = regGreska;
    }

    public registracijaPrijava() { 
    }

    public String getUsernameReg() {
        return usernameReg;
    }

    public void setUsernameReg(String usernameReg) {
        this.usernameReg = usernameReg;
    }

    public String getPasswordReg() {
        return passwordReg;
    }

    public void setPasswordReg(String passwordReg) {
        this.passwordReg = passwordReg;
    }

    public String getEmailReg() {
        return emailReg;
    }

    public void setEmailReg(String emailReg) {
        this.emailReg = emailReg;
    }

    public String getIspis() {
        return ispis;
    }

    public void setIspis(String ispis) {
        this.ispis = ispis;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
/**
 * prijava korisnika koja se izvodi web servisa
 * @return 
 */
    public String prijaviSe() {

        long pocetak = System.currentTimeMillis();
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();

        if (this.username.isEmpty() || this.password.isEmpty()) {
            System.out.println("Popunite sva polja" + this.username);
            System.out.println("Popunite sva polja" + this.email);
            System.out.println("Popunite sva polja" + this.password);
            this.priGreska = "A";
            return "";
        }
        String json = MeteoWSKlijent.registracijaREST(this.username);
        if (json.equals("[]")) {
            System.out.println("Ne postoji username");
        }
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();
        System.out.println("Velicina" + array.size());
        for (JsonValue aa : array) {
            JsonReader reader = Json.createReader(new StringReader(aa.toString()));
            JsonObject jo = reader.readObject();
            int id = jo.getInt("uid");
            String username = jo.getString("username");
            String pass = jo.getString("password");
            String email = jo.getString("email");
            System.out.println(username);
            if (pass.equals(this.password)) {
                System.out.println("Ulogirani ste");
                HttpSession session = SessionUtils.getSession();
                session.setAttribute("username", this.username);
                session.setAttribute("password", this.password);
                session.setAttribute("userid", id);
                return "korisnici";
            } else {
                System.out.println("Kriva lozinka");
                this.priGreska = "A";
                return "";
            }
        }

        this.priGreska = "A";
        return "";
    }
    
/**
 * registracija korisnika preko web servisa i validacija podataka
 */
    public void registrirajSe() {

        if (this.usernameReg.isEmpty() || this.passwordReg.isEmpty() || this.emailReg.isEmpty()) {
            System.out.println("Popunite sva polja" + this.usernameReg);
            System.out.println("Popunite sva polja" + this.emailReg);
            System.out.println("Popunite sva polja" + this.passwordReg);
            this.regGreska = "A";
            return;
        }
        if (this.passwordReg.equals(this.passwordReg2)) {

        } else {
            this.regGreska = "A";
            System.out.println("Lozinke su razlicite");
            return;
        }
        String json = MeteoWSKlijent.registracijaREST(this.usernameReg);
        System.out.println(MeteoWSKlijent.registracijaREST(this.username));
        if (json.equals("[]")) {
            JsonObjectBuilder job = Json.createObjectBuilder();
            job.add("username", this.usernameReg);
            job.add("password", this.passwordReg);
            job.add("email", this.emailReg);
            System.out.println("USPJESNO: " + MeteoWSKlijent.registrirajREST(job.build()));
            this.reg="A";
            this.regGreska = "b";
        } else {
            this.regGreska = "A";
            this.reg="b";
            System.out.println("Korisnicko ime vec postoji");
        }

    }

}
