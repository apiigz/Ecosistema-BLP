package controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dao.SimulacionDAO;
import modelo.clsEcosistema;
import modelo.clsEntidad;
import modelo.enumClima;
import modelo.enumTipo;

/**
 * Recibe las acciones de la vista, valida, coordina al modelo y al DAO,
 * y le entrega a la vista los datos actualizados. Sin código Swing.
 */
public class ControladorSimulacion {
    private static final int MIN_PLANTAS = 5, MAX_PLANTAS = 30;
    private static final int MIN_CONEJOS = 2, MAX_CONEJOS = 15;
    private static final int MIN_LOBOS = 1, MAX_LOBOS = 5;
    private static final int MIN_TURNOS = 10, MAX_TURNOS = 50;

    private final SimulacionDAO dao;
    private final IVista vista;
    private clsEcosistema eco;   // null = no hay simulación

    public ControladorSimulacion(SimulacionDAO dao, IVista vista) {
        this.dao = dao;
        this.vista = vista;
    }

    // ------------------------------------------------------------------ arranque

    /** Se llama al abrir la aplicación: retoma la simulación guardada si existe. */
    public void iniciar() {
        try {
            eco = dao.cargar();
        } catch (IOException e) {
            eco = null;
            vista.mostrarError("No se pudo cargar la simulación guardada: " + e.getMessage()
                    + ".\nSe empieza desde cero.");
        }

        if (eco == null) {
            mostrarSinSimulacion();
            vista.mostrarPantallaConfiguracion();
        } else {
            actualizarVista();
            vista.mostrarPantallaSimulacion();
            vista.mostrarInfo("Se retomó la simulación guardada en el turno " + eco.getTurnoActual() + ".");
        }
    }

    // ------------------------------------------------------------------ acciones de la vista

    public void iniciarSimulacion(String txtPlantas, String txtConejos, String txtLobos,
                                  String txtTurnos, String etiquetaClima) {
        List<String> errores = new ArrayList<>();
        int plantas = validarEntero("plantas", txtPlantas, MIN_PLANTAS, MAX_PLANTAS, errores);
        int conejos = validarEntero("conejos", txtConejos, MIN_CONEJOS, MAX_CONEJOS, errores);
        int lobos = validarEntero("lobos", txtLobos, MIN_LOBOS, MAX_LOBOS, errores);
        int turnos = validarEntero("turnos", txtTurnos, MIN_TURNOS, MAX_TURNOS, errores);
        enumClima clima = enumClima.desdeEtiqueta(etiquetaClima);
        if (clima == null) errores.add("Elegí un clima inicial.");

        if (!errores.isEmpty()) {
            vista.mostrarErrorConfiguracion(String.join("\n", errores));
            return;
        }
        if (hayEnCurso() && !vista.confirmar("Hay una simulación en curso. ¿Querés descartarla y empezar esta?")) {
            return;
        }

        eco = new clsEcosistema(clima, turnos);
        for (int i = 0; i < plantas; i++) eco.crearEntidad(enumTipo.PLANTA);
        for (int i = 0; i < conejos; i++) eco.crearEntidad(enumTipo.CONEJO);
        for (int i = 0; i < lobos; i++) eco.crearEntidad(enumTipo.LOBO);

        boolean guardado = dao.guardar(eco);
        actualizarVista();
        vista.mostrarPantallaSimulacion();
        if (!guardado) avisarErrorDeGuardado();
    }

    public void avanzarTurno() {
        if (eco == null) {
            vista.mostrarError("Todavía no hay una simulación en curso. Configurá una para empezar.");
            return;
        }
        if (eco.haTerminado()) {
            vista.mostrarInfo("La simulación ya terminó. Podés ver el reporte o empezar una nueva.");
            return;
        }
        eco.procesarTurno();
        boolean guardado = dao.guardar(eco);
        actualizarVista();
        if (!guardado) avisarErrorDeGuardado();
        if (eco.haTerminado()) vista.mostrarPantallaReporte();
    }

