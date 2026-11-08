package controlador;

/** Ventana 3: historial de acontecimientos (VentanaHistorial). */
public interface IVistaHistorial extends IVista {
    void mostrarHistorial(String[][] filas);          // columnas: Turno, Categoría, Descripción
    void reiniciarFiltros();
}
