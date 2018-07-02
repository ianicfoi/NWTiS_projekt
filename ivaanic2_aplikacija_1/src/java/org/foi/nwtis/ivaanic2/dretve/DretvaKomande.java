/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import org.foi.nwtis.ivaanic2.konfiguracije.Konfiguracija;
import org.foi.nwtis.ivaanic2.rest.klijenti.GMKlijent;
import org.foi.nwtis.ivaanic2.web.podaci.Lokacija;
import org.foi.nwtis.ivaanic2.master.Iot_Master;
import org.foi.nwtis.dkermek.ws.serveri.StatusKorisnika;
import org.foi.nwtis.dkermek.ws.serveri.Uredjaj;

/**
 * Dretva koja u sebi pokreće radnu dretvu, obrađuje zahtjeve i radi sa
 * servisima.
 *
 * @author Ivan
 */
public class DretvaKomande extends Thread {

    private ServerSocket serverSocket;
    public Socket s;
    public InputStream is;
    public OutputStream os;
    public ServletContext sc;
    
    public boolean proslaNaredba = false;
    
    public String zahtjev = "";
    public String korisnik = "";
    public String lozinka = "";
    

    public DretvaKomande(Socket s, ServletContext sc) {
        this.s = s;
        this.sc = sc;
    }

    @Override
    public void interrupt() {
        try {
            s.close();
        } catch (IOException ex) {
            System.out.println("Greska: " + ex);
        }
    }

    /**
     * Metoda koja implementira rad dretve te obrađuje zahtjeve i upravlja radom
     * sa servisima.
     */
    @Override
    public void run() {

        System.out.println("Dretva je pokrenuta.");

        try {
            is = s.getInputStream();
            os = s.getOutputStream();
            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            System.out.println("Primljena komanda: " + sb);

            String komanda_adminStart = "^USER ([^\\s]+); PASSWD ([^\\s]+); START;$";
            String komanda_adminStop = "^USER ([^\\s]+); PASSWD ([^\\s]+); STOP;$";
            String komanda_adminPause = "^USER ([^\\s]+); PASSWD ([^\\s]+); PAUSE;$";
            String komanda_adminStat = "^USER ([^\\s]+); PASSWD ([^\\s]+); STATUS;$";
            String komanda_adminWork = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master WORK;$";
            
            
            
            
            String komanda_IoT_Master_START = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master START;$";
            String komanda_IoT_Master_STOP = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master STOP;$";
            String komanda_IoT_Master_WAIT = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master WAIT;$";
            String komanda_IoT_Master_LOAD = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master LOAD;$";
            String komanda_IoT_Master_CLEAR = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master CLEAR;$";
            String komanda_IoT_Master_STATUS = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master STATUS;$";
            String komanda_IoT_Master_LIST = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master LIST;$";
            
            
            this.zahtjev = sb.toString();
            zapisiUDnevnik(true);

            if (ServerSocketDretva.primaj == false) {
                os.write("ERR 12".getBytes());
                os.flush();
                return;
            }

            //
            //
            //Upravljanje reg izrazima za NE iot master naredbe
            //
            //
            this.zahtjev = sb.toString();
            Pattern p = Pattern.compile(komanda_adminPause);
            Matcher m = p.matcher(sb);

            boolean status = m.matches();
            System.out.println("Opcija: " + status);
            if (status) {
                System.out.println("USERNAME: " + m.group(1));

                proslaNaredba = true;
                System.out.println("Opcija: komanda_adminPause " + status);
                boolean state = ServerPause();
                zapisiUDnevnik(state);
            }

            p = Pattern.compile(komanda_adminStart);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                System.out.println("USERNAME: " + m.group(1));
                if (!loginBaza(m.group(1), m.group(2))) {
                    os.write("ERR 10  ".getBytes());
                    os.flush();
                    return;
                }

                proslaNaredba = true;
                System.out.println("Opcija: komanda_adminStart" + status);

                //pozivanje metode za pokretanje admina
                adminStart();
                zapisiUDnevnik(true);
            }

            p = Pattern.compile(komanda_IoT_Master_LIST);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                System.out.println("USERNAME: " + m.group(1));
                if (!loginBaza(m.group(1), m.group(2))) {
                    os.write("ERR 10  ".getBytes());
                    os.flush();
                    return;
                }

                proslaNaredba = true;
                System.out.println("Opcija: komanda_adminStop" + status);

                //pozivanje metode za iot master list
                IoT_master_LIST();
                zapisiUDnevnik(true);
            }

