/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecosistema.blp;

/**
 *
 * @author pazga
 */
public abstract class clsAnimal extends clsEntidad implements intMortal {
    private int velocidad;
    private double peso;
    
    public clsAnimal(int velocidad, double peso, String nombre, double energia, int edad, boolean viva){
        this.velocidad = velocidad;
        this.peso = peso;
        super(nombre, energia, edad, viva);
    }
    
    //getters y setters
    
    public int getVelocidad() {return velocidad;};
    public void setVelocidad(int velocidad) {this.velocidad = velocidad;};
    
    public double getPeso() {return peso;};
    public void setPeso(double peso) {this.peso = peso;};
    
    //método abstracto
    
    public abstract void comer(clsEcosistema eco);
    
    //método concreto
    public void moverse(){
        System.out.println("El " + getNombre() + "se movió."); //después le agrego una lógica con un RAND para que hayan más dialogos y se vea re lindo cc
    }
}