    public void cambiarClima(String etiquetaClima) {
        if (!hayEcosistema()) return;
        enumClima nuevo = enumClima.desdeEtiqueta(etiquetaClima);
        if (nuevo == null) {
            vista.mostrarError("Elegí un clima de la lista.");
            return;
        }
        try {
            eco.intervenirCambiarClima(nuevo);
        } catch (IllegalStateException | IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
            return;
        }
        boolean guardado = dao.guardar(eco);
        actualizarVista();
        vista.mostrarInfo("Clima cambiado a " + nuevo.getEtiqueta() + ".");
        if (!guardado) avisarErrorDeGuardado();
    }

    public void agregarEntidad(String etiquetaTipo) {
        if (!hayEcosistema()) return;
        enumTipo tipo = enumTipo.desdeEtiqueta(etiquetaTipo);
        if (tipo == null) {
            vista.mostrarError("Elegí qué entidad querés agregar.");
            return;
        }
        clsEntidad nueva;
        try {
            nueva = eco.intervenirAgregar(tipo);
        } catch (IllegalStateException | IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
            return;
        }
        boolean guardado = dao.guardar(eco);
        actualizarVista();
        vista.mostrarInfo("Se agregó " + nueva.getNombre() + " al ecosistema.");
        if (!guardado) avisarErrorDeGuardado();
    }

    public void nuevaSimulacion() {
        if (hayEnCurso() && !vista.confirmar("Hay una simulación en curso. Si empezás una nueva se pierde. ¿Seguro?")) {
            return;
        }
        eco = null;
        if (!dao.eliminar()) {
            vista.mostrarError("No se pudo borrar la simulación guardada anterior.");
        }
        mostrarSinSimulacion();
        vista.mostrarPantallaConfiguracion();
    }

    public void guardar() {
        if (!hayEcosistema()) return;
        if (dao.guardar(eco)) vista.mostrarInfo("Simulación guardada.");
        else avisarErrorDeGuardado();
    }

    public void verReporte() {
        if (eco == null || !eco.haTerminado()) {
            vista.mostrarError("El reporte final está disponible cuando termina la simulación.");
            return;
        }
        vista.mostrarReporte(construirReporte());
        vista.mostrarPantallaReporte();
    }

    // ------------------------------------------------------------------ internos

    private boolean hayEnCurso() { return eco != null && !eco.haTerminado(); }

    private boolean hayEcosistema() {
        if (eco == null) {
            vista.mostrarError("Todavía no hay una simulación en curso. Configurá una para empezar.");
            return false;
        }
        return true;
    }

    private void avisarErrorDeGuardado() {
        vista.mostrarError("No se pudo guardar la simulación en el archivo. Puede que se pierda el progreso al cerrar.");
    }

    private int validarEntero(String campo, String texto, int min, int max, List<String> errores) {
        String limpio = (texto == null) ? "" : texto.trim();
        if (limpio.isEmpty()) {
            errores.add("La cantidad de " + campo + " es obligatoria.");
            return 0;
        }
        int valor;
        try {
            valor = Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            errores.add("La cantidad de " + campo + " tiene que ser un número entero.");
            return 0;
        }
        if (valor < min || valor > max) {
            errores.add("La cantidad de " + campo + " tiene que estar entre " + min + " y " + max + ".");
        }
        return valor;
    }

