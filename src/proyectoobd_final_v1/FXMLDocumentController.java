/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoobd_final_v1;

import Graficos.MedidorRPM;
import eu.hansolo.medusa.Gauge;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
    
    @FXML
    StackPane panelRPM;
    
    @FXML
    AnchorPane fondoRPM;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        fondoRPM.setBackground(new Background(new BackgroundFill(Gauge.DARK_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        
        gauge = new MedidorRPM();
        
        panelRPM.getChildren().add(gauge);
    }    
    
}
