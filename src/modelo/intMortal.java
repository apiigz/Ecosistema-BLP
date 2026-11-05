package modelo;

public interface intMortal {
    boolean estaVivo();
    void morir();

    default void verificarMuerte(double energia) {
        if (energia <= 0 && estaVivo()) {
            morir();
        }
    }
}
