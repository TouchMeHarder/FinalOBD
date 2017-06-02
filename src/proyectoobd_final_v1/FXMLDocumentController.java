/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectoobd_final_v1;

import Graficos.MedidorRPM;
import Logica.DataSource;
import Logica.DatosSim;
import Logica.HiloBusquedaDisp;
import Logica.HiloBusquedaServ;
import Logica.Valores;
import com.github.pires.obd.commands.control.DistanceMILOnCommand;
import com.github.pires.obd.commands.control.PendingTroubleCodesCommand;
import com.github.pires.obd.commands.control.PermanentTroubleCodesCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.HeadersOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import eu.hansolo.medusa.Gauge;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author practicas
 */
public class FXMLDocumentController implements Initializable {

    private long lastTimerCall;
    private AnimationTimer timer;

    private Valores valores;

    public final XYChart.Series series = new XYChart.Series();

    private StreamConnection streamConnection;
    private OutputStream outStream;
    private InputStream inStream;

    private PermanentTroubleCodesCommand perma;
    private PendingTroubleCodesCommand pending;
    private DistanceMILOnCommand mil;

    private AirIntakeTemperatureCommand intake;
    private AmbientAirTemperatureCommand ambient;
    private EngineCoolantTemperatureCommand coolant;

    private RPMCommand rpm;

    private MedidorRPM gauge;
    private ArrayList dispositivos;

    private ArrayList mockRPM;
    private ArrayList RPMReales;

    private String nombreDisp;

    private String url_disp;

    private final String LOGS = "log.txt";
    private FileWriter fWriter;
    private BufferedWriter out;
    private PrintWriter pWriter;

    private ObservableList<String> itemsListaDisp = FXCollections.observableArrayList();
    private ObservableList<String> itemsListaServ = FXCollections.observableArrayList();
    private ObservableList<String> itemsListaPerm = FXCollections.observableArrayList();
    private ObservableList<String> itemsListaPend = FXCollections.observableArrayList();

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
    ListView listaPerm;
    @FXML
    ListView listaPend;

    @FXML
    TextField distancia;
    @FXML
    TextField refrig;
    @FXML
    TextField interior;
    @FXML
    TextField exterior;

    @FXML
    LineChart grafica;

