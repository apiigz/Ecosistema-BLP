package modelo;

import java.text.Normalizer;

/** Tipos de entidad del ecosistema. */
public enum enumTipo {
    PLANTA("Planta", "Plantas"), CONEJO("Conejo", "Conejos"), LOBO("Lobo", "Lobos");

    private final String etiqueta;
    private final String plural;

    enumTipo(String etiqueta, String plural) { this.etiqueta = etiqueta; this.plural = plural; }

    public String getEtiqueta() { return etiqueta; }
    public String getPlural() { return plural; }

    /** Convierte el texto de un combo en el tipo. Devuelve null si no coincide. */
    public static enumTipo desdeEtiqueta(String texto) {
        if (texto == null) return null;
        String buscado = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        for (enumTipo t : values()) {
            if (t.etiqueta.equalsIgnoreCase(buscado) || t.name().equalsIgnoreCase(buscado)) return t;
        }
        return null;
    }
}
