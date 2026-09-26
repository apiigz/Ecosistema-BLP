/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecosistema.blp;

/**
 *
 * @author pazga
 */
public class clsEntidad {
    //declaración atributois
    private String nombre;
    private double energia;
    private int edad;
    private boolean viva;
    
    //constructor con parametros
    public clsEntidad(String nombre, double energia, int edad, boolean viva){
        this.nombre = nombre;
        this.energia = energia;
        this.edad = edad;
        this.viva = viva;
    }
    
    //getters y setters
    
    //nombre
    public String getNombre() {return nombre;}
    public void setNombre (String nombre) {this.nombre = nombre;};
    
    //energia
    public double getEnergia() {return energia;};
    public void setEnergia(double energia){
        if (energia < 0){
            System.out.println("La energia no puede ser negativa");
            energia = 0;
            this.energia = energia;
        }
    }
    
    //edad
    public int setEdad() {return edad;};
    public void getEdad(int edad) {this.edad = edad;};
    
    //viva
    public boolean setViva() {return viva;};
    public void getViva(boolean viva) {this.viva = viva;};
    
    //métodos abstractos
    public abstract void actuar(clsEcosistema eco);
    public abstract void mostrarEstado();
    
    //métodos concretos
    public void envejecer(){ //=> incrementa edad (int edad) y descuenta energía base (double energia)
        int nuevaEdad = edad + 1;
        double nuevaEnergia = energia - 1;
        
        edad = nuevaEdad;
        energia = nuevaEnergia;
        //Los valores de +1 y -1 van a cambiar, creo q
    } 
}