    @FXML
    public void salir() {
        try {
            pWriter.close();
            out.close();
            fWriter.close();

            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Método que obtiene los valores del fichero, que se guardan en una lista para ser representados más tarde
    @FXML
    public void cargarMock() {
        try {
            DatosSim datos = new DatosSim();

            mockRPM = datos.getValoresRPM();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText("Información");
            alert.setContentText("Los datos se han cargado correctamente.");

            alert.show();
        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ha ocurrido un error");
            alert.setContentText("La carga de datos no se ha realizado.");

            alert.showAndWait();
        }
    }

    //Método que realiza los cambios en las gráficas (reloj revoluciones y gráfica de líneas)
    @FXML
    public void mostrarMockupRPM() {
        /*Se vacían tanto la serie de valores como la gráfica, para que se pueda realizar el test varias veces en
        la misma ejecución y los resultados no se intercalen*/
        series.getData().clear();
        grafica.getData().clear();

        try {
            /*Se lanza un hilo que modifica los valores del reloj de revoluciones basándose en los valores
            de la lista obtenida en la funcion cargarMock()*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < mockRPM.size(); i++) {
                            try {
                                Thread.sleep(100);
                                System.out.println(mockRPM.get(i));
                                gauge.getRpmGauge().setValue(Integer.parseInt((String) mockRPM.get(i)));
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();

                                Alert alert = new Alert(AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Ha ocurrido un error!");
                                alert.setContentText("La ejecución ha sido interrumpida inesperadamente.");

                                alert.showAndWait();
                            }
                        }

                        synchronized (grafica) {
                            grafica.notify();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Alert alert = new Alert(AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Ha ocurrido un error!");
                                alert.setContentText("La lista de datos esta vacia.");

                                alert.showAndWait();
                            }
                        });
                    }
                }
            }).start();

            /*Segundo hilo que se encarga de establecer los valores de la serie y añadirla a la gráfica*/
            new Thread(new Runnable() {

                @Override
                public void run() {
                    synchronized (grafica) {
                        try {
                            grafica.wait();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();

                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Ha ocurrido un error!");
                            alert.setContentText("La ejecución ha sido interrumpida inesperadamente.");

                            alert.showAndWait();
                        }
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < mockRPM.size(); i++) {
                                int aux = Integer.parseInt(mockRPM.get(i).toString());
                                FXMLDocumentController.this.series.getData().add(new XYChart.Data(String.valueOf(i), aux));
                            }
                            grafica.getData().add(series);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ha ocurrido un error!");
            alert.setContentText("No hay datos para poder realizar el test.");

            alert.showAndWait();
        }
    }

    @FXML
    public void vistaPrevia() {

        try {
            Map m = new HashMap();

            String jasperReport = "/Recursos/PDF.jasper";

            JasperPrint jasperPrint = JasperFillManager.fillReport(FXMLDocumentController.class.getResourceAsStream("/Recursos/PDF.jasper"), m, new DataSource(this.valores));
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void obtenerRPM() {
        RPMReales = new ArrayList();
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        for (int i = 0; i < 400; i++) {
                            try {
                                rpm.run(inStream, outStream);
                                RPMReales.add(rpm.getRPM());
                                gauge.getRpmGauge().setValue(Double.parseDouble(RPMReales.get(i).toString()));
                                Thread.sleep(10);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Método que representa las temperaturas obtenidas del OBD
    @FXML
    public void obtenerTemp() {
        try {
            if (coolant.getFormattedResult() == null || coolant.getFormattedResult().equals("NODATA") || coolant.getFormattedResult().equals("")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Ha ocurrido un error!");
                alert.setContentText("Temperatura del liquido refrigerante no disponible.");

                alert.showAndWait();
            } else {
                String valorRefrig = String.valueOf(coolant.getTemperature());

                refrig.setText(valorRefrig);
                valores.setRefrigerante(valorRefrig);
            }

            /*if (ambient.getFormattedResult() == null || ambient.getFormattedResult().equals("NODATA") || ambient.getFormattedResult().equals("")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Ha ocurrido un error!");
                alert.setContentText("Temperatura de entrada(motor) no disponible.");

                alert.showAndWait();
            } else {
                String valorAmbient = String.valueOf(ambient.getTemperature());

                exterior.setText(valorAmbient);
                valores.setEntrada(valorAmbient);
            }*/
            if (intake.getFormattedResult() == null || intake.getFormattedResult().equals("NODATA") || intake.getFormattedResult().equals("")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Ha ocurrido un error!");
                alert.setContentText("Temperatura interior(motor) no disponible.");

                alert.showAndWait();
            } else {
                String valorInterior = String.valueOf(intake.getTemperature());

                interior.setText(valorInterior);
                valores.setInterior(valorInterior);
            }
        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ha ocurrido un error!");
            alert.setContentText("La obtencion de datos ha fallado.");

            alert.showAndWait();
        }
    }

    //Método que muestra los diferentes codigos pedidos al OBD
    @FXML
    public void obtenerCodigos() {
        try {
            String valorMil = mil.getFormattedResult();

            distancia.setText(valorMil);
            valores.setLuzMil(valorMil);

            if (perma.getFormattedResult() == null || perma.getFormattedResult().equals("NODATA") || perma.getFormattedResult().equals("")) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Información");
                alert.setHeaderText("Resultado");
                alert.setContentText("No hay codigos de error permanentes registrados.");

                alert.showAndWait();
            } else {
                String valorPerma = perma.getFormattedResult();

                itemsListaPerm.add(valorPerma);
                valores.setPermanente(valorPerma);
            }

            if (pending.getFormattedResult() == null || pending.getFormattedResult().equals("NODATA") || pending.getFormattedResult().equals("")) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Información");
                alert.setHeaderText("Resultado");
                alert.setContentText("No hay codigos de error pendientes registrados.");

                alert.showAndWait();
            } else {
                String valorPending = pending.getFormattedResult();

                itemsListaPend.add(valorPending);
                valores.setPendiente(valorPending);
            }

            listaPerm.setItems(itemsListaPerm);
            listaPend.setItems(itemsListaPend);
        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ha ocurrido un error!");
            alert.setContentText("La obtencion de datos ha fallado.");

            alert.showAndWait();
        }
    }

    //Método que cierra la conexión con el dispositivo OBD.
    @FXML
    public void desconectarOBD() {
        try {
            outStream.close();
            inStream.close();

            streamConnection.close();
        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ha ocurrido un error!");
            alert.setContentText("La conexión no se ha podido cerrar o no hay conexiones para cerrar.");

            alert.showAndWait();
        }
    }

    //Método que establece la conexión entre el dispositivo OBD y el programa
    @FXML
    public void conectarOBD() {
        try {
            streamConnection = (StreamConnection) Connector.open(url_disp);

            outStream = streamConnection.openOutputStream();
            inStream = streamConnection.openInputStream();

            new EchoOffCommand().run(inStream, outStream);

            new LineFeedOffCommand().run(inStream, outStream);

            new HeadersOffCommand().run(inStream, outStream);

            new SelectProtocolCommand(ObdProtocols.AUTO).run(inStream, outStream);

            /*La función getRPM() es propia de la clase RPMCommand. El formato del valor que devuelve es de
            tipo int (entero).*/
 /*int val = rpm.getRPM();

            gauge.getRpmGauge().setValue(val);

            Timer timer = new Timer();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    gauge.getRpmGauge().setValue(rpm.getRPM());
                }
            };

            timer.schedule(task, 10, 100);*/

 /*Platform.runLater(()->{
                for (int i = 0; i < 200; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println(val);
                }
            });*/
 /*new Thread(new Runnable() {
                @Override
                public void run() {
                   
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = 0; i < 10000; i++) {
                                    Thread.sleep(20);
                                    
                                    gauge.getRpmGauge().setValue();
                                }
                            } catch (InterruptedException ex) {
                                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                }
            }).start();*/
            try {
                rpm = new RPMCommand();
                rpm.run(inStream, outStream);
            } catch (Exception e) {

            }

            try {
                perma = new PermanentTroubleCodesCommand();
                perma.run(inStream, outStream);
            } catch (Exception e) {

            }

            try {
                pending = new PendingTroubleCodesCommand();
                pending.run(inStream, outStream);
            } catch (Exception e) {

            }

            try {
                mil = new DistanceMILOnCommand();
                mil.run(inStream, outStream);
            } catch (Exception e) {

            }

            /*try {
                intake = new AirIntakeTemperatureCommand();
                intake.run(inStream, outStream);
            } catch (Exception e) {

            }*/
            try {
                ambient = new AmbientAirTemperatureCommand();
                ambient.run(inStream, outStream);
            } catch (Exception e) {

            }

            try {
                coolant = new EngineCoolantTemperatureCommand();
                coolant.run(inStream, outStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException ex) {
            ex.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ha ocurrido un error!");
            alert.setContentText("Ha habido un problema con la conexión.");

            alert.showAndWait();
        } catch (InterruptedException ex) {
            ex.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ha ocurrido un error!");
            alert.setContentText("La ejecución ha sido interrumpida inesperadamente. Puede que ya haya una instancia"
                    + " del proceso en ejecución. \n Asegúrese de haber terminado la sesión actual antes de intentar iniciar"
                    + " una nueva.");

            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ha ocurrido un error!");
            alert.setContentText("Es posible que esté intentando establecer una conexión sin haber eleccionado un"
                    + " dispositivo. Por favor, seleccione un dispositivo.");

            alert.showAndWait();
        }
    }

    //** Es posible que sea irrelevante mostrar o incluso implementar este método, ya que la conexión siempre se **
    //** establecerá con un dispositivo OBD, que siempre tendrá los mismos servicios disponibles **
    //Método que lista los servicios que soporta el dispositivo conectado(OBD)
    @FXML
    public void listaServ() {
        itemsListaServ.clear();

        try {
            url_disp = new String();

            int i = listaDisp.getSelectionModel().getSelectedIndex();

            RemoteDevice rd = (RemoteDevice) dispositivos.get(i);

            nombreDisp = rd.getBluetoothAddress();

            HiloBusquedaServ hilo = new HiloBusquedaServ(nombreDisp, url_disp);

            hilo.start();

            Platform.runLater(() -> {
                barra_serv.setProgress(-1);
                serv.setDisable(true);
                conn.setDisable(true);
            });

            new Thread(new Runnable() {
                @Override
                public synchronized void run() {
                    try {
                        synchronized (url_disp) {
                            url_disp.wait();

                            Platform.runLater(() -> {
                                barra_serv.setProgress(0.0);
                                serv.setDisable(false);
                                conn.setDisable(false);

                                itemsListaServ.add(url_disp);
                                listaServ.setItems(itemsListaServ);
                            });

                            url_disp = hilo.getServicio();
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();

                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Ha ocurrido un error!");
                        alert.setContentText("La ejecución ha sido interrumpida inesperadamente");

                        alert.showAndWait();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ha ocurrido un error!");
            alert.setContentText("No ha seleccionado ningún dispositivo para establacer la conexión o el"
                    + " dispositivo no tiene el servicio requerido disponible");

            alert.showAndWait();
        }
    }

    //Método que lista los dispositivos bluetooth en rango
    @FXML
    public void listaDisp() {
        itemsListaDisp.clear();

        HiloBusquedaDisp hilo = new HiloBusquedaDisp(dispositivos);

        hilo.start();

        Platform.runLater(() -> {
            barra_disp.setProgress(-1);
            serv.setDisable(true);
            conn.setDisable(true);

            itemsListaDisp = hilo.getLista();

            listaDisp.setItems(itemsListaDisp);
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (dispositivos) {

                        dispositivos.wait();

                        Platform.runLater(() -> {
                            barra_disp.setProgress(0.0);
                            serv.setDisable(false);
                            conn.setDisable(false);
                        });

                        dispositivos = hilo.getDispositivos();

                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();

                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Ha ocurrido un error!");
                    alert.setContentText("La ejecución ha sido interrumpida inesperadamente");

                    alert.showAndWait();
                }
            }
        }, "You can name the thread here!").start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            //Se inicializa los objetos de escritura para que se guarden las excepciones en un fichero de logs
            fWriter = new FileWriter(LOGS, true);

            out = new BufferedWriter(fWriter);

            pWriter = new PrintWriter(out, true);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        valores = new Valores();

        dispositivos = new ArrayList();

        //Se prepara el panel que contiene los relojes y gráficas
        fondoRPM.setBackground(new Background(new BackgroundFill(Gauge.DARK_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        gauge = new MedidorRPM();

        panelRPM.getChildren().add(gauge);
    }

}
