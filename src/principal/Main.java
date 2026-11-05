package principal;

import controlador.ControladorSimulacion;
import dao.SimulacionDAO;
import vista.VentanaPrincipal;

/** Crea las piezas, las conecta y muestra la ventana. */
public class Main {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            SimulacionDAO dao = new SimulacionDAO("datos/simulacion.txt"); // ruta relativa al proyecto
            VentanaPrincipal ventana = new VentanaPrincipal();
            ControladorSimulacion controlador = new ControladorSimulacion(dao, ventana);
            ventana.setControlador(controlador);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
            controlador.iniciar();   // después de mostrar la ventana, así los mensajes salen sobre ella
        });
    }
}
