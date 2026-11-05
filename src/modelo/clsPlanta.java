package modelo;

public class clsPlanta extends clsEntidad implements intReproducible, intMortal {
    private int tamanio;

    public clsPlanta(int id, int tamanio, String nombre, double energia, int edad, boolean viva) {
        super(id, nombre, energia, edad, viva);
        this.tamanio = tamanio;
    }

    public int getTamanio() { return tamanio; }
    public void setTamanio(int tamanio) { this.tamanio = Math.max(1, Math.min(5, tamanio)); }

    /** La planta es comida: muere y devuelve la energía que aporta. */
    public int serComida(String quienLaComio) {
        setCausaMuerte("comida por " + quienLaComio);
        morir();
        return tamanio * 10;
    }

    @Override
    public enumTipo getTipo() { return enumTipo.PLANTA; }

    @Override
    public String getDetalle() { return "Tamaño " + tamanio; }

    @Override
    public void actuar(clsEcosistema eco) { reproducirse(eco); }

    @Override
    public boolean puedeReproducirse(clsEcosistema eco) {
        if (eco.getClimaActual() == enumClima.INVIERNO) return false;
        return getViva() && getEnergia() >= 10;
    }

    @Override
    public void reproducirse(clsEcosistema eco) {
        if (!puedeReproducirse(eco)) return;

        double probabilidad = 0.3;
        switch (eco.getClimaActual()) {
            case SOLEADO:  probabilidad *= 1.5; break;
            case LLUVIOSO: probabilidad *= 2.0; break;
            case SEQUIA:   probabilidad *= 0.5; break;
            default: break;
        }

        if (Math.random() < probabilidad) {
            int nuevoTamanio = (Math.random() < 0.9) ? tamanio : (int) (Math.random() * 5) + 1;
            int id = eco.siguienteId(enumTipo.PLANTA);
            clsPlanta hija = new clsPlanta(id, nuevoTamanio, "Planta " + id, 20, 0, true);
            setEnergia(getEnergia() - 10);
            eco.registrarNacimiento(hija, this);
        }
    }

    @Override
    public void morir() {
        setViva(false);
        setEnergia(0);
    }

    @Override
    public boolean estaVivo() { return getViva(); }
}
