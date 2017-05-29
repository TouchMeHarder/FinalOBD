/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author practicas
 */
//Check thoroughly
public class DatosSim {

    private File fichero_rpm = new File("src/Recursos/revoluciones.txt");
    private ArrayList valoresRPM;

    public DatosSim() {
        valoresRPM = leerFichero();
    }

    private ArrayList leerFichero() {
        ArrayList datosFichero = null;

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fichero_rpm));
            String str;
            String[] aux;
            str = in.readLine();
            datosFichero = new ArrayList();
            aux = str.split(",");

            for (int i = 0; i < aux.length; i++) {
                datosFichero.add(aux[i]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DatosSim.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatosSim.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(DatosSim.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return datosFichero;
    }

    public ArrayList getValoresRPM() {
        return valoresRPM;
    }
}
