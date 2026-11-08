package dao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.clsAnimal;
import modelo.clsConejo;
import modelo.clsEcosistema;
import modelo.clsEntidad;
import modelo.clsLobo;
import modelo.clsPlanta;
import modelo.enumClima;
import modelo.enumTipo;

/**
 * Guarda y lee la simulación en un archivo de texto plano (.txt).
 * No usa Swing, no conoce la vista ni el controlador y no muestra mensajes:
 * informa el resultado (true/false, null o IOException) y el controlador decide qué hacer.
 *
 * Formato del archivo (una cosa por línea):
 *   clave=valor                       datos generales, récords y contadores
 *   evento=texto                      eventos del último turno
 *   hist=turno|categoria|texto        historial completo (una línea por acontecimiento)
 *   PLANTA|id|nombre|energia|edad|tamanio
 *   CONEJO|id|nombre|energia|edad|velocidad|peso
 *   LOBO|id|nombre|energia|edad|velocidad|peso|exitosCaza
 */
public class SimulacionDAO {
    private static final String VERSION = "ECOSISTEMA_V1";
    private final Path ruta;

    /** @param rutaRelativa por ejemplo "datos/simulacion.txt" (relativa a la carpeta del proyecto) */
    public SimulacionDAO(String rutaRelativa) {
        this.ruta = Paths.get(rutaRelativa);
    }

    public boolean existe() {
        return Files.isRegularFile(ruta);
    }

