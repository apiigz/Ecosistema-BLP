package modelo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Reglas de la simulación y datos del reporte.
 * No interactúa con el usuario (ni consola ni ventanas): los eventos del turno quedan guardados
 * en una lista para que el controlador los pida y la vista los muestre.
 */
public class clsEcosistema {
    public static final int MAX_LOBOS = 5;
    public static final int TURNOS_ENTRE_INTERVENCIONES = 3;

    private final List<clsPlanta> plantas = new ArrayList<>();
    private final List<clsConejo> conejos = new ArrayList<>();
    private final List<clsLobo> lobos = new ArrayList<>();

    private enumClima climaActual;
    private int turnoActual = 0;
    private int turnosTotales;
    private int totalLobosCreados = 0;          // iniciales + agregados (máximo 5 en toda la simulación)
    private boolean intervencionRealizada = false;

    // datos para el reporte (índice = enumTipo.ordinal())
    private final int[] nacimientos = new int[3];
    private final int[] muertes = new int[3];
    private final int[] siguienteId = new int[3];
    private final int[] edadLongeva = new int[3];
    private final String[] nombreLongeva = {"", "", ""};
    private String nombreMejorLobo = "";
    private int exitosMejorLobo = 0;
    private final List<Integer> actividadPorTurno = new ArrayList<>(); // posición i = turno i+1

    private final List<String> eventosTurno = new ArrayList<>();
    private final Random random = new Random();

    public clsEcosistema(enumClima climaInicial, int turnosTotales) {
        this.climaActual = climaInicial;
        this.turnosTotales = turnosTotales;
    }

    // ------------------------------------------------------------------ consultas

    public List<clsPlanta> getPlantas() { return plantas; }
    public List<clsConejo> getConejos() { return conejos; }
    public List<clsLobo> getLobos() { return lobos; }

    public int getCantidadPlantas() { return plantas.size(); }
    public int getCantidadConejos() { return conejos.size(); }
    public int getCantidadLobos() { return lobos.size(); }

    public int getCantidadConejosVivos() { return getConejosVivos().size(); }

    public List<clsConejo> getConejosVivos() {
        List<clsConejo> vivos = new ArrayList<>();
        for (clsConejo c : conejos) {
            if (c.getViva()) vivos.add(c);
        }
        return vivos;
    }

    /** Todas las entidades vivas (plantas, conejos y lobos) para mostrar en la tabla. */
    public List<clsEntidad> getEntidadesVivas() {
        List<clsEntidad> todas = new ArrayList<>();
        for (clsPlanta p : plantas) if (p.getViva()) todas.add(p);
        for (clsConejo c : conejos) if (c.getViva()) todas.add(c);
        for (clsLobo l : lobos) if (l.getViva()) todas.add(l);
        return todas;
    }

    public enumClima getClimaActual() { return climaActual; }
    public void setClimaActual(enumClima climaActual) { this.climaActual = climaActual; }

    public int getTurnoActual() { return turnoActual; }
    public int getTurnosTotales() { return turnosTotales; }
    public int getTotalLobosCreados() { return totalLobosCreados; }
    public boolean isIntervencionRealizada() { return intervencionRealizada; }

    /** Copia de los eventos del último turno (más las intervenciones posteriores). */
    public List<String> getEventosTurno() { return new ArrayList<>(eventosTurno); }

    public void registrarEvento(String texto) { eventosTurno.add(texto); }

    // ------------------------------------------------------------------ estado de la partida

    public boolean ecosistemaColapsado() {
        return plantas.isEmpty() || conejos.isEmpty() || lobos.isEmpty();
    }

    public boolean haTerminado() {
        return turnoActual >= turnosTotales || ecosistemaColapsado();
    }

    /** Texto con la causa de fin ("" si la simulación sigue en curso). */
    public String getCausaFin() {
        if (ecosistemaColapsado()) {
            List<String> extintas = new ArrayList<>();
            if (plantas.isEmpty()) extintas.add(enumTipo.PLANTA.getPlural());
            if (conejos.isEmpty()) extintas.add(enumTipo.CONEJO.getPlural());
            if (lobos.isEmpty()) extintas.add(enumTipo.LOBO.getPlural());
            return "Colapso del ecosistema (se extinguieron: " + String.join(", ", extintas) + ")";
        }
        if (turnoActual >= turnosTotales) {
            return "Turnos completos (" + turnosTotales + ")";
        }
        return "";
    }

    public boolean puedeIntervenir() {
        return turnoActual > 0
                && turnoActual % TURNOS_ENTRE_INTERVENCIONES == 0
                && !haTerminado()
                && !intervencionRealizada;
    }

    public boolean puedeAgregarLobo() { return totalLobosCreados < MAX_LOBOS; }

    // ------------------------------------------------------------------ creación de entidades

