/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoobd_final_v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

/**
 *
 * @author practicas
 */
public class ProyectoOBD_Final_v1 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        List<String> choices = new ArrayList<>();
        choices.add("Movil");
        choices.add("Pinganillo");
        choices.add("OBDII");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("OBD II");
        dialog.setHeaderText("Elija dispositivo para conectar");
        dialog.setContentText("Dispositivos disponibles:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {

            Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
