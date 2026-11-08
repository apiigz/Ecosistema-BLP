package controlador;

/**
 * Lo que tienen en común las tres ventanas. El controlador solo conoce estas
 * interfaces (no importa nada de Swing).
 */
public interface IVista {
    void mostrarVentana();
    void ocultarVentana();

    void mostrarError(String mensaje);
    void mostrarInfo(String mensaje);
    boolean confirmar(String pregunta);   // true = el usuario dijo que sí
}
