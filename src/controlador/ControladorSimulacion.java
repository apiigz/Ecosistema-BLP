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
 * Recibe las acciones de las tres ventanas, valida, coordina al modelo y al DAO,
 * y le entrega a cada vista los datos actualizados. Sin código Swing.
 */
public class ControladorSimulacion {
    private static final int MIN_PLANTAS = 5, MAX_PLANTAS = 30;
    private static final int MIN_CONEJOS = 2, MAX_CONEJOS = 15;
    private static final int MIN_LOBOS = 1, MAX_LOBOS = clsEcosistema.MAX_LOBOS;
    private static final int MIN_TURNOS = 10, MAX_TURNOS = 50;

    private final SimulacionDAO dao;
    private final IVistaConfiguracion config;
    private final IVistaSimulador sim;
    private final IVistaHistorial hist;
    private clsEcosistema eco;                 // null = no hay simulación
    private String tipoElegido = "Conejo";     // lo que está seleccionado en el combo de intervención

    public ControladorSimulacion(SimulacionDAO dao, IVistaConfiguracion config,
                                 IVistaSimulador sim, IVistaHistorial hist) {
        this.dao = dao;
        this.config = config;
        this.sim = sim;
        this.hist = hist;
    }

    // ------------------------------------------------------------------ arranque

    /** Se llama al abrir la aplicación: retoma la simulación guardada si existe. */
    public void iniciar() {
        String errorDeCarga = null;
        try {
            eco = dao.cargar();
        } catch (IOException e) {
            eco = null;
            errorDeCarga = "No se pudo cargar la simulación guardada: " + e.getMessage()
                    + ".\nSe empieza desde cero.";
        }

        if (eco == null) {
            mostrarSinSimulacion();
            config.mostrarVentana();
            if (errorDeCarga != null) config.mostrarError(errorDeCarga);
        } else {
            actualizarVista();
            sim.mostrarVentana();
            sim.mostrarInfo("Se retomó la simulación guardada en el turno " + eco.getTurnoActual() + ".");
            if (eco.haTerminado()) sim.mostrarReporte(construirReporte());
        }
    }

