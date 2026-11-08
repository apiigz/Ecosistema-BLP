package modelo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class clsLobo extends clsAnimal {
    private int exitosCaza;

    public clsLobo(int id, int exitosCaza, int velocidad, double peso, String nombre, double energia, int edad, boolean viva) {
        super(id, velocidad, peso, nombre, energia, edad, viva);
        this.exitosCaza = exitosCaza;
    }

    public int getExitosCaza() { return exitosCaza; }
    public void setExitosCaza(int exitosCaza) { this.exitosCaza = exitosCaza; }

    @Override
    public enumTipo getTipo() { return enumTipo.LOBO; }

    @Override
    public String getDetalle() { return super.getDetalle() + " | Cacerías " + exitosCaza; }

    @Override
    public void comer(clsEcosistema eco) {
        List<clsConejo> presas = eco.getConejosVivos();   // solo conejos que siguen vivos
        if (presas.isEmpty()) return;

        clsConejo presa = presas.get(ThreadLocalRandom.current().nextInt(presas.size()));

        double probabilidad = (getEnergia() / 100.0) * 0.7;
        if (eco.getClimaActual() == enumClima.INVIERNO) {
            probabilidad += 0.20;
        }

        if (Math.random() < probabilidad) {
            setEnergia(getEnergia() + presa.getEnergia());
            presa.setCausaMuerte("cazado por " + getNombre());
            presa.morir();
            exitosCaza += 1;
            eco.registrarEvento(clsEcosistema.CAT_MORTALIDAD, getNombre() + " cazó a " + presa.getNombre());
        } else {
            setEnergia(getEnergia() - 15);
            eco.registrarEvento(presa.getNombre() + " se safó de " + getNombre());
        }
    }

    @Override
    public void actuar(clsEcosistema eco) { comer(eco); }

    @Override
    public void morir() {
        setViva(false);
        setEnergia(0);
    }

    @Override
    public boolean estaVivo() { return getViva(); }
}
