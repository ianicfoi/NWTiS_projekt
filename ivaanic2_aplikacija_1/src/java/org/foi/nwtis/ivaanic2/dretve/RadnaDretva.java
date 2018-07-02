package org.foi.nwtis.ivaanic2.dretve;

import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Thread.yield;
import java.net.Socket;
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
import org.foi.nwtis.ivaanic2.rest.klijenti.OWMKlijent;
import org.foi.nwtis.ivaanic2.web.podaci.Lokacija;
import org.foi.nwtis.ivaanic2.web.podaci.MeteoPodaci;
import org.foi.nwtis.ivaanic2.web.podaci.Uredjaj;

/**
 * Dretva koja dohvaca meteoPrognoze za uredaje i sprema u bazu.
 *
 * @author Ivan
 */
public class RadnaDretva extends Thread {

    private boolean prekid_obrade = false;
    private ServletContext sc = null;

    public static boolean dretvaIzvodenje = true;
    public static boolean dretvaStop = false;

    public List<Uredjaj> uredjaji = new ArrayList<>();
    public List<Uredjaj> uredjajiPrognoza = new ArrayList<>();
    public List<MeteoPodaci> meteoPrognoze = new ArrayList<>();

    public static Socket socket;
    InputStream is = null;
    OutputStream os = null;

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        System.out.println("Radna dretva se izvodi.");

        int brojCiklusa = 1;
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        while (true) {

            if (ServerSocketDretva.primaj == false) {
                break;
            }

            while (!dretvaIzvodenje) {
                yield();
            }

            try {
                String query = "select * from uredaji";
                Statement s = Database.c.createStatement();
                ResultSet rs = s.executeQuery(query);
                while (rs.next()) {

                    Uredjaj novi = new Uredjaj();
                    novi.setId(rs.getInt("id"));
                    Lokacija novaLokacija = new Lokacija(rs.getString("latitude"), rs.getString("longitude"));
                    novi.setGeoloc(novaLokacija);
                    uredjaji.add(novi);
                }

            } catch (SQLException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (RadnaDretva.dretvaStop) {
                break;
            }

            Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
            int trajanjeCiklusa = Integer.parseInt(konf.dajPostavku("timeSecThread"));
            System.out.println("Trajanje ciklusa dretve" + trajanjeCiklusa);
            System.out.println("Ciklus dretve: " + brojCiklusa);
            brojCiklusa++;
            OWMKlijent noviPoziv = new OWMKlijent(konf.dajPostavku("api.key"));

            try {
                sleep(trajanjeCiklusa * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
            boolean postoji = false;

            for (Uredjaj uredaj : uredjaji) {
                for (Uredjaj uredajZaPrognozu : uredjajiPrognoza) {
                    if (uredaj.getGeoloc().getLatitude().equals(uredajZaPrognozu.getGeoloc().getLatitude())) {
                        if (uredaj.getGeoloc().getLongitude().equals(uredajZaPrognozu.getGeoloc().getLongitude())) {
                            //boolean varijabla osigurava da ne postoje kopije uredaja u bazi
                            postoji = true;
                        }
                    }
                }

                if (!postoji) {
                    uredjajiPrognoza.add(uredaj);
                }
                postoji = false;
            }

            for (Uredjaj uredajZaPrognozu : uredjajiPrognoza) {
//dohvacanje meteo meteoPrognoze ako se radi o neponavljajucim lokacijama
                MeteoPodaci mp = noviPoziv.getRealTimeWeather(uredajZaPrognozu.getGeoloc().getLatitude(), uredajZaPrognozu.getGeoloc().getLongitude());
                mp.setLatitude(uredajZaPrognozu.getGeoloc().getLatitude());
                mp.setLongitude(uredajZaPrognozu.getGeoloc().getLongitude());
                this.meteoPrognoze.add(mp);
            }

            for (Uredjaj uredaj : uredjaji) {
                for (MeteoPodaci mp : this.meteoPrognoze) {
                    if (uredaj.getGeoloc().getLatitude().equals(mp.getLatitude())) {
                        if (uredaj.getGeoloc().getLongitude().equals(mp.getLongitude())) {
                            String temp = mp.getTemperatureValue().toString();
                            String vlaga = mp.getHumidityValue().toString();
                            String tlak = mp.getPressureValue().toString();
                            String sunset = mp.getSunSet().toString();
                            float tempMin = mp.getTemperatureMin();
                            float tempMax = mp.getTemperatureMax();
                            float vjetar = mp.getWindSpeedValue();
                            float vjetarSmjer = mp.getWindSpeedValue();
                            String vrijemeOpisa = mp.getWeatherValue();
                            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date now = new Date();
                            String strDate = sdfDate.format(now);
                            String query = "insert into meteo values(default," + uredaj.getId() + ",'" + uredaj.getNaziv() + "'," + uredaj.getGeoloc().getLatitude() + "," + uredaj.getGeoloc().getLongitude() + ""
                                    + ",'" + mp.getWeatherIcon() + "','" + vrijemeOpisa + "'," + temp + "," + tempMin + "," + tempMax + "," + vlaga + "," + tlak + "," + vjetar + "," + vjetarSmjer + ",'" + strDate + "')";
                            Statement s;
                            try {
                                s = Database.c.createStatement();
                                s.executeUpdate(query);
                            } catch (SQLException ex) {
                                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
            this.meteoPrognoze.clear();
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public RadnaDretva(ServletContext sc) {
        this.sc = sc;
    }

}
