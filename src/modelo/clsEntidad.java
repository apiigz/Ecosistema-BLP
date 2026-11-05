package modelo;

/** Clase base de todo lo que vive en el ecosistema. No interactúa con el usuario. */
public abstract class clsEntidad {
    private final int id;            // único por tipo, nunca se reutiliza (sirve para guardar/cargar)
    private String nombre;
    private double energia;
    private int edad;
    private boolean viva;
    private String causaMuerte = ""; // vacío = murió sin energía

    public clsEntidad(int id, String nombre, double energia, int edad, boolean viva) {
        this.id = id;
        this.nombre = nombre;
        this.energia = Math.max(0, energia);
        this.edad = edad;
        this.viva = viva;
    }

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getEnergia() { return energia; }
    public void setEnergia(double energia) { this.energia = Math.max(0, energia); }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public boolean getViva() { return viva; }
    public void setViva(boolean viva) { this.viva = viva; }

    public String getCausaMuerte() { return causaMuerte; }
    public void setCausaMuerte(String causaMuerte) { this.causaMuerte = causaMuerte; }

    // métodos abstractos: cada tipo los resuelve a su manera (polimorfismo)
    public abstract void actuar(clsEcosistema eco);
    public abstract enumTipo getTipo();
    /** Texto con los datos propios del tipo, para mostrar en la tabla. */
    public abstract String getDetalle();

    // método concreto: +1 de edad y -1 de energía base
    public void envejecer() {
        edad += 1;
        setEnergia(energia - 1);
    }
}
