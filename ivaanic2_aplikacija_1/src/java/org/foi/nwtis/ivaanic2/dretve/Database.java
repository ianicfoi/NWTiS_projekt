package org.foi.nwtis.ivaanic2.dretve;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import org.foi.nwtis.ivaanic2.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa za slusaca aplikacije čija metoda se spaja na bazu podataka.
 *
 * @author Ivan
 */
public class Database {

    public static ServletContext sc;
    public static Connection c;

    public static void spojiNaBazu() {

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

        System.out.println("Konekcija je uspjela.");
    }

}
