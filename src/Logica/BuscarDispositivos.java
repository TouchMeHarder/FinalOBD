/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

/**
 *
 * @author practicas
 */
public class BuscarDispositivos {

    public ArrayList obtenerDispositivos() {
        
        

        ArrayList listaDisp = new ArrayList();
        final Object peticionFinalizada = new Object();

        listaDisp.clear();

        DiscoveryListener dispositivosEnRango = new DiscoveryListener() {
            @Override
            public void deviceDiscovered(RemoteDevice rd, DeviceClass dc) {
                listaDisp.add(rd);
            }

            @Override
            public void servicesDiscovered(int i, ServiceRecord[] srs) {
            }

            @Override
            public void serviceSearchCompleted(int i, int i1) {
            }

            @Override
            public void inquiryCompleted(int i) {
                synchronized (peticionFinalizada) {
                    peticionFinalizada.notifyAll();
                }
            }
        };

        synchronized (peticionFinalizada) {
            try {
                boolean init = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, dispositivosEnRango);
                if (init) {
                    peticionFinalizada.wait();
                }
            } catch (BluetoothStateException ex) {
                Logger.getLogger(BuscarDispositivos.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(BuscarDispositivos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return listaDisp;
    }

}
