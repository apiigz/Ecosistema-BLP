/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecosistema.blp;

/**
 *
 * @author pazga
 */
public class clsLobo extends clsAnimal {
    private int exitosCaza;
    
    public clsLobo(int exitosCaza, int velocidad, double peso, String nombre, double energia, int edad, boolean viva){
        this.exitosCaza = exitosCaza;
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
}
