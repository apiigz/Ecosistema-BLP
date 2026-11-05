package modelo;

import java.util.List;

public class clsConejo extends clsAnimal implements intReproducible {

    // ---- Balance del juego -------------------------------------------------------------
    // En la IE1 cada conejo se comía una planta por turno y se reproducía siempre, y el
    // ecosistema colapsaba en ~3 turnos (nunca se llegaba a la primera intervención).
    // Estos valores lo hacen jugable. Para volver EXACTAMENTE a la regla de la IE1:
    //   ENERGIA_HAMBRE = Double.MAX_VALUE, PROB_REPRODUCCION = 1.0, COSTO_REPRODUCCION = 0, ENERGIA_BEBE = 100
    public static final double ENERGIA_HAMBRE = 60;      // el conejo solo busca plantas si tiene menos que esto
    public static final double PROB_REPRODUCCION = 0.25; // probabilidad de reproducirse cuando puede
    public static final double COSTO_REPRODUCCION = 20;  // energía que gasta el padre al reproducirse
    public static final double ENERGIA_BEBE = 60;        // energía con la que nace un conejo
    public static final double ENERGIA_PARA_REPRODUCIRSE = 60;

    public clsConejo(int id, int velocidad, double peso, String nombre, double energia, int edad, boolean viva) {
        super(id, velocidad, peso, nombre, energia, edad, viva);
    }

    @Override
    public enumTipo getTipo() { return enumTipo.CONEJO; }

    @Override
    public void comer(clsEcosistema eco) {
        if (getEnergia() >= ENERGIA_HAMBRE) return;   // sin hambre: no busca plantas
        boolean comio = false;
        for (clsPlanta planta : eco.getPlantas()) {
            if (planta.getViva()) {
                int energiaGanada = planta.serComida(getNombre());
                setEnergia(getEnergia() + energiaGanada);
                eco.registrarEvento(getNombre() + " comió a " + planta.getNombre());
                comio = true;
                break;
            }
        }
        if (!comio) {
            setEnergia(getEnergia() - 15);
            eco.registrarEvento(getNombre() + " no encontró plantas y perdió energía");
        }
        if (getEnergia() <= 0) {
            morir();
        }
    }

    @Override
    public void actuar(clsEcosistema eco) {
        comer(eco);
        reproducirse(eco);
    }

    @Override
    public void morir() {
        setViva(false);
        setEnergia(0);
    }

    @Override
    public boolean estaVivo() { return getViva(); }

    @Override
    public boolean puedeReproducirse(clsEcosistema eco) {
        return getViva() && getEnergia() >= ENERGIA_PARA_REPRODUCIRSE;
    }

    @Override
    public void reproducirse(clsEcosistema eco) {
        if (!puedeReproducirse(eco) || eco.getCantidadConejosVivos() < 2) return;
        if (Math.random() >= PROB_REPRODUCCION) return;

        List<clsConejo> conejos = eco.getConejos();
        int nuevaVelocidad;
        if (Math.random() < 0.5) {
            nuevaVelocidad = getVelocidad();
        } else {
            int posicionMadre = conejos.indexOf(this) + 1;
            if (posicionMadre >= conejos.size()) posicionMadre = 0;
            nuevaVelocidad = conejos.get(posicionMadre).getVelocidad();
        }

        int id = eco.siguienteId(enumTipo.CONEJO);
        clsConejo hijo = new clsConejo(id, nuevaVelocidad, 5.0, "Conejo " + id, ENERGIA_BEBE, 1, true);
        setEnergia(getEnergia() - COSTO_REPRODUCCION);
        eco.registrarNacimiento(hijo, this);
    }
}