    public int siguienteId(enumTipo tipo) {
        return ++siguienteId[tipo.ordinal()];
    }

    private int rango(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Crea una entidad con valores al azar y la agrega al ecosistema.
     * Se usa para la población inicial y para la intervención.
     * @throws IllegalStateException si se supera el máximo de lobos
     */
    public clsEntidad crearEntidad(enumTipo tipo) {
        if (tipo == enumTipo.LOBO && !puedeAgregarLobo()) {
            throw new IllegalStateException("No se pueden agregar más de " + MAX_LOBOS
                    + " lobos en total durante la simulación.");
        }
        int id = siguienteId(tipo);
        clsEntidad nueva;
        switch (tipo) {
            case LOBO:
                nueva = new clsLobo(id, 0, rango(50, 100), rango(30, 70), "Lobo " + id, rango(60, 100), rango(9, 15), true);
                totalLobosCreados++;
                break;
            case CONEJO:
                nueva = new clsConejo(id, rango(50, 80), rango(3, 7), "Conejo " + id, rango(50, 90), rango(3, 4), true);
                break;
            case PLANTA:
                nueva = new clsPlanta(id, rango(4, 5), "Planta " + id, rango(30, 60), rango(9, 10), true);
                break;
            default:
                throw new IllegalArgumentException("Tipo de entidad desconocido");
        }
        insertar(nueva);
        return nueva;
    }

    /** Un nacimiento: se agrega, se cuenta y queda registrado en los eventos. */
    public void registrarNacimiento(clsEntidad hijo, clsEntidad padre) {
        insertar(hijo);
        nacimientos[hijo.getTipo().ordinal()]++;
        registrarEvento(padre.getNombre() + " se reprodujo: nació " + hijo.getNombre());
    }

    private void insertar(clsEntidad e) {
        switch (e.getTipo()) {
            case PLANTA: plantas.add((clsPlanta) e); break;
            case CONEJO: conejos.add((clsConejo) e); break;
            case LOBO:   lobos.add((clsLobo) e); break;
            default: break;
        }
    }

    // ------------------------------------------------------------------ turno

    /** Procesa un turno completo y devuelve los eventos que ocurrieron. */
    public List<String> procesarTurno() {
        if (haTerminado()) return getEventosTurno();

        turnoActual++;
        intervencionRealizada = false;
        eventosTurno.clear();

        // 1) las plantas se reproducen
        for (clsPlanta p : new ArrayList<>(plantas)) {
            if (p.getViva()) p.actuar(this);
        }
        // 2) los conejos buscan plantas y comen
        for (clsConejo c : new ArrayList<>(conejos)) {
            if (c.getViva()) c.actuar(this);
        }
        // 3) los lobos cazan
        for (clsLobo l : new ArrayList<>(lobos)) {
            if (l.getViva()) l.actuar(this);
        }
        // 4) clima, envejecimiento y energía base
        aplicarEfectosClimaYEnvejecer();
        // 5) récords (antes de borrar a los muertos) y limpieza
        actualizarRecords();
        limpiarMuertos(plantas);
        limpiarMuertos(conejos);
        limpiarMuertos(lobos);

        actividadPorTurno.add(eventosTurno.size());
        return getEventosTurno();
    }

    private void aplicarEfectosClimaYEnvejecer() {
        for (clsConejo c : conejos) {
            if (!c.estaVivo()) continue;
            switch (climaActual) {
                case SOLEADO:  c.setEnergia(c.getEnergia() + 5); break;
                case LLUVIOSO: c.setEnergia(c.getEnergia() + 3); break;
                case SEQUIA:   c.setEnergia(c.getEnergia() - 5); break;
                case INVIERNO: c.setEnergia(c.getEnergia() - 8); break;
                default: break;
            }
        }
        if (climaActual == enumClima.LLUVIOSO) {
            for (clsLobo l : lobos) {
                if (l.estaVivo()) l.setEnergia(l.getEnergia() - 5);
            }
        }
        for (clsPlanta p : plantas) {
            if (p.getViva()) { p.envejecer(); p.verificarMuerte(p.getEnergia()); }
        }
        for (clsConejo c : conejos) {
            if (c.getViva()) { c.envejecer(); c.verificarMuerte(c.getEnergia()); }
        }
        for (clsLobo l : lobos) {
            if (l.getViva()) { l.envejecer(); l.verificarMuerte(l.getEnergia()); }
        }
    }

    private void actualizarRecords() {
        for (clsPlanta p : plantas) registrarLongevidad(p);
        for (clsConejo c : conejos) registrarLongevidad(c);
        for (clsLobo l : lobos) {
            registrarLongevidad(l);
            if (l.getExitosCaza() > exitosMejorLobo) {
                exitosMejorLobo = l.getExitosCaza();
                nombreMejorLobo = l.getNombre();
            }
        }
    }

    private void registrarLongevidad(clsEntidad e) {
        int t = e.getTipo().ordinal();
        if (e.getEdad() > edadLongeva[t]) {
            edadLongeva[t] = e.getEdad();
            nombreLongeva[t] = e.getNombre();
        }
    }

    private void limpiarMuertos(List<? extends clsEntidad> lista) {
        Iterator<? extends clsEntidad> it = lista.iterator();
        while (it.hasNext()) {
            clsEntidad e = it.next();
            if (!e.getViva()) {
                muertes[e.getTipo().ordinal()]++;
                if (e.getCausaMuerte().isEmpty()) {
                    registrarEvento(e.getNombre() + " murió (sin energía)");
                }
                it.remove();
            }
        }
    }

    // ------------------------------------------------------------------ intervención (cada 3 turnos)

    private void verificarIntervencion() {
        if (puedeIntervenir()) return;
        if (haTerminado()) {
            throw new IllegalStateException("La simulación ya terminó.");
        }
        if (intervencionRealizada) {
            throw new IllegalStateException("Ya intervinieron en este turno. Avancen al siguiente.");
        }
        throw new IllegalStateException("Solo se puede intervenir cada " + TURNOS_ENTRE_INTERVENCIONES
                + " turnos (3, 6, 9...).");
    }

    public void intervenirCambiarClima(enumClima nuevo) {
        verificarIntervencion();
        if (nuevo == null) throw new IllegalArgumentException("Clima inválido.");
        if (nuevo == climaActual) {
            throw new IllegalStateException("El clima ya es " + nuevo.getEtiqueta() + ". Elegí otro o avanzá el turno.");
        }
        climaActual = nuevo;
        intervencionRealizada = true;
        registrarEvento("Intervención: el clima cambió a " + nuevo.getEtiqueta());
    }

    public clsEntidad intervenirAgregar(enumTipo tipo) {
        verificarIntervencion();
        if (tipo == null) throw new IllegalArgumentException("Tipo de entidad inválido.");
        clsEntidad nueva = crearEntidad(tipo);   // puede lanzar la excepción del máximo de lobos
        intervencionRealizada = true;
        registrarEvento("Intervención: se agregó " + nueva.getNombre());
        return nueva;
    }

    // ------------------------------------------------------------------ datos del reporte

    public int getNacimientos(enumTipo t) { return nacimientos[t.ordinal()]; }
    public int getMuertes(enumTipo t) { return muertes[t.ordinal()]; }
    public int getSiguienteId(enumTipo t) { return siguienteId[t.ordinal()]; }
    public String getNombreLongeva(enumTipo t) { return nombreLongeva[t.ordinal()]; }
    public int getEdadLongeva(enumTipo t) { return edadLongeva[t.ordinal()]; }
    public String getNombreMejorLobo() { return nombreMejorLobo; }
    public int getExitosMejorLobo() { return exitosMejorLobo; }
    public List<Integer> getActividadPorTurno() { return new ArrayList<>(actividadPorTurno); }

    /** Turno con más eventos (0 si todavía no se jugó ninguno). */
    public int getTurnoMayorActividad() {
        int mejor = -1;
        int turno = 0;
        for (int i = 0; i < actividadPorTurno.size(); i++) {
            if (actividadPorTurno.get(i) > mejor) {
                mejor = actividadPorTurno.get(i);
                turno = i + 1;
            }
        }
        return turno;
    }

    public int getActividadMaxima() {
        int max = 0;
        for (int a : actividadPorTurno) max = Math.max(max, a);
        return max;
    }

    // ------------------------------------------------------------------ para que el DAO pueda rearmar el estado

    public void restaurarEntidad(clsEntidad e) { insertar(e); }
    public void setTurnoActual(int t) { this.turnoActual = t; }
    public void setTotalLobosCreados(int n) { this.totalLobosCreados = n; }
    public void setIntervencionRealizada(boolean b) { this.intervencionRealizada = b; }
    public void setNacimientos(enumTipo t, int n) { nacimientos[t.ordinal()] = n; }
    public void setMuertes(enumTipo t, int n) { muertes[t.ordinal()] = n; }
    public void setSiguienteId(enumTipo t, int n) { siguienteId[t.ordinal()] = n; }
    public void setLongeva(enumTipo t, String nombre, int edad) {
        nombreLongeva[t.ordinal()] = nombre;
        edadLongeva[t.ordinal()] = edad;
    }
    public void setMejorLobo(String nombre, int exitos) {
        this.nombreMejorLobo = nombre;
        this.exitosMejorLobo = exitos;
    }
    public void setActividadPorTurno(List<Integer> actividad) {
        actividadPorTurno.clear();
        actividadPorTurno.addAll(actividad);
    }
    public void setEventosTurno(List<String> eventos) {
        eventosTurno.clear();
        eventosTurno.addAll(eventos);
    }
}
