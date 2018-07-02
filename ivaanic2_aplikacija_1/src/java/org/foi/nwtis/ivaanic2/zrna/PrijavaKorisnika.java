/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.zrna;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import org.foi.nwtis.ivaanic2.konfiguracije.bp.BP_Konfiguracija;

/**
 * Zrno za prijavu korisnika.
 *
 * @author Ivan
 */
public class PrijavaKorisnika {
     
    Connection c;
    public static ServletContext sc;
    
     private String user = "";
     private String pass = "";
     private String msg ="";
	
        
        
    /**
     * Creates a new instance of PrijavaKorisnika
     */
    public PrijavaKorisnika() {
        
    }
    
    public String prijava(){
       
    String query ="select * from korisnici where username='"+this.user+"' and password ='"+ this.pass + "'";
        
        return "Uspjesna prijava";   
    }
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pass;
	}

	public void setPwd(String pass) {
		this.pass = pass;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	
        /**
         * Dohvacanje kor imena i lozinke za spajanje na bazu.
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
        
        
    }
    
 
