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
 * Naredbe i za server koji nije iot master.
 *
 * @author Ivan
 */
@Named(value = "naredbeServer")
@SessionScoped
public class naredbeServer implements Serializable {

    /**
     * Creates a new instance of naredbeServer
     */
    private String response = "";
    private String statusServer = "";
    private String responseMaster = "";

    public naredbeServer() {

    }

    public String getResponseMaster() {
        return responseMaster;
    }

    public void setResponseMaster(String responseMaster) {
        this.responseMaster = responseMaster;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getStatusServer() {
        return statusServer;
    }

    public void setStatusServer(String statusServer) {
        this.statusServer = statusServer;
    }

    /**
     * Komanda start
     */
    public void start() {

        Socket socket = null;

        try {

            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER " + SessionUtils.getUserName() + "; PASSWD " + SessionUtils.getPassword() + "; START;";
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
            response = sb.toString();

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * pause komanda koja pauzira radnu dretvu
     */
    public void pause() {

        Socket socket = null;
        try {
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER " + SessionUtils.getUserName() + "; PASSWD " + SessionUtils.getPassword() + "; PAUSE;";
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
            response = sb.toString();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Komanda stop, stopira se radna dretva.
     */
    public void stop() {

        Socket socket = null;

        try {

            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER " + SessionUtils.getUserName() + "; PASSWD " + SessionUtils.getPassword() + "; STOP;";
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
            response = sb.toString();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Iot master start komanda - registr grupa
     */
    public void startMaster() {

        Socket socket = null;
        try {

            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER " + SessionUtils.getUserName() + "; PASSWD " + SessionUtils.getPassword() + "; IoT_Master START;";
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
            responseMaster = sb.toString();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Iot work komanda
     */
    public void workMaster() {

        Socket socket = null;
        try {

            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER " + SessionUtils.getUserName() + "; PASSWD " + SessionUtils.getPassword() + "; IoT_Master WORK;";
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
            responseMaster = sb.toString();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Iot master stop komanda
     */
    public void stopMaster() {

        Socket socket = null;

        try {

            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER " + SessionUtils.getUserName() + "; PASSWD " + SessionUtils.getPassword() + "; IoT_Master STOP;";
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
            responseMaster = sb.toString();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Iot master wait
     */
    public void waitMaster() {

        Socket socket = null;

        try {

            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER " + SessionUtils.getUserName() + "; PASSWD " + SessionUtils.getPassword() + "; IoT_Master WAIT;";
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
            responseMaster = sb.toString();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Iot master pause komanda
     */
    public void pauseMaster() {

        Socket socket = null;

        try {

            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER " + SessionUtils.getUserName() + "; PASSWD " + SessionUtils.getPassword() + "; IoT_Master PAUSE;";
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
            responseMaster = sb.toString();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Iot master status komanda
     */
    public void statusMaster() {

        Socket socket = null;

        try {

            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER " + SessionUtils.getUserName() + "; PASSWD " + SessionUtils.getPassword() + "; IoT_Master STATUS;";
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
            responseMaster = sb.toString();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Status komanda
     */
    public void status() {

        Socket socket = null;

        try {

            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            String zahtjev = "USER " + SessionUtils.getUserName() + "; PASSWD " + SessionUtils.getPassword() + "; STATUS;";
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
            response = sb.toString();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

}
