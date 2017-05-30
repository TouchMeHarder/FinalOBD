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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
            try {
                List l = servicios.get(nombre);

                servicio = l.get(2).toString();
            } catch (Exception e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Ha ocurrido un error!");
                        alert.setContentText("El dispositivo seleccionado no tiene el servicio requerido disponible");

                        alert.showAndWait();
                    }
                });
            }
        }

        synchronized (url) {
            url.notify();
        }
    }

    public String getServicio() {
        return servicio;
    }
}
