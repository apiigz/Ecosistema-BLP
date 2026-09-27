/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecosistema.blp;

/**
 *
 * @author pazga
 */
public class clsConejo extends clsAnimal implements intReproducible{
    public clsConejo(int velocidad, double peso, String nombre, double energia, int edad, boolean viva){
        super(velocidad, peso, nombre, energia, edad, viva);
    }
    
    @Override
    public void comer(clsEcosistema eco){
        System.out.println("q");
    }
    
    @Override
    public void mostrarEstado(){
        System.out.println("q");
    }
    
    @Override
    public void actuar(clsEcosistema eco){
        System.out.println("q");
    }
    
    @Override
    public void morir(){
        System.out.println("mori xd");
    }
    
    @Override
    public boolean estaVivo(){
        return true;
    }
    
    @Override
    public boolean puedeReproducirse(){
        return true;
    }
    
    @Override
    public clsEcosistema reproducirse(){
        System.out.println("xd");
        return null;
    }
}