    /** @return true si se pudo guardar, false si hubo un error de entrada/salida */
    public boolean guardar(clsEcosistema eco) {
        try {
            Path carpeta = ruta.getParent();
            if (carpeta != null) Files.createDirectories(carpeta);
            Files.write(ruta, aLineas(eco), StandardCharsets.UTF_8);
            return true;
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    /**
     * @return la simulación guardada, o null si todavía no hay archivo
     * @throws IOException si no se puede leer o si el archivo tiene datos inválidos
     */
    public clsEcosistema cargar() throws IOException {
        if (!existe()) return null;
        List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        try {
            return aEcosistema(lineas);
        } catch (RuntimeException e) {
            throw new IOException("el archivo tiene datos inválidos (" + e.getMessage() + ")");
        }
    }

    /** Borra el archivo guardado. @return false si no se pudo borrar */
    public boolean eliminar() {
        try {
            Files.deleteIfExists(ruta);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ------------------------------------------------------------------ escritura

    private List<String> aLineas(clsEcosistema eco) {
        List<String> l = new ArrayList<>();
        l.add("FORMATO=" + VERSION);
        l.add("turnoActual=" + eco.getTurnoActual());
        l.add("turnosTotales=" + eco.getTurnosTotales());
        l.add("clima=" + eco.getClimaActual().name());
        l.add("lobosCreados=" + eco.getTotalLobosCreados());
        l.add("intervencionRealizada=" + eco.isIntervencionRealizada());

        for (enumTipo t : enumTipo.values()) {
            l.add("nacimientos." + t.name() + "=" + eco.getNacimientos(t));
            l.add("muertes." + t.name() + "=" + eco.getMuertes(t));
            l.add("siguienteId." + t.name() + "=" + eco.getSiguienteId(t));
            l.add("longeva." + t.name() + "=" + eco.getNombreLongeva(t) + "|" + eco.getEdadLongeva(t));
        }
        l.add("mejorLobo=" + eco.getNombreMejorLobo() + "|" + eco.getExitosMejorLobo());

        StringBuilder act = new StringBuilder();
        for (int a : eco.getActividadPorTurno()) {
            if (act.length() > 0) act.append(",");
            act.append(a);
        }
        l.add("actividad=" + act);

        for (String ev : eco.getEventosTurno()) {
            l.add("evento=" + ev);
        }

        for (String[] h : eco.getHistorial()) {
            l.add("hist=" + h[0] + "|" + h[1] + "|" + h[2]);
        }

        for (clsEntidad e : eco.getEntidadesVivas()) {
            StringBuilder sb = new StringBuilder();
            sb.append(e.getTipo().name()).append("|").append(e.getId()).append("|")
              .append(e.getNombre()).append("|").append(e.getEnergia()).append("|").append(e.getEdad());
            if (e instanceof clsPlanta) {
                sb.append("|").append(((clsPlanta) e).getTamanio());
            } else if (e instanceof clsAnimal) {
                clsAnimal a = (clsAnimal) e;
                sb.append("|").append(a.getVelocidad()).append("|").append(a.getPeso());
                if (e instanceof clsLobo) {
                    sb.append("|").append(((clsLobo) e).getExitosCaza());
                }
            }
            l.add(sb.toString());
        }
        return l;
    }

    // ------------------------------------------------------------------ lectura

    private clsEcosistema aEcosistema(List<String> lineas) {
        Map<String, String> campos = new HashMap<>();
        List<String> eventos = new ArrayList<>();
        List<String[]> historial = new ArrayList<>();
        List<clsEntidad> entidades = new ArrayList<>();

        for (String linea : lineas) {
            if (linea.trim().isEmpty()) continue;
            if (esLineaDeEntidad(linea)) {
                entidades.add(leerEntidad(linea));
                continue;
            }
            int i = linea.indexOf('=');
            if (i < 0) throw new IllegalArgumentException("línea no reconocida: " + linea);
            String clave = linea.substring(0, i);
            String valor = linea.substring(i + 1);
            if (clave.equals("evento")) eventos.add(valor);
            else if (clave.equals("hist")) {
                String[] h = valor.split("\\|", 3);
                if (h.length != 3) throw new IllegalArgumentException("historial mal formado: " + valor);
                Integer.parseInt(h[0].trim());
                historial.add(h);
            }
            else campos.put(clave, valor);
        }

        if (!VERSION.equals(campos.get("FORMATO"))) {
            throw new IllegalArgumentException("formato desconocido");
        }

        int total = entero(campos, "turnosTotales");
        int turno = entero(campos, "turnoActual");
        if (total < 1 || turno < 0 || turno > total) {
            throw new IllegalArgumentException("turnos fuera de rango");
        }

        enumClima clima = enumClima.valueOf(texto(campos, "clima"));
        clsEcosistema eco = new clsEcosistema(clima, total);
        eco.setTurnoActual(turno);
        eco.setTotalLobosCreados(entero(campos, "lobosCreados"));
        eco.setIntervencionRealizada(Boolean.parseBoolean(texto(campos, "intervencionRealizada")));

        for (enumTipo t : enumTipo.values()) {
            eco.setNacimientos(t, entero(campos, "nacimientos." + t.name()));
            eco.setMuertes(t, entero(campos, "muertes." + t.name()));
            eco.setSiguienteId(t, entero(campos, "siguienteId." + t.name()));
            String[] par = par(texto(campos, "longeva." + t.name()));
            eco.setLongeva(t, par[0], Integer.parseInt(par[1]));
        }
        String[] mejor = par(texto(campos, "mejorLobo"));
        eco.setMejorLobo(mejor[0], Integer.parseInt(mejor[1]));

        List<Integer> actividad = new ArrayList<>();
        String act = texto(campos, "actividad");
        if (!act.trim().isEmpty()) {
            for (String n : act.split(",")) actividad.add(Integer.parseInt(n.trim()));
        }
        eco.setActividadPorTurno(actividad);
        eco.setEventosTurno(eventos);
        eco.setHistorial(historial);

        for (clsEntidad e : entidades) eco.restaurarEntidad(e);
        return eco;
    }

    private boolean esLineaDeEntidad(String linea) {
        for (enumTipo t : enumTipo.values()) {
            if (linea.startsWith(t.name() + "|")) return true;
        }
        return false;
    }

    private clsEntidad leerEntidad(String linea) {
        String[] p = linea.split("\\|", -1);
        enumTipo tipo = enumTipo.valueOf(p[0]);
        int id = Integer.parseInt(p[1]);
        String nombre = p[2];
        double energia = Double.parseDouble(p[3]);
        int edad = Integer.parseInt(p[4]);
        if (energia < 0 || edad < 0) throw new IllegalArgumentException("valores negativos en " + nombre);

        switch (tipo) {
            case PLANTA:
                esperar(p, 6, linea);
                return new clsPlanta(id, Integer.parseInt(p[5]), nombre, energia, edad, true);
            case CONEJO:
                esperar(p, 7, linea);
                return new clsConejo(id, Integer.parseInt(p[5]), Double.parseDouble(p[6]), nombre, energia, edad, true);
            case LOBO:
                esperar(p, 8, linea);
                return new clsLobo(id, Integer.parseInt(p[7]), Integer.parseInt(p[5]), Double.parseDouble(p[6]),
                        nombre, energia, edad, true);
            default:
                throw new IllegalArgumentException("tipo desconocido");
        }
    }

    private void esperar(String[] partes, int cantidad, String linea) {
        if (partes.length != cantidad) throw new IllegalArgumentException("línea incompleta: " + linea);
    }

    private String texto(Map<String, String> campos, String clave) {
        String v = campos.get(clave);
        if (v == null) throw new IllegalArgumentException("falta el dato '" + clave + "'");
        return v;
    }

    private int entero(Map<String, String> campos, String clave) {
        int n = Integer.parseInt(texto(campos, clave).trim());
        if (n < 0) throw new IllegalArgumentException("'" + clave + "' no puede ser negativo");
        return n;
    }

    private String[] par(String valor) {
        String[] p = valor.split("\\|", -1);
        if (p.length != 2) throw new IllegalArgumentException("dato mal formado: " + valor);
        return p;
    }
}
