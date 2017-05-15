/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoobd_final_v1;

import Graficos.MedidorRPM;
import Logica.BuscarDispositivos;
import Logica.HiloBusquedaDisp;
import eu.hansolo.medusa.Gauge;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;

/**
 *
 * @author practicas
 */
public class FXMLDocumentController implements Initializable {

    MedidorRPM gauge;
    BuscarDispositivos bd;
    private ArrayList dispositivos;
    
    private ObservableList<String> itemsListaDisp = FXCollections.observableArrayList();
    private ObservableList<String> itemsListaServ = FXCollections.observableArrayList();
    
    @FXML
    Button salir;

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
        
    }

    @FXML
    public void listaDisp() {
        HiloBusquedaDisp hilo = new HiloBusquedaDisp();
        
        hilo.start();
        
        itemsListaDisp = hilo.getLista();
        
        listaDisp.setItems(itemsListaDisp);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        listaDisp.setItems(itemsListaDisp);

        fondoRPM.setBackground(new Background(new BackgroundFill(Gauge.DARK_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        gauge = new MedidorRPM();

        panelRPM.getChildren().add(gauge);
    }

}
