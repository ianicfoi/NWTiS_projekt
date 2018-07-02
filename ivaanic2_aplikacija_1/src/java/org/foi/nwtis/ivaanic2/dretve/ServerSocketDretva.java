/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ivaanic2.dretve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 * Server socket dretva koja prima zahtjeve preko server socketa pomocu accept.
 *
 * @author Ivan
 */
public class ServerSocketDretva extends Thread {

    private ServerSocket serverSocket;
    public static Socket s;
    public static ServletContext context;
    
    public static boolean primaj = true;

    
    
    
    @Override
    public void interrupt() {
        try {
            s.close();
        } catch (IOException ex) {
            System.err.println("Pogreska");
        }
    }

    
    @Override
    public void run() {

        System.out.println("Server socket dretva pokrenuta.");

        try {
            int port = (8000);
            serverSocket = new ServerSocket(port);

            while (true) {
                Socket so = serverSocket.accept();
                System.out.println("Server socket primio zahtjev.");
                DretvaKomande drKomande = new DretvaKomande(so, context);
                drKomande.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerSocketDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

}
