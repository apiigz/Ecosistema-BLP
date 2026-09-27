/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecosistema.blp;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;


/**
 *
 * @author pazga
 */
public class clsPlanta extends clsEntidad implements intReproducible, intMortal {
    private int tamanio;
    
    public clsPlanta(int tamanio, String nombre, double energia, int edad, boolean viva){
        super(nombre, energia, edad, viva);
        this.tamanio = tamanio;
    }
    
    //getters y setters
    public int getTamanio() {return tamanio;};
    public void setTamanio(int tamanio){
        if (tamanio < 1){
            tamanio = 1;
            this.tamanio = tamanio;
        }
        else if(tamanio > 5){
            tamanio = 5;
            this.tamanio = tamanio;
        }
        else{
            this.tamanio = tamanio;
        }
    }
    
    public int serComida(){
        setEnergia(0);
        setViva(false);
        return this.tamanio * 10;
    }
    
    @Override
    public void mostrarEstado(){
        System.out.println("Nombre planta:" + getNombre() + "Tamaño: " + tamanio + "Energia: " +getEnergia());
    }
    
    @Override
    public void actuar(clsEcosistema eco){
        reproducirse(eco);
    }
    
    @Override
    public boolean puedeReproducirse(clsEcosistema eco){
        if (eco.getClimaActual() == enumClima.INVIERNO){
            return false;
        }
        return getViva() && getEnergia() >= 10;
    }
    
    @Override
    public void reproducirse(clsEcosistema eco){        
        if (!puedeReproducirse(eco)){
            return;
        }
        List<clsPlanta> plantas = eco.getPlantas();
        
        double probabilidadBase = 0.3; //Supongamos que es del 30%
        
        if (eco.getClimaActual() == enumClima.SOLEADO){
            probabilidadBase *= 1.5; 
        }
        else if(eco.getClimaActual() == enumClima.LLUVIOSO){
            probabilidadBase *= 2.0;
        }
        else if (eco.getClimaActual() == enumClima.SEQUIA){
            probabilidadBase *= 0.5;
        }
        
        if (Math.random() < probabilidadBase){
            
            int nuevoTamanio;
            if (Math.random() < 0.9){
                nuevoTamanio = this.getTamanio();
            }
            else{
                nuevoTamanio = (int) (Math.random() * 5) + 1;
            }
            
            String nuevoNombre = "Planta " + (plantas.size() + 1);
            double nuevaEnergia = 20;
            int nuevaEdad = 0;
            boolean nuevoViva = true;
            
            clsPlanta nuevaPlanta = new clsPlanta( nuevoTamanio, nuevoNombre, nuevaEnergia, nuevaEdad, nuevoViva);
            plantas.add(nuevaPlanta);
            
            this.setEnergia(this.getEnergia() - 10);
            
            System.out.println(getNombre() + " se reprodujo y nació: " + nuevoNombre);
        }
    }
    
    @Override
    public void morir(){
        this.setViva(false);
        this.setEnergia(0);
    }
    
    @Override
    public boolean estaVivo(){
        return this.getViva();
    }
}
