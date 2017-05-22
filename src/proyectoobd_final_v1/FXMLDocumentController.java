/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoobd_final_v1;

import Graficos.MedidorRPM;
import Logica.HiloBusquedaDisp;
import Logica.HiloBusquedaServ;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.HeadersOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdRawCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;
import eu.hansolo.medusa.Gauge;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author practicas
 */
public class FXMLDocumentController implements Initializable {

    private MedidorRPM gauge;
    private ArrayList dispositivos;

    private String nombreDisp;

    private String url_disp;

    private ObservableList<String> itemsListaDisp = FXCollections.observableArrayList();
    private ObservableList<String> itemsListaServ = FXCollections.observableArrayList();

    @FXML
    Button salir;
    @FXML
    Button serv;
    @FXML
    Button conn;
    @FXML
    Button conectar_obd;

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

    //Método que establece la conexión entre el dispositivo OBD y el programa
    @FXML
    public void conectarOBD() {
        try {
            System.out.println(url_disp);
            
            StreamConnection streamConnection = (StreamConnection) Connector.open(url_disp);

            OutputStream outStream = streamConnection.openOutputStream();
            InputStream inStream = streamConnection.openInputStream();
            new ObdRawCommand("AT D").run(inStream, outStream);
            new ObdRawCommand("AT Z").run(inStream, outStream);
            
            new EchoOffCommand().run(inStream, outStream);
            
            new LineFeedOffCommand().run(inStream, outStream);
            
            new ObdRawCommand("AT S0").run(inStream, outStream);
            
            new HeadersOffCommand().run(inStream, outStream);
            
            new SelectProtocolCommand(ObdProtocols.AUTO).run(inStream, outStream);

            RPMCommand rpm = new RPMCommand();
            rpm.run(inStream, outStream);
            System.out.println(rpm.getRPM());
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //** Es posible que sea irrelevante mostrar o incluso implementar este método, ya que la conexión siempre se **
    //** establecerá con un dispositivo OBD, que siempre tendrá los mismos servicios disponibles **
    
    //Método que lista los servicios que soporta el dispositivo conectado(OBD)
    @FXML
    public void listaServ() {
        url_disp = new String();

        int i = listaDisp.getSelectionModel().getSelectedIndex();

        RemoteDevice rd = (RemoteDevice) dispositivos.get(i);

        nombreDisp = rd.getBluetoothAddress();

        HiloBusquedaServ hilo = new HiloBusquedaServ(nombreDisp, url_disp);

        hilo.start();

        Platform.runLater(() -> {
            barra_serv.setProgress(-1);
        });

        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    synchronized (url_disp) {
                        url_disp.wait();

                        Platform.runLater(() -> {
                            barra_serv.setProgress(0.0);

                            itemsListaServ.add(url_disp);
                            listaServ.setItems(itemsListaServ);
                        });

                        url_disp = hilo.getServicio();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    //Método que lista los dispositivos bluetooth en rango
    @FXML
    public void listaDisp() {
        HiloBusquedaDisp hilo = new HiloBusquedaDisp(dispositivos);

        hilo.start();

        Platform.runLater(() -> {
            barra_disp.setProgress(-1);
            serv.setDisable(true);

            itemsListaDisp = hilo.getLista();

            listaDisp.setItems(itemsListaDisp);
        });

        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    synchronized (dispositivos) {

                        dispositivos.wait();

                        Platform.runLater(() -> {
                            barra_disp.setProgress(0.0);
                            serv.setDisable(false);
                        });

                        dispositivos = hilo.getDispositivos();

                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, "").start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        dispositivos = new ArrayList();

        listaDisp.setItems(itemsListaDisp);

        //Se prepara el panel que contiene los relojes y gráficas
        fondoRPM.setBackground(new Background(new BackgroundFill(Gauge.DARK_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        gauge = new MedidorRPM();

        panelRPM.getChildren().add(gauge);
    }

}
