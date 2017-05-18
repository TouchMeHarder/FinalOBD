/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.bluetooth.RemoteDevice;

/**
 *
 * @author practicas
 */
public class HiloBusquedaDisp extends Thread {

    private ObservableList<String> items = FXCollections.observableArrayList();
    private ArrayList dispositivos;

    @Override
    public synchronized void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                items.clear();
            }
        });

        BuscarDispositivos buscar = new BuscarDispositivos();

        dispositivos = buscar.obtenerDispositivos();

        for (int i = 0; i < dispositivos.size(); i++) {
            try {
                RemoteDevice rd = (RemoteDevice) dispositivos.get(i);
                String nombre = rd.getFriendlyName(false);
                Thread.sleep(100);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        items.add(nombre);
                    }
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(HiloBusquedaDisp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HiloBusquedaDisp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println(dispositivos.isEmpty() + " esto es en el hilo de busqueda");
        System.out.println(dispositivos.get(0).toString() + " esto es el obejto [0]");
    }
    
    public ObservableList<String> getLista() {
        return items;
    }
    
    public ArrayList getDispositivos() {
        return dispositivos;
    }

}
