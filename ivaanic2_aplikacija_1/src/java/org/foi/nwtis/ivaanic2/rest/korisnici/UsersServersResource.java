/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.rest.korisnici;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.ivaanic2.dretve.Database;
import org.foi.nwtis.ivaanic2.rest.ws.MeteoRESTResourceContainer;

/**
 * Rest web servis za rad s korisnicima u bazi podataka
 *
 * @author Ivan
 */
@Path("/uss")
public class UsersServersResource {

    private String id = "";
    public static ServletContext sc;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of UsersServersResource
     */
    public UsersServersResource() {
    }

    /**
     * Metoda koja ima upit za dohvat svih korisnika.
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        long pocetak = System.currentTimeMillis();
        JsonArrayBuilder jab = Json.createArrayBuilder();

        try {
            String query = "select * from korisnici";
            Statement s = Database.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                JsonObjectBuilder job = Json.createObjectBuilder();
                job.add("id", rs.getInt("id"));
                job.add("username", rs.getString("username"));
                job.add("password", rs.getString("password"));
                job.add("email", rs.getString("email"));

                jab.add(job);
            }
        } catch (SQLException ex) {
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/uss");
        }
        long kraj = System.currentTimeMillis();
        zapisiUDnevnik((int) (kraj - pocetak), 1, "/uss");
        return jab.build().toString();
    }

    /**
     * Metoda koja dodaje korisnika.
     *
     * @param content representation for the new resource
     *
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Path("/dodaj")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String dodajKorisnika(String content) {

        long pocetak = System.currentTimeMillis();
        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();
        String email = jo.getString("email");
        String naziv = jo.getString("username");
        String password = jo.getString("password");
        String query = "select * from korisnici where username='" + naziv + "'";

        try {
            Statement s = Database.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            int id = 1;
            while (rs.next()) {

                long kraj = System.currentTimeMillis();
                zapisiUDnevnik((int) (kraj - pocetak), 0, "/uss/dodaj");
                return "0";
            }
            query = "insert into korisnici values(default,'" + naziv + "','" + password + "','" + email + "')";
            s = Database.c.createStatement();
            s.executeUpdate(query);

            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 1, "/uss/dodaj");
            return "1";
        } catch (SQLException ex) {

            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/uss/dodaj");
            return "0";
        }
    }

    /**
     * Metoda koja azurira korisnika.
     *
     * @param content
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/azuriraj")
    public String azurirajKorisnika(String content) {

        long pocetak = System.currentTimeMillis();
        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();
        String id = jo.getString("id");
        String email = jo.getString("email");
        String naziv = jo.getString("username");
        String password = jo.getString("password");
        String query = "select * from korisnici where id=" + id + "";

        try {
            Statement s = Database.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                query = "update korisnici set username='" + naziv + "',email='" + email + "',password='" + password + "' where id=" + id;
                s.executeUpdate(query);

                long kraj = System.currentTimeMillis();
                zapisiUDnevnik((int) (kraj - pocetak), 1, "/uss/azuriraj");
                return "1";
            }
            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/uss/azuriraj");
            return "0";
        } catch (SQLException ex) {
            return "0";
        }
    }

    /**
     * Preuzmi samo jednog korisnika.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{korisnickoIme}")
    public String getUsersServerResource(@PathParam("korisnickoIme") String korisnickoIme) {

        long pocetak = System.currentTimeMillis();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        try {

            String query = "select * from korisnici where username='" + korisnickoIme + "'";
            Statement s = Database.c.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {

                JsonObjectBuilder job = Json.createObjectBuilder();
                job.add("uid", rs.getInt("id"));
                job.add("username", rs.getString("username"));
                job.add("password", rs.getString("password"));
                job.add("email", rs.getString("email"));
                jab.add(job);
            }
        } catch (SQLException ex) {

            long kraj = System.currentTimeMillis();
            zapisiUDnevnik((int) (kraj - pocetak), 0, "/uss/{korisnicko_ime}");
            return "[]";
        }

        long kraj = System.currentTimeMillis();
        zapisiUDnevnik((int) (kraj - pocetak), 1, "/uss/{korisnicko_ime}");
        return jab.build().toString();
    }

    /**
     * Metoda za upis podataka u dnevnik.
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
