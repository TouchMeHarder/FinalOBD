/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

/**
 *
 * @author practicas
 */
public class BuscarSevicios {

    private final UUID OBEX_OPP = new UUID(0x1101);

    private final int URL = 0x0100;

    public Map<String, List<String>> getServicios() {

        UUID[] uscaUuid = new UUID[]{OBEX_OPP};
        final Object busquedaSevicioCompetada = new Object();

        BuscarDispositivos buscarDisp = new BuscarDispositivos();

        final Map<String, List<String>> mapaResultado = new HashMap<String, List<String>>();

        try {
            DiscoveryListener serviciosDisponibles = new DiscoveryListener() {
                @Override
                public void deviceDiscovered(RemoteDevice rd, DeviceClass dc) {
                }

                @Override
                public void servicesDiscovered(int i, ServiceRecord[] srs) {
                    for (int j = 0; j < srs.length; j++) {
                        String url = srs[j].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                        if (url == null) {
                            continue;
                        }
                        String aux;

                        RemoteDevice rd = srs[j].getHostDevice();
                        DataElement serviceName = srs[j].getAttributeValue(URL);

                        mapaResultado.get(rd.getBluetoothAddress()).add(url);
                    }
                }

                @Override
                public void serviceSearchCompleted(int i, int i1) {
                    synchronized (busquedaSevicioCompetada) {
                        busquedaSevicioCompetada.notifyAll();
                    }
                }

                @Override
                public void inquiryCompleted(int i) {
                }
            };

            ArrayList dispositivos = buscarDisp.obtenerDispositivos();

            for (int i = 0; i < dispositivos.size(); i++) {
                RemoteDevice rd = (RemoteDevice) dispositivos.get(i);

                List<String> listaDetallesDisp = new ArrayList<String>();

                listaDetallesDisp.add(rd.getFriendlyName(false));
                listaDetallesDisp.add(rd.getBluetoothAddress());

                mapaResultado.put(rd.getBluetoothAddress(), listaDetallesDisp);

                synchronized (busquedaSevicioCompetada) {
                    LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(null, uscaUuid, rd, serviciosDisponibles);
                    busquedaSevicioCompetada.wait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapaResultado;
    }

}
