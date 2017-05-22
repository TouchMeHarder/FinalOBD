/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author practicas
 */
public class HiloBusquedaServ extends Thread {

    private ObservableList<String> items = FXCollections.observableArrayList();
    private Map<String, List<String>> servicios;
    private String nombre;
    private String servicio;
    private String url;

    public HiloBusquedaServ(String s, String aux) {
        nombre = s;
        url = aux;
    }

    @Override
    public synchronized void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                items.clear();
            }
        });

        BuscarSevicios bs = new BuscarSevicios();

        servicios = bs.getServicios();

        if (servicios.containsKey(nombre)) {
            List l = servicios.get(nombre);

            servicio = l.get(2).toString();
        } else {
            System.out.println("El dispositivo soporta el servicio requerido");
        }

        synchronized (url) {
            url.notify();
        }
    }

    public String getServicio() {
        return servicio;
    }
}