    /** Le entrega a la vista todos los datos actuales del ecosistema. */
    private void actualizarVista() {
        vista.mostrarContadores(eco.getTurnoActual(), eco.getTurnosTotales(), eco.getClimaActual().getEtiqueta(),
                eco.getCantidadPlantas(), eco.getCantidadConejos(), eco.getCantidadLobos());
        vista.mostrarEntidades(construirFilasEntidades());
        vista.mostrarEventos(eco.getEventosTurno());

        boolean termino = eco.haTerminado();
        vista.habilitarAvance(!termino);
        vista.habilitarIntervencion(eco.puedeIntervenir());
        vista.habilitarReporte(termino);
        if (termino) vista.mostrarReporte(construirReporte());

        if (termino) {
            vista.mostrarMensajeEstado("Simulación finalizada: " + eco.getCausaFin() + ".");
        } else if (eco.puedeIntervenir()) {
            vista.mostrarMensajeEstado("Turno " + eco.getTurnoActual() + " de " + eco.getTurnosTotales()
                    + ": podés intervenir (cambiar el clima o agregar una entidad) o avanzar.");
        } else {
            int proxima = (eco.getTurnoActual() / clsEcosistema.TURNOS_ENTRE_INTERVENCIONES + 1)
                    * clsEcosistema.TURNOS_ENTRE_INTERVENCIONES;
            vista.mostrarMensajeEstado("Turno " + eco.getTurnoActual() + " de " + eco.getTurnosTotales()
                    + ": la próxima intervención es en el turno " + proxima + ".");
        }
    }

    private void mostrarSinSimulacion() {
        vista.mostrarContadores(0, 0, "-", 0, 0, 0);
        vista.mostrarEntidades(new Object[0][]);
        vista.mostrarEventos(new ArrayList<String>());
        vista.mostrarReporte(new String[0][]);
        vista.habilitarAvance(false);
        vista.habilitarIntervencion(false);
        vista.habilitarReporte(false);
        vista.mostrarMensajeEstado("No hay una simulación en curso. Configurá una para empezar.");
    }

    private Object[][] construirFilasEntidades() {
        List<clsEntidad> vivas = eco.getEntidadesVivas();
        Object[][] filas = new Object[vivas.size()][];
        for (int i = 0; i < vivas.size(); i++) {
            clsEntidad e = vivas.get(i);
            filas[i] = new Object[]{e.getTipo().getEtiqueta(), e.getNombre(),
                Math.round(e.getEnergia()), e.getEdad(), e.getDetalle()};
        }
        return filas;
    }

    private String[][] construirReporte() {
        List<String[]> f = new ArrayList<>();
        f.add(new String[]{"Causa de fin", eco.getCausaFin()});
        f.add(new String[]{"Turnos jugados", eco.getTurnoActual() + " de " + eco.getTurnosTotales()});
        f.add(new String[]{"Clima final", eco.getClimaActual().getEtiqueta()});

        int turnoMax = eco.getTurnoMayorActividad();
        f.add(new String[]{"Turno de mayor actividad",
            turnoMax == 0 ? "Sin actividad" : "Turno " + turnoMax + " (" + eco.getActividadMaxima() + " eventos)"});

        for (enumTipo t : enumTipo.values()) {
            String nombre = eco.getNombreLongeva(t);
            f.add(new String[]{t.getEtiqueta() + (t == enumTipo.CONEJO ? " más longevo" : " más longeva"),
                nombre.isEmpty() ? "-" : nombre + " (" + eco.getEdadLongeva(t) + " turnos de edad)"});
        }

        String lobo = eco.getNombreMejorLobo();
        f.add(new String[]{"Lobo con más cacerías exitosas",
            lobo.isEmpty() ? "Ningún lobo logró cazar" : lobo + " (" + eco.getExitosMejorLobo() + " cacerías)"});

        for (enumTipo t : enumTipo.values()) {
            f.add(new String[]{"Nacimientos de " + t.getPlural().toLowerCase(), String.valueOf(eco.getNacimientos(t))});
        }
        for (enumTipo t : enumTipo.values()) {
            f.add(new String[]{"Muertes de " + t.getPlural().toLowerCase(), String.valueOf(eco.getMuertes(t))});
        }
        f.add(new String[]{"Población final", eco.getCantidadPlantas() + " plantas | "
            + eco.getCantidadConejos() + " conejos | " + eco.getCantidadLobos() + " lobos"});
        return f.toArray(new String[0][]);
    }
}
