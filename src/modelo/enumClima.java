package modelo;

import java.text.Normalizer;

/** Climas posibles del ecosistema (mínimo 3 según la consigna; acá hay 4). */
public enum enumClima {
    SOLEADO("Soleado"), LLUVIOSO("Lluvioso"), SEQUIA("Sequía"), INVIERNO("Invierno");

    private final String etiqueta;

    enumClima(String etiqueta) { this.etiqueta = etiqueta; }

    /** Texto lindo para mostrar en pantalla. */
    public String getEtiqueta() { return etiqueta; }

    /** Convierte el texto de un combo en el clima. Devuelve null si no coincide. */
    public static enumClima desdeEtiqueta(String texto) {
        if (texto == null) return null;
        String buscado = normalizar(texto);
        for (enumClima c : values()) {
            if (normalizar(c.etiqueta).equals(buscado) || c.name().equalsIgnoreCase(texto.trim())) return c;
        }
        return null;
    }

    private static String normalizar(String s) {
        return Normalizer.normalize(s.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
    }
}
