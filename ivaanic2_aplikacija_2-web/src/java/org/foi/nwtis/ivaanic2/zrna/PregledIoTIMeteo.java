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
import org.foi.nwtis.ivaanic2.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.ivaanic2.rest.klijenti.OWMKlijent;
import org.foi.nwtis.ivaanic2.web.podaci.Korisnik;
import org.foi.nwtis.ivaanic2.web.podaci.Lokacija;
import org.foi.nwtis.ivaanic2.web.podaci.MeteoPodaci;
import org.foi.nwtis.ivaanic2.web.podaci.Uredjaj;
import org.foi.nwtis.ivaanic2.ws.klijenti.MeteoWSKlijent;

/**
 *
 * @author Ivan
 */
@Named(value = "pregledIoTIMeteo")
@SessionScoped
public class PregledIoTIMeteo implements Serializable {

    public static ServletContext sc;
    
    private String id = "";
    private String idVazeci = "";
    private String naziv = "";
    private String lat = "";
    private String lon = "";
    private String geoAdresa = "";
    private String n = "";
    private String idn = "";

    private List<Uredjaj> uredaji = new ArrayList<>();
    private List<MeteoPodaci> meteo = new ArrayList<>();
    private List<MeteoPodaci> vazeci = new ArrayList<>();

    private String idGeo = "";

    
    /**
     * Creates a new instance of PregledIoTIMeteo
     */
    public PregledIoTIMeteo() {
    }
    
    public List<MeteoPodaci> getMeteo() {
        return meteo;
    }

    public String getIdGeo() {
        return idGeo;
    }

    public void setIdGeo(String idGeo) {
        this.idGeo = idGeo;
    }

    public String getIdVazeci() {
        return idVazeci;
    }

    public void setIdVazeci(String idVazeci) {
        this.idVazeci = idVazeci;
    }

    public List<MeteoPodaci> getVazeci() {
        return vazeci;
    }

    public void setVazeci(List<MeteoPodaci> vazeci) {
        this.vazeci = vazeci;
    }

    public void setMeteo(List<MeteoPodaci> meteo) {
        this.meteo = meteo;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getIdn() {
        return idn;
    }

    public void setIdn(String idn) {
        this.idn = idn;
    }

    public String getId() {
        return id;
    }

    public String getGeoAdresa() {
        return geoAdresa;
    }

    public void setGeoAdresa(String geoAdresa) {
        this.geoAdresa = geoAdresa;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    

    public List<Uredjaj> getUredaji() {
        dohvatiUredaje();
        return uredaji;
    }

    public void setUredaji(List<Uredjaj> uredaji) {
        this.uredaji = uredaji;
    }

    public static ServletContext getSc() {
        return sc;
    }

    public static void setSc(ServletContext sc) {
        PregledIoTIMeteo.sc = sc;
    }

    /**
     * Azurira uredaj preko servisa prema id uredaja
     */
    public void azurirajUredaj() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        System.out.println("Uredaj podaci : " + this.id + "  " + this.lat + "  " + this.lon + "   " + this.naziv);
        job.add("id", this.id);
        job.add("naziv", this.naziv);
        job.add("lat", this.lat);
        job.add("lon", this.lon);
        System.out.println("Json: " + MeteoWSKlijent.updateUredajREST(job.build()));
    }

    public void geokoder() {
        geoAdresa = MeteoWSKlijent.geocoder(this.idGeo);
    }

    /**
     * Dodaje uredaj pomocu servisa.
     */
    public void dodajUredaj() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        System.out.println("Uredaj podaci: " + this.id + "  " + this.lat + "  " + this.lon + "   " + this.naziv);
        job.add("naziv", this.naziv);
        job.add("lat", this.lat);
        job.add("lon", this.lon);
        job.add("status", "0");
        System.out.println("Json: " + MeteoWSKlijent.dodajUredajREST(job.build()));
    }

    /**
     * Dohvaca sve uredaje pomocu servisa.
     */
    public void dohvatiUredaje() {
        uredaji.clear();
        String json = MeteoWSKlijent.dohvatiSveUredajeREST(sc);
        System.out.println(json);
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();
        System.out.println("Velicina: " + array.size());
        
        
        for (JsonValue a : array) {
            JsonReader reader = Json.createReader(new StringReader(a.toString()));
            JsonObject jo = reader.readObject();
            int id = (jo.getInt("uid"));
            String naziv = jo.getString("naziv");
            String lat = jo.getString("lat");
            String lon = jo.getString("lon");
            Lokacija nova = new Lokacija(lat, lon);
            Uredjaj novi = new Uredjaj(id, naziv, nova);
            uredaji.add(novi);
        }
    }

    /**
     * Dohvaca n broj meteo podataka ovisno o id samog uredaja
     */
    public void dohvatiNmeteoPodataka() {

        String json = MeteoWSKlijent.zadnjihNMeteo(n, idn);

        System.out.println(json);
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();
        meteo.clear();
        System.out.println("Velicina" + array.size());
        
        
        for (JsonValue a : array) {
            JsonReader reader = Json.createReader(new StringReader(a.toString()));
            JsonObject jo = reader.readObject();
            MeteoPodaci novi = new MeteoPodaci();
            novi.setHumidityValue(Float.parseFloat(jo.getString("vlaga")));
            novi.setTemperatureValue(Float.parseFloat(jo.getString("temp")));
            novi.setPressureValue(Float.parseFloat(jo.getString("tlak")));
            novi.setWeatherIcon((jo.getString("vrijeme")));
            meteo.add(novi);
            
            
            System.out.println("Meteo: " + novi.getTemperatureValue() + "  " + novi.getHumidityValue());
        }

    }

    /**
     * Dohvaca vazece meteo podatke za iot uredaj
     */
    public void dohvatiVazece() {
        String json = MeteoWSKlijent.vazeciMeteo(idVazeci);
        System.out.println(json);
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();
        vazeci.clear();

        System.out.println("Velicina " + array.size());
        
        
        for (JsonValue a : array) {
            JsonReader reader = Json.createReader(new StringReader(a.toString()));
            JsonObject jo = reader.readObject();
            MeteoPodaci novi = new MeteoPodaci();
            novi.setHumidityValue(Float.parseFloat(jo.getString("vlaga")));
            novi.setTemperatureValue(Float.parseFloat(jo.getString("temp")));
            novi.setPressureValue(Float.parseFloat(jo.getString("tlak")));
            novi.setWeatherIcon((jo.getString("vrijeme")));
            vazeci.add(novi);
            
            
            System.out.println("Meteo: " + novi.getTemperatureValue() + "  " + novi.getHumidityValue());
        }
    }

}
