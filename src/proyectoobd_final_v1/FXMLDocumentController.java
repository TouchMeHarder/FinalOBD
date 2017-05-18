/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoobd_final_v1;

import Graficos.MedidorRPM;
import Logica.BuscarDispositivos;
import Logica.HiloBusquedaDisp;
import Logica.HiloBusquedaServ;
import eu.hansolo.medusa.Gauge;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javax.bluetooth.RemoteDevice;

/**
 *
 * @author practicas
 */
public class FXMLDocumentController implements Initializable {

    private MedidorRPM gauge;
    private ArrayList dispositivos;

    private String nombreDisp;

    private ObservableList<String> itemsListaDisp = FXCollections.observableArrayList();
    private ObservableList<String> itemsListaServ = FXCollections.observableArrayList();

    @FXML
    Button salir;
    @FXML
    Button serv;
    @FXML
    Button conn;
    
    @FXML
    ProgressBar barra_disp;
    @FXML
    ProgressBar barra_serv;

    @FXML
    StackPane panelRPM;

    @FXML
    AnchorPane fondoRPM;

    @FXML
    ListView listaDisp;
    @FXML
    ListView listaServ;

    @FXML
    public void salir() {
        System.exit(0);
    }

    @FXML
    public void listaServ() {

        int i = listaDisp.getSelectionModel().getSelectedIndex();

        RemoteDevice rd = (RemoteDevice) dispositivos.get(i);

        nombreDisp = rd.getBluetoothAddress();

        HiloBusquedaServ hilo = new HiloBusquedaServ(nombreDisp);

        hilo.start();

        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    this.wait(15000);
                    System.out.println("ya he acabado de esperar");

                    itemsListaServ.add(hilo.getServicio());
                    listaServ.setItems(itemsListaServ);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    @FXML
    public void listaDisp() {
        HiloBusquedaDisp hilo = new HiloBusquedaDisp();

        hilo.start();

        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                itemsListaDisp = hilo.getLista();

                listaDisp.setItems(itemsListaDisp);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    barra_disp.setProgress(.5);
                    
                    this.wait(15000);
                    System.out.println("este hilo se acaba de iniciar");

                    dispositivos = hilo.getDispositivos();
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        dispositivos = new ArrayList();

        listaDisp.setItems(itemsListaDisp);

        fondoRPM.setBackground(new Background(new BackgroundFill(Gauge.DARK_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        gauge = new MedidorRPM();

        panelRPM.getChildren().add(gauge);
    }

}