    // ------------------------------------------------------------------ ventana de configuración

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
            config.mostrarErrorConfiguracion(String.join("\n", errores));
            return;
        }
        config.limpiarErrorConfiguracion();

        eco = new clsEcosistema(clima, turnos);
        for (int i = 0; i < plantas; i++) eco.crearEntidad(enumTipo.PLANTA);
        for (int i = 0; i < conejos; i++) eco.crearEntidad(enumTipo.CONEJO);
        for (int i = 0; i < lobos; i++) eco.crearEntidad(enumTipo.LOBO);

        boolean guardado = dao.guardar(eco);
        actualizarVista();
        config.ocultarVentana();
        sim.mostrarVentana();
        if (!guardado) avisarErrorDeGuardado();
    }

    // ------------------------------------------------------------------ ventana del simulador

    public void avanzarTurno() {
        if (eco == null) {
            sim.mostrarError("Todavía no hay una simulación en curso. Configurá una para empezar.");
            return;
        }
        if (eco.haTerminado()) {
            sim.mostrarInfo("La simulación ya terminó.");
            return;
        }
        eco.procesarTurno();
        boolean guardado = dao.guardar(eco);
        actualizarVista();
        if (!guardado) avisarErrorDeGuardado();
        if (eco.haTerminado()) {
            sim.mostrarReporte(construirReporte());
            if (sim.confirmar("¿Querés empezar una nueva simulación?")) nuevaSimulacion();
        }
    }

    public void cambiarClima(String etiquetaClima) {
        if (!hayEcosistema()) return;
        enumClima nuevo = enumClima.desdeEtiqueta(etiquetaClima);
        if (nuevo == null) {
            sim.mostrarError("Elegí un clima de la lista.");
            return;
        }
        try {
            eco.intervenirCambiarClima(nuevo);
        } catch (IllegalStateException | IllegalArgumentException e) {
            sim.mostrarError(e.getMessage());
            return;
        }
        boolean guardado = dao.guardar(eco);
        actualizarVista();
        sim.mostrarInfo("Clima cambiado a " + nuevo.getEtiqueta() + ".");
        if (!guardado) avisarErrorDeGuardado();
    }

    /** La vista avisa cada vez que cambia el tipo elegido en el combo de intervención. */
    public void elegirTipo(String etiquetaTipo) {
        if (etiquetaTipo != null) tipoElegido = etiquetaTipo;
        refrescarCantidad();
    }

    /**
     * Agrega varias entidades del mismo tipo. Si son lobos, el total (iniciales + agregados)
     * no puede pasar de 5: si se pasa, muestra el error y no agrega nada (la caja queda
     * como está para que el usuario la corrija).
     */
    public void agregarEntidad(String etiquetaTipo, String txtCantidad) {
        if (!hayEcosistema()) return;
        enumTipo tipo = enumTipo.desdeEtiqueta(etiquetaTipo);
        if (tipo == null) {
            sim.mostrarError("Elegí qué entidad querés agregar.");
            return;
        }

        String limpio = (txtCantidad == null) ? "" : txtCantidad.trim();
        if (limpio.isEmpty()) {
            sim.mostrarError("Escribí la cantidad que querés agregar.");
            return;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            sim.mostrarError("La cantidad tiene que ser un número entero.");
            return;
        }
        if (cantidad < 1 || cantidad > clsEcosistema.MAX_AGREGAR_POR_INTERVENCION) {
            sim.mostrarError("La cantidad tiene que estar entre 1 y " + clsEcosistema.MAX_AGREGAR_POR_INTERVENCION + ".");
            return;
        }
        if (tipo == enumTipo.LOBO && cantidad > eco.getLobosRestantes()) {
            sim.mostrarError(eco.mensajeLimiteLobos());
            return;
        }

        try {
            eco.intervenirAgregar(tipo, cantidad);
        } catch (IllegalStateException | IllegalArgumentException e) {
            sim.mostrarError(e.getMessage());
            return;
        }
        boolean guardado = dao.guardar(eco);
        sim.limpiarCantidad();
        actualizarVista();
        sim.mostrarInfo("Se agregó " + cantidad + " " + (cantidad == 1 ? tipo.getEtiqueta() : tipo.getPlural())
                + " al ecosistema.");
        if (!guardado) avisarErrorDeGuardado();
    }

    public void nuevaSimulacion() {
        if (hayEnCurso() && !sim.confirmar("Hay una simulación en curso. Si empezás una nueva se pierde. ¿Seguro?")) {
            return;
        }
        eco = null;
        if (!dao.eliminar()) {
            sim.mostrarError("No se pudo borrar la simulación guardada anterior.");
        }
        mostrarSinSimulacion();
        hist.ocultarVentana();
        sim.ocultarVentana();
        config.mostrarVentana();
    }

    // ------------------------------------------------------------------ ventana de historial

    public void abrirHistorial() {
        if (!hayEcosistema()) return;
        hist.reiniciarFiltros();
        hist.mostrarHistorial(filtrar("Todos", "", Integer.MIN_VALUE, Integer.MAX_VALUE));
        hist.mostrarVentana();
    }

    public void volverAlSimulador() {
        hist.ocultarVentana();
        sim.mostrarVentana();
    }

    /**
     * @param categoria "Todos", "Reproducción", "Mortalidad" o "Clima"
     * @param busqueda  texto a buscar dentro de la descripción (vacío = sin filtro)
     * @param desde     turno mínimo (vacío = sin límite)
     * @param hasta     turno máximo (vacío = sin límite)
     */
    public void filtrarHistorial(String categoria, String busqueda, String desde, String hasta) {
        if (eco == null) return;
        List<String> errores = new ArrayList<>();
        int d = turnoOpcional("Desde", desde, Integer.MIN_VALUE, errores);
        int h = turnoOpcional("Hasta", hasta, Integer.MAX_VALUE, errores);
        if (errores.isEmpty() && d > h) errores.add("\"Desde\" no puede ser mayor que \"Hasta\".");
        if (!errores.isEmpty()) {
            hist.mostrarError(String.join("\n", errores));
            return;
        }
        hist.mostrarHistorial(filtrar(categoria, busqueda, d, h));
    }

    private int turnoOpcional(String campo, String texto, int porDefecto, List<String> errores) {
        String limpio = (texto == null) ? "" : texto.trim();
        if (limpio.isEmpty()) return porDefecto;
        try {
            int n = Integer.parseInt(limpio);
            if (n < 1) errores.add("\"" + campo + "\" tiene que ser un turno de 1 en adelante.");
            return n;
        } catch (NumberFormatException e) {
            errores.add("\"" + campo + "\" tiene que ser un número entero.");
            return porDefecto;
        }
    }

    private String[][] filtrar(String categoria, String busqueda, int desde, int hasta) {
        String texto = (busqueda == null) ? "" : busqueda.trim().toLowerCase();
        boolean todas = categoria == null || categoria.equalsIgnoreCase("Todos");
        List<String[]> res = new ArrayList<>();
        for (String[] fila : eco.getHistorial()) {
            int turno = Integer.parseInt(fila[0]);
            if (turno < desde || turno > hasta) continue;
            if (!todas && !fila[1].equalsIgnoreCase(categoria)) continue;
            if (!texto.isEmpty() && !fila[2].toLowerCase().contains(texto)) continue;
            res.add(fila);
        }
        return res.toArray(new String[0][]);
    }

    // ------------------------------------------------------------------ internos

    private boolean hayEnCurso() { return eco != null && !eco.haTerminado(); }

    private boolean hayEcosistema() {
        if (eco == null) {
            sim.mostrarError("Todavía no hay una simulación en curso. Configurá una para empezar.");
            return false;
        }
        return true;
    }

    private void avisarErrorDeGuardado() {
        sim.mostrarError("No se pudo guardar la simulación en el archivo. Puede que se pierda el progreso al cerrar.");
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

    /** Le entrega al simulador todos los datos actuales del ecosistema. */
    private void actualizarVista() {
        sim.mostrarContadores(eco.getTurnoActual(), eco.getTurnosTotales(), eco.getClimaActual().getEtiqueta(),
                eco.getCantidadPlantas(), eco.getCantidadConejos(), eco.getCantidadLobos());
        sim.mostrarEntidades(construirFilasEntidades());
        sim.mostrarEventos(eco.getEventosTurno());

        boolean termino = eco.haTerminado();
        sim.habilitarAvance(!termino);
        sim.habilitarIntervencion(eco.puedeIntervenir());
        refrescarCantidad();

        if (termino) {
            sim.mostrarMensajeEstado("Simulación finalizada: " + eco.getCausaFin() + ".");
        } else if (eco.puedeIntervenir()) {
            sim.mostrarMensajeEstado("Turno " + eco.getTurnoActual() + " de " + eco.getTurnosTotales()
                    + ": podés intervenir (cambiar el clima o agregar entidades) o avanzar.");
        } else {
            int proxima = (eco.getTurnoActual() / clsEcosistema.TURNOS_ENTRE_INTERVENCIONES + 1)
                    * clsEcosistema.TURNOS_ENTRE_INTERVENCIONES;
            sim.mostrarMensajeEstado("Turno " + eco.getTurnoActual() + " de " + eco.getTurnosTotales()
                    + ": la próxima intervención es en el turno " + proxima + ".");
        }
    }

    /**
     * Decide si la caja de cantidad está habilitada. Regla de los lobos: si el tipo elegido
     * es Lobo y ya no se puede agregar ninguno (restante = 0), la caja se bloquea.
     */
    private void refrescarCantidad() {
        if (eco == null || !eco.puedeIntervenir()) {
            sim.habilitarCantidad(false, "");
            return;
        }
        enumTipo tipo = enumTipo.desdeEtiqueta(tipoElegido);
        if (tipo == enumTipo.LOBO) {
            int restantes = eco.getLobosRestantes();
            if (restantes == 0) {
                sim.habilitarCantidad(false, "Ya hay " + clsEcosistema.MAX_LOBOS + " lobos: no se pueden agregar más.");
            } else {
                sim.habilitarCantidad(true, "Podés agregar hasta " + restantes + " lobo" + (restantes == 1 ? "" : "s") + ".");
            }
        } else {
            sim.habilitarCantidad(true, "Hasta " + clsEcosistema.MAX_AGREGAR_POR_INTERVENCION + " por intervención.");
        }
    }

    private void mostrarSinSimulacion() {
        sim.mostrarContadores(0, 0, "-", 0, 0, 0);
        sim.mostrarEntidades(new Object[0][]);
        sim.mostrarEventos(new ArrayList<String>());
        sim.habilitarAvance(false);
        sim.habilitarIntervencion(false);
        sim.habilitarCantidad(false, "");
        sim.mostrarMensajeEstado("No hay una simulación en curso. Configurá una para empezar.");
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
