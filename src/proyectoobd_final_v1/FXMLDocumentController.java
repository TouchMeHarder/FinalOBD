/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoobd_final_v1;

import Graficos.MedidorRPM;
import Logica.DatosSim;
import Logica.HiloBusquedaDisp;
import Logica.HiloBusquedaServ;
import com.github.pires.obd.commands.control.PendingTroubleCodesCommand;
import com.github.pires.obd.commands.control.PermanentTroubleCodesCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.HeadersOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

    private ArrayList mockRPM;

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
    Button mockup;
    @FXML
    Button cargaMock;

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
    LineChart grafica;

    @FXML
    public void salir() {
        System.exit(0);
    }

    @FXML
    public void cargarMock() {
        try {
            DatosSim datos = new DatosSim();

            mockRPM = datos.getValoresRPM();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText("Información");
            alert.setContentText("Los datos se han cargado correctamente");

            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ha ocurrido un error");
            alert.setContentText("La carga de datos no se ha realizado");

            alert.showAndWait();
        }
    }

    //Check thoroughly
    @FXML
    public void mostrarMockupRPM() {
        XYChart.Series series = new XYChart.Series();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mockRPM.size(); i++) {
                    try {
                        Thread.sleep(200);
                        System.out.println(mockRPM.get(i));
                        gauge.getRpmGauge().setValue(Integer.parseInt((String) mockRPM.get(i)));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                synchronized (grafica) {
                    grafica.notify();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (grafica) {
                    try {
                        grafica.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                for (int i = 0; i < mockRPM.size(); i++) {
                    int aux = Integer.parseInt(mockRPM.get(i).toString());

                    series.getData().add(new XYChart.Data(String.valueOf(i), aux));
                }
            }
        }).start();

        grafica.getData().add(series);
    }

    //Método que establece la conexión entre el dispositivo OBD y el programa
    @FXML
    public void conectarOBD() {
        try {
            StreamConnection streamConnection = (StreamConnection) Connector.open(url_disp);

            OutputStream outStream = streamConnection.openOutputStream();
            InputStream inStream = streamConnection.openInputStream();

            new EchoOffCommand().run(inStream, outStream);

            new LineFeedOffCommand().run(inStream, outStream);

            new HeadersOffCommand().run(inStream, outStream);

            new SelectProtocolCommand(ObdProtocols.AUTO).run(inStream, outStream);

            RPMCommand rpm = new RPMCommand();
            rpm.run(inStream, outStream);
            gauge.getRpmGauge().setValue(rpm.getRPM());

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        gauge.getRpmGauge().setValue(rpm.getRPM());
                    }
                }
            });

            PermanentTroubleCodesCommand perma = new PermanentTroubleCodesCommand();
            perma.run(inStream, outStream);
            System.out.println("Estos son los codigos permanentes: " + perma.getFormattedResult());

            PendingTroubleCodesCommand pending = new PendingTroubleCodesCommand();
            pending.run(inStream, outStream);
            System.out.println("Estos son los codigos pendientes: " + pending.getFormattedResult());

            TroubleCodesCommand trouble = new TroubleCodesCommand();
            trouble.run(inStream, outStream);
            System.out.println("Estos son codigos genericos: " + trouble.getFormattedResult());
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
        }, "You can name the thread here!").start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dispositivos = new ArrayList();

        listaDisp.setItems(itemsListaDisp);

        //Se prepara el panel que contiene los relojes y gráficas
        fondoRPM.setBackground(new Background(new BackgroundFill(Gauge.DARK_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        gauge = new MedidorRPM();

        panelRPM.getChildren().add(gauge);
    }

}
