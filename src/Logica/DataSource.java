/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author practicas
 */
public class DataSource implements JRDataSource {

    private Valores valores;

    public DataSource(Valores v) {
        this.valores = v;
    }

    @Override
    public boolean next() throws JRException {
        return true;
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {

        if (jrf.getName().equals("matricula")) {
            return valores.getMatricula();
        } else if (jrf.getName().equals("nombre")) {
            return valores.getNombre();
        } else if (jrf.getName().equals("apellidos")) {
            return valores.getApellidos();
        } else if (jrf.getName().equals("luzMil")) {
            return valores.getLuzMil();
        } else if (jrf.getName().equals("perma")) {
            return valores.getPermanente();
        } else if (jrf.getName().equals("pend")) {
            return valores.getPendiente();
        } else if (jrf.getName().equals("refrig")) {
            return valores.getRefrigerante();
        } else if (jrf.getName().equals("entrada")) {
            return valores.getEntrada();
        } else if (jrf.getName().equals("interior")) {
            return valores.getInterior();
        }

        return false;
    }
}
