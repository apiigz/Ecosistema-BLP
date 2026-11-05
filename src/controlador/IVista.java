package controlador;

import java.util.List;

/**
 * Lo que la vista tiene que saber hacer. El controlador solo conoce esta interfaz
 * (no importa nada de Swing). La ventana de NetBeans la implementa.
 */
public interface IVista {
    // navegación
    void mostrarPantallaConfiguracion();
    void mostrarPantallaSimulacion();
    void mostrarPantallaReporte();

    // datos
    void mostrarContadores(int turno, int turnosTotales, String clima, int plantas, int conejos, int lobos);
    void mostrarEntidades(Object[][] filas);          // columnas: Tipo, Nombre, Energía, Edad, Detalle
    void mostrarEventos(List<String> eventos);
    void mostrarReporte(String[][] filas);            // columnas: Dato, Valor
    void mostrarMensajeEstado(String mensaje);

    // habilitar / deshabilitar
    void habilitarAvance(boolean habilitado);
    void habilitarIntervencion(boolean habilitada);
    void habilitarReporte(boolean habilitado);

    // mensajes
    void mostrarErrorConfiguracion(String mensaje);   // errores del formulario (en pantalla)
    void mostrarError(String mensaje);
    void mostrarInfo(String mensaje);
    boolean confirmar(String pregunta);               // true = el usuario dijo que sí
}
