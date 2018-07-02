/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.ws;

import java.io.StringReader;
import java.net.URLEncoder;
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
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletContext;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.ivaanic2.konfiguracije.Konfiguracija;
import org.foi.nwtis.ivaanic2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.ivaanic2.rest.klijenti.GMRESTHelper;
import org.foi.nwtis.ivaanic2.rest.klijenti.OWMKlijent;
import org.foi.nwtis.ivaanic2.web.podaci.MeteoPodaci;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.foi.nwtis.ivaanic2.dretve.Database;
import org.foi.nwtis.ivaanic2.rest.ws.MeteoRESTResourceContainer;

/**
 * Klasa koja pruza soap servis.
 *
 * @author Ivan
 */
@WebService(serviceName = "GeoMeteoWS")
public class GeoMeteoWS {

    private ResultSet rs;
    private Statement s;
    public static ServletContext sc;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " , how are you";
    }

    /**
     * Preuzima zadnje odabrane meteo podatke za odabrani iot uredaj.
     * Zapisivanje u dnevnik.
     */
    @WebMethod(operationName = "zadnjiPreuzetiMeteo")
    public String zadnjiPreuzetiMeteo(@WebParam(name = "id") int id) {
        long pocetak = System.currentTimeMillis();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder job = Json.createObjectBuilder();
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
            long kraj = System.currentTimeMillis();

            zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
        }

        try {
            Database.c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                    bp_konf.getUserUsername(),
                    bp_konf.getUserPassword());
        } catch (SQLException ex) {
            System.out.println(ex);
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
        }

        String query = "select * from meteo where id=" + id + " order by preuzeto desc limit 1;";

        try {
            s = Database.c.createStatement();
            rs = s.executeQuery(query);

            while (rs.next()) {

                job.add("vlaga", rs.getString("vlaga"));
                job.add("temp", rs.getString("temp"));
                job.add("tlak", rs.getString("tlak"));
                job.add("vjetar", rs.getString("vjetar"));
                job.add("vrijeme", rs.getString("vrijemeOpis"));
                jab.add(job);
            }

        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
        }

        long kraj = System.currentTimeMillis();
        zapisiUDnevnik((int) (kraj - pocetak), 1, "/GetoMeteoWS");
        return jab.build().toString();
    }

    /**
     * Dohvaca posljednjih n meteoroloskih podataa za odabrani iot uredaj.
     */
    @WebMethod(operationName = "zadnjihNMeteo")
    public String operation(@WebParam(name = "n") String n, @WebParam(name = "id") String id) {
        long pocetak = System.currentTimeMillis();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder job = Json.createObjectBuilder();
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }

        try {
            Database.c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                    bp_konf.getUserUsername(),
                    bp_konf.getUserPassword());
        } catch (SQLException ex) {
            System.out.println(ex);
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
        }

        String query = "select * from meteo where id=" + id + "  order by preuzeto desc limit " + n;
        
        try {
            s = Database.c.createStatement();
            rs = s.executeQuery(query);
            while (rs.next()) {

                job.add("vlaga", rs.getString("vlaga"));
                job.add("temp", rs.getString("temp"));
                job.add("tlak", rs.getString("tlak"));
                job.add("vjetar", rs.getString("vjetar"));
                job.add("vrijeme", rs.getString("vrijemeOpis"));
                jab.add(job);

            }

        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
        }
        long kraj = System.currentTimeMillis();
        zapisiUDnevnik((int) (kraj - pocetak), 1, "/GetoMeteoWS");
        return jab.build().toString();
    }

    /**
     * Preuzima vazece meteo podatke za odabrani iot uredaj (openweathermap).
     */
    @WebMethod(operationName = "vazeciMeteo")
    public String vazeciMeteo(@WebParam(name = "id") String id) {
        long pocetak = System.currentTimeMillis();
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
        OWMKlijent novi = new OWMKlijent((konf.dajPostavku("api.key")));
        JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder job = Json.createObjectBuilder();

        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
        }

        try {
            Database.c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                    bp_konf.getUserUsername(),
                    bp_konf.getUserPassword());
        } catch (SQLException ex) {
            System.out.println(ex);
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
        }

        String query = "select * from uredaji";

        try {
            s = Database.c.createStatement();
            rs = s.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt("id") == Integer.parseInt(id)) {
                    MeteoPodaci mp = novi.getRealTimeWeather(rs.getString("latitude"), rs.getString("longitude"));
                    job.add("vlaga", mp.getHumidityValue().toString());
                    job.add("temp", mp.getTemperatureValue().toString());
                    job.add("tlak", mp.getPressureValue().toString());
                    job.add("vjetar", mp.getWindSpeedValue().toString());
                    job.add("vrijeme", mp.getWeatherValue().toString());
                    jab.add(job);
                    return jab.build().toString();
                }
            }
        } catch (SQLException ex) {
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        long kraj = System.currentTimeMillis();
        zapisiUDnevnik((int) (kraj - pocetak), 1, "/GetoMeteoWS");
        return null;
    }

    /**
     * Dohvati sve podatke za odredeni uredaj od datuma do datuma.
     */
    @WebMethod(operationName = "dajSveMeteoPodatkeZaUredjaj")
    public List<MeteoPodaci> dajSveMeteoPodatkeZaUredjaj(@WebParam(name = "id") int id, @WebParam(name = "from") long from, @WebParam(name = "to") long to) {
        long pocetak = System.currentTimeMillis();
        List<MeteoPodaci> mp = new ArrayList<>();
        try {

            Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
            OWMKlijent novi = new OWMKlijent((konf.dajPostavku("api.key")));
            JsonArrayBuilder jab = Json.createArrayBuilder();
            JsonObjectBuilder job = Json.createObjectBuilder();

            BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
            try {
                Class.forName(bp_konf.getDriverDatabase());
            } catch (ClassNotFoundException ex) {
                long kraj = System.currentTimeMillis();
                zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
                System.out.println(ex);
            }

            try {
                Database.c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                        bp_konf.getUserUsername(),
                        bp_konf.getUserPassword());
            } catch (SQLException ex) {
                System.out.println(ex);
                long kraj = System.currentTimeMillis();
                zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
            }

            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fromDate = new Date(from);
            String strDate = sdfDate.format(fromDate);
            Date toDate = new Date(to);
            String strToDate = sdfDate.format(toDate);
            System.out.println("DATUMI :" + strDate + "  " + strToDate);
            String query = "select * from meteo where id=" + id + " and preuzeto >'" + strDate + "' and preuzeto <'" + strToDate + "'";
            s = Database.c.createStatement();
            rs = s.executeQuery(query);
            while (rs.next()) {
                System.out.println(rs.getInt("id"));
                mp.add(new MeteoPodaci(new Date(), new Date(), 19.1f, 5.2f, 25.5f, "C", 55.8f, "%", 998.8f, "hPa", 0.0f, "", 0.0f, "", "", 1, "", "ok", 0.0f, "", "", 7, "", "", new Date()));

            }
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 1, "/GetoMeteoWS");
            return mp;
        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        mp.add(new MeteoPodaci(new Date(), new Date(), 19.1f, 5.2f, 25.5f, "C", 55.8f, "%", 998.8f, "hPa", 0.0f, "", 0.0f, "", "", 1, "", "ok", 0.0f, "", "", 7, "", "", new Date()));
        long kraj = System.currentTimeMillis();
        zapisiUDnevnik((int) (kraj - pocetak), 1, "/GetoMeteoWS");
        return mp;
    }

    /**
     * Dohvati adresu iot uredaja na bazi njegove geolokacije.
     */
    @WebMethod(operationName = "Geocoder")
    public String Geocoder(@WebParam(name = "id") String id) {
        long pocetak = System.currentTimeMillis();

        Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
        OWMKlijent novi = new OWMKlijent((konf.dajPostavku("api.key")));
        JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder job = Json.createObjectBuilder();

        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
        }

        try {
            Database.c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                    bp_konf.getUserUsername(),
                    bp_konf.getUserPassword());
        } catch (SQLException ex) {
            System.out.println(ex);
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
        }

        String query = "select * from uredaji";
        try {
            s = Database.c.createStatement();
            rs = s.executeQuery(query);
            System.err.println("Id: " + id);
            while (rs.next()) {
                if (rs.getInt("id") == Integer.parseInt(id)) {

                    Client client = ClientBuilder.newClient();
                    WebTarget webResource = client.target(GMRESTHelper.getGM_BASE_URI())
                            .path("maps/api/geocode/json");
                    webResource = webResource.queryParam("latlng", URLEncoder.encode(rs.getString("latitude") + "," + rs.getString("longitude")));
                    webResource = webResource.queryParam("sensor", "false");
                    String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
                    JsonReader reader = Json.createReader(new StringReader(odgovor));
                    JsonObject jo = reader.readObject();

                    JsonObject obj = jo.getJsonArray("results").getJsonObject(0).getJsonArray("address_components").getJsonObject(2);
                    return obj.getString("long_name");
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/GetoMeteoWS");
        }
        long kraj = System.currentTimeMillis();
        zapisiUDnevnik((int) (kraj - pocetak), 1, "/GetoMeteoWS");
        return "";
    }

    /**
     * Zapisuje u tablicu dnevnik podatke kao sto su trajanje, status, url.
     *
     * @param trajanje
     * @param status
     * @param url
     */
    public void zapisiUDnevnik(int trajanje, int status, String url) {
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String strDate = sdfDate.format(now);
            String query = "insert into dnevnik values(default,'ivaanic2','" + url + "','localhost','" + strDate + "', " + trajanje + ", " + status + ")";
            Statement s = Database.c.createStatement();
            s.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(MeteoRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
