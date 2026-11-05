package modelo;

public abstract class clsAnimal extends clsEntidad implements intMortal {
    private int velocidad;
    private double peso;

    public clsAnimal(int id, int velocidad, double peso, String nombre, double energia, int edad, boolean viva) {
        super(id, nombre, energia, edad, viva);
        this.velocidad = velocidad;
        this.peso = peso;
    }

    public int getVelocidad() { return velocidad; }
    public void setVelocidad(int velocidad) { this.velocidad = velocidad; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public abstract void comer(clsEcosistema eco);

    // método concreto: ya no imprime, devuelve el texto
    public String moverse() {
        return getNombre() + " se movió.";
    }

    @Override
    public String getDetalle() {
        return "Velocidad " + velocidad + " | Peso " + (Math.round(peso * 10) / 10.0);
    }
}
