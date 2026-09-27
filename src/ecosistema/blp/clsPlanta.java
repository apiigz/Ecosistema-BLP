/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecosistema.blp;

/**
 *
 * @author pazga
 */
public class clsPlanta extends clsEntidad implements intReproducible {
    private int tamanio;
    
    public clsPlanta(int tamanio, String nombre, double energia, int edad, boolean viva){
        this.tamanio = tamanio;
        super(nombre, energia, edad, viva);
    }
    
    //getters y setters
    @Override
    public void mostrarEstado(){
        System.out.println("xd");
    }
    
    public void actuar(clsEcosistema eco){
        System.out.println("xd");
    }
    
    public boolean puedeReproducirse(){
        return true;
    }
}
