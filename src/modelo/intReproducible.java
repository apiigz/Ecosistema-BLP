package modelo;

public interface intReproducible {
    void reproducirse(clsEcosistema eco);
    boolean puedeReproducirse(clsEcosistema eco);

    default void intentarReproduccion(clsEcosistema eco) {
        if (puedeReproducirse(eco)) {
            reproducirse(eco);
        }
    }
}
