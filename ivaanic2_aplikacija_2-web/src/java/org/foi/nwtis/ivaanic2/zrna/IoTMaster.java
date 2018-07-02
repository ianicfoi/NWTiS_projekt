/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.zrna;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.net.Socket;
import org.foi.nwtis.ivaanic2.sesije.SessionUtils;

/**
 *
 * @author Ivan
 */
@Named(value = "iot_master")
@SessionScoped
public class IoTMaster implements Serializable {

    /**
     * Creates a new instance of iot_master
     */
    public IoTMaster() {
    }
        private String responseMaster = "";
    
    
 
    public String getResponseMaster() {
        return responseMaster;
    }

    public void setResponseMaster(String responseMaster) {
        this.responseMaster = responseMaster;
    }

    
     /**
           * load master komanda
           */
             public void loadMaster(){
    
        Socket socket = null; 
          try {
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master LOAD;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Odgovor: " + sb);
            responseMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
    
    
    
  /**
   * clear zahtjev, tj brisanje svih uredaja
   */
       public void clearMaster(){
    
        Socket socket = null; 
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master CLEAR;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Odgovor: " + sb);
            responseMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
       
        /**
         * list zahtjev za mastera
         */
          public void listMaster(){
    
        Socket socket = null; 
             try {
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master LIST;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("odgovor: " + sb);
            responseMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
         
 
    
}
