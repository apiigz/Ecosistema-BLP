package controlador;

/** Ventana 1: configuración inicial (VentanaPrincipal). */
public interface IVistaConfiguracion extends IVista {
    void mostrarErrorConfiguracion(String mensaje);   // errores del formulario, en pantalla
    void limpiarErrorConfiguracion();
}
