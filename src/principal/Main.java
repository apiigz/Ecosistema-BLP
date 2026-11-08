package principal;

import controlador.ControladorSimulacion;
import dao.SimulacionDAO;
import vista.VentanaHistorial;
import vista.VentanaPrincipal;
import vista.VentanaSimulador;

/** Crea las piezas, las conecta y muestra la primera ventana. */
public class Main {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            SimulacionDAO dao = new SimulacionDAO("datos/simulacion.txt"); // ruta relativa al proyecto

            VentanaPrincipal configuracion = new VentanaPrincipal();   // ventana 1
            VentanaSimulador simulador = new VentanaSimulador();       // ventana 2
            VentanaHistorial historial = new VentanaHistorial();       // ventana 3

            ControladorSimulacion controlador = new ControladorSimulacion(dao, configuracion, simulador, historial);
            configuracion.setControlador(controlador);
            simulador.setControlador(controlador);
            historial.setControlador(controlador);

            controlador.iniciar();   // muestra la ventana que corresponda (config, o simulador si hay partida guardada)
        });
    }
}