            p = Pattern.compile(komanda_adminStop);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                System.out.println("USERNAME: " + m.group(1));
                if (!loginBaza(m.group(1), m.group(2))) {
                    os.write("ERR 10  ".getBytes());
                    os.flush();
                    return;
                }
                proslaNaredba = true;
                System.out.println("Opcija: komanda_adminStop" + status);

                //pozivanje metode za zaustavljanje admina
                adminStop();
                zapisiUDnevnik(true);
            }

            p = Pattern.compile(komanda_IoT_Master_START);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                System.out.println("USERNAME: " + m.group(1));
                if (!loginBaza(m.group(1), m.group(2))) {
                    os.write("ERR 10  ".getBytes());
                    os.flush();
                    return;
                }
                proslaNaredba = true;
                System.out.println("Opcija: IOT_MASTER" + status);
                zapisiUDnevnik(true);
            }

            p = Pattern.compile(komanda_adminStat);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                proslaNaredba = true;

                //pozivanje metode za status admina
                adminStatus();
                zapisiUDnevnik(true);
            }

            //
            //
            //Upravljanje reg izrazima za iot master
            //
            //
            p = Pattern.compile(komanda_adminWork);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                proslaNaredba = true;
                System.out.println("Opcijapcija: komanda_IOT WORK" + status);
                //pozivanje metode za iot master
                IoT_master_WORK();
            }

            p = Pattern.compile(komanda_IoT_Master_WAIT);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                //pozivanje metode za iot master
                IoT_master_WAIT();
                proslaNaredba = true;
                System.out.println("Opcija: komanda_IOT WAIT" + status);
            }

            p = Pattern.compile(komanda_IoT_Master_START);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                //pozivanje metode za iot master
                IoT_master_START();
                proslaNaredba = true;
                System.out.println("Opcija: komanda_IOT START" + status);
            }

            p = Pattern.compile(komanda_IoT_Master_STOP);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                //pozivanje metode za iot master
                IoT_master_STOP();
                proslaNaredba = true;
                System.out.println("Opcija: komanda_IOT STOP" + status);
            }

            p = Pattern.compile(komanda_IoT_Master_LOAD);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                //pozivanje metode za iot master
                IoT_master_LOAD();
                proslaNaredba = true;
                System.out.println("Opcija: komanda_IoT_Master_LOAD" + status);
            }

            p = Pattern.compile(komanda_IoT_Master_CLEAR);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                //pozivanje metode za iot master
                IoT_master_clear();
                proslaNaredba = true;
                System.out.println("Opcija: komanda_IoT_Master_CLEAR " + status);
            }

            p = Pattern.compile(komanda_IoT_Master_STATUS);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                //pozivanje metode za iot master
                IoT_master_STATUS();
                proslaNaredba = true;
                System.out.println("Opcija: komanda_IoT_Master_CLEAR " + status);
            }
        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }

        if (proslaNaredba) {
            Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
            java.util.Properties properties = System.getProperties();
            System.out.println("SERVER MAIL: " + konf.dajPostavku("mail.server"));
            properties.put("mail.smtp.host", konf.dajPostavku("mail.server"));
            Session session = Session.getInstance(properties, null);

            try {
                System.out.println("Email poruka poslana.");
                MimeMessage message = new MimeMessage(session);
                message.setFrom();
                System.out.println("Mail adresa je : " + konf.dajPostavku("mail.usernameThread"));
                message.setRecipients(Message.RecipientType.TO, konf.dajPostavku("mail.usernameThread"));
                message.setSubject(konf.dajPostavku("mail.subject"));
                message.setText(this.zahtjev);
                DateFormat dateFormatt = new SimpleDateFormat("yyyy/MM/dd");
                Date datet = new Date();
                message.setSentDate(datet);
                Transport.send(message);
            } catch (AddressException ex) {
                System.out.println("Greska: " + ex);
            } catch (MessagingException ex) {
                System.out.println("Greska: " + ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Pauzira radnu dretvu koja povlaci meteo podatke.
     *
     * @return
     */
    private boolean ServerPause() {

        try {
            if (RadnaDretva.dretvaIzvodenje == false) {
                os.write("Error 10;".getBytes());
                os.flush();
                return false;

            } else {
                RadnaDretva.dretvaIzvodenje = false;
                os.write("OK".getBytes());
                os.flush();
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    /**
     * Pokrece radnu dretvu koja povlaci meteo podatke.
     *
     * @return
     */
    private boolean adminStart() {
        try {
            if (RadnaDretva.dretvaIzvodenje == true) {
                os.write("Error 11;".getBytes());
                os.flush();
                return false;

            } else {
                RadnaDretva.dretvaIzvodenje = true;
                os.write("OK 10;".getBytes());
                os.flush();
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * Zaustavlja server dretvu koja pokrece radnu dretvu.
     */
    private boolean adminStop() {
        try {

            ServerSocketDretva.primaj = false;
            os.write("OK".getBytes());
            os.flush();
            return false;

        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private boolean adminStatus() {
        try {
            if (RadnaDretva.dretvaIzvodenje == false) {
                os.write("OK 15;".getBytes());
                os.flush();

            } else {
                os.write("OK 14;".getBytes());
                os.flush();
            }

            return true;
        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * Unos zapisa u dnevnik.
     *
     * @param state
     */
    public void zapisiUDnevnik(boolean state) {
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String strDate = sdfDate.format(now);
            String query = "insert into zahtjevi values(default,'ivaanic2','" + this.zahtjev + "','" + strDate + "'," + state + ")";
            Statement s = Database.c.createStatement();
            s.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Metoda zapisuje uredaj u tablicu uredaji.
     *
     * @param naziv
     * @param adresa
     */
    public void zapisiUredaj(String naziv, String adresa) {

        GMKlijent novi = new GMKlijent();
        Lokacija loc = novi.getGeoLocation(adresa);
        int id = 0;

        try {

            String query = "Select id, max(id) from uredaji group by id desc limit 1";
            Statement s = Database.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                id = rs.getInt("id");
            }

            query = "Select * from uredaji where naziv='" + naziv + "'";

            s = Database.c.createStatement();
            rs = s.executeQuery(query);
            while (rs.next()) {
                os.write("Error 30".getBytes());
                os.flush();
                return;
            }
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String strDate = sdfDate.format(now);
            id++;
            query = "insert into uredaji values(" + id + ",'" + naziv + "','" + loc.getLatitude() + "','" + loc.getLongitude() + "',1, vrijeme_promjene='" + strDate + "',vrijeme_kreiranja='" + strDate + "')";
            s = Database.c.createStatement();
            s.executeUpdate(query);
            os.write("OK".getBytes());
            os.flush();
        } catch (SQLException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Metoda sadrzi upite za aktivaciju uredaja, tj mijenjanje statusa.
     *
     * @param id
     */
    public void aktivacijaUredaja(String id) {

        try {

            String query = "Select * from uredaji where id=" + id + "";

            Statement s = Database.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt("status") == 1) {
                    os.write("Error 31".getBytes());
                    os.flush();
                } else {
                    query = "update uredaji set status=1 where id=" + id;
                    s = Database.c.createStatement();
                    s.executeUpdate(query);
                    os.write("OK 10".getBytes());
                    os.flush();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Login na bazu pomocu kor imena i lozinke.
     *
     * @param username
     * @param pass
     * @return
     */
    public boolean loginBaza(String username, String pass) {
        try {

            String query = "Select * from korisnici where username='" + username + "' and password ='" + pass + "';";

            Statement s = Database.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                return true;

            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * Sadrzi upite za manipuliranje statusom uredaja.
     *
     * @param id
     */
    public void deaktivacijaUredaja(String id) {

        try {

            String query = "Select * from uredaji where id=" + id;

            Statement s = Database.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt("status") == 0) {
                    os.write("Error 32".getBytes());
                    os.flush();
                } else {
                    query = "update uredaji set status=0 where id=" + id;
                    s = Database.c.createStatement();
                    s.executeUpdate(query);
                    os.write("OK".getBytes());
                    os.flush();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Sadrzi upite za manipuliranje statusom uredaja.
     *
     * @param id
     */
    public void statusUredaja(String id) {

        try {

            String query = "Select * from uredaji where id=" + id;

            Statement s = Database.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt("status") == 0) {
                    os.write("OK 34".getBytes());
                    os.flush();
                } else {
                    query = "update uredaji set status=0 where id=" + id;
                    s = Database.c.createStatement();
                    s.executeUpdate(query);
                    os.write("OK 35".getBytes());
                    os.flush();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Sadrzi upite za brisanje iz tablica uredaji i meteo.
     *
     * @param id
     */
    public void brisanjeUredaja(String id) {

        try {

            String query = "Select * from uredaji where id=" + id;

            Statement s = Database.c.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {

                query = "delete from meteo  where id=" + id;
                s = Database.c.createStatement();
                s.executeUpdate(query);
                query = "delete from uredaji  where id=" + id;
                s = Database.c.createStatement();
                s.executeUpdate(query);
                os.write("OK 10".getBytes());
                os.flush();
                return;

            }
            os.write("Error 33".getBytes());
            os.flush();
            return;
        } catch (SQLException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * – registrira grupu. Vraća OK 10; ako nije bila registrirana, odnosno ERR
     * 20; ako je bila registrirana. Takoder pokrece grupu
     */
    public void IoT_master_START() {
        boolean status = Iot_Master.registrirajGrupuIoT("ivaanic2", "AqLFX");
        System.out.println("START GRUPE: " + status);
        if (status) {
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 20".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * – učitava predefinirane IoT uređaje u grupu. Vraća OK 10;.
     */
    public void IoT_master_LOAD() {
        boolean status = Iot_Master.ucitajSveUredjajeGrupe("ivaanic2", "AqLFX");
        System.out.println("START GRUPE: " + status);

        try {
            os.write("OK 10".getBytes());
            os.flush();
        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Briše sve IoT uređaje iz grupu. Vraća OK 10;.
     */
    public void IoT_master_clear() {
        boolean status = Iot_Master.obrisiSveUredjajeGrupe("ivaanic2", "AqLFX");
        System.out.println("START GRUPE: " + status);

        try {
            os.write("OK 10".getBytes());
            os.flush();
        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    

    /**
     * Vraća id svih IoT uređaje iz grupi. Vraća OK 10; {IoT d{1-6} ″naziv″,
     * {IoT d{1-6} ″naziv″,...,{IoT d{1-6} ″naziv″}}};
     */
    public void IoT_master_LIST() {
        List<Uredjaj> lista = Iot_Master.dajSveUredjajeGrupe("ivaanic2", "AqLFX");
        try {
            String rezultat = "";
            System.out.println("BROJ UREDAJA: " + lista.size() + " ids. " + rezultat);
            for (Uredjaj uredaj : lista) {
                rezultat += "{" + uredaj.getId() + " " + uredaj.getNaziv() + "} <br/>";
            }
            System.out.println("BROJ UREDAJA: " + lista.size() + " ids. " + rezultat);
            os.write(("OK 10 <br/> " + rezultat).getBytes());
            os.flush();
        } catch (IOException ex) {
            Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Odjavljuje (deregistrira) grupu. Vraća OK 10; ako je bila registrirana,
     * odnosno ERR 21; ako nije bila registrirana.
     */
    public void IoT_master_STOP() {
        if (Iot_Master.deregistrirajGrupuIoT("ivaanic2", "AqLFX")) {
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 21".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * aktivira grupu. Vraća OK 10; ako nije bila aktivna, odnosno ERR 22; ako
     * je bila aktivna.
     */
    public void IoT_master_WORK() {
        if (Iot_Master.aktivirajGrupuIoT("ivaanic2", "AqLFX")) {
            System.out.println("Započet rad komandom iot master");
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 22".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    

    /**
     * Brise sve iot uredaje iz grupe.
     */
    public void IoT_master_CLEAR() {
        if (Iot_Master.obrisiSveUredjajeGrupe("ivaanic2", "AqLFX")) {
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 24".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    /**
     * Blokira grupu. Vraća OK 10; ako je bila aktivna, odnosno ERR 23; ako nije
     * bila aktivna.
     */
    public void IoT_master_WAIT() {
        if (Iot_Master.blokirajGrupuIoT("ivaanic2", "AqLFX")) {
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 23".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    /**
     * Vraća status grupe. Vraća OK dd; gdje dd znači: 24 – blokirana, 25 –
     * aktivna.
     */
    public void IoT_master_STATUS() {

        StatusKorisnika a = Iot_Master.dajStatusGrupeIoT("ivaanic2", "AqLFX");
        System.out.println(a.AKTIVAN);
        if (a.AKTIVAN != null) {
            try {
                os.write("OK 24".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 25".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaKomande.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
