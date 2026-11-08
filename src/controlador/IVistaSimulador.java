package controlador;

import java.util.List;

/** Ventana 2: la simulación turno a turno (VentanaSimulador). */
public interface IVistaSimulador extends IVista {
    void mostrarContadores(int turno, int turnosTotales, String clima, int plantas, int conejos, int lobos);
    void mostrarEntidades(Object[][] filas);          // columnas: Tipo, Nombre, Energía, Edad, Detalle
    void mostrarEventos(List<String> eventos);
    void mostrarMensajeEstado(String mensaje);
    void mostrarReporte(String[][] filas);            // columnas: Dato, Valor

    void habilitarAvance(boolean habilitado);
    void habilitarIntervencion(boolean habilitada);   // combos + botones de intervención

    /**
     * Caja de cantidad (y botón Agregar) de la intervención.
     * Se bloquea, por ejemplo, cuando el tipo elegido es Lobo y ya se llegó al máximo.
     * @param ayuda texto de ayuda que se muestra junto a la caja (puede ser vacío)
     */
    void habilitarCantidad(boolean habilitada, String ayuda);
    void limpiarCantidad();
}
