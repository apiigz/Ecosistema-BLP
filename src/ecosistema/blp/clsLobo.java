/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecosistema.blp;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import java.util.ArrayList;

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
    
    //getters y setters
    public int getExitosCaza() {return exitosCaza;};
    public void setExitosCaza(int exitosCaza) {this.exitosCaza = exitosCaza;};
    
    @Override
    public void comer(clsEcosistema eco){
        List<clsConejo> conejos = eco.getConejos();
        
        //prompt ia: "cómo puedo elegir un indice aleatorio de una lista de objetos en Java?"
        if (!conejos.isEmpty()){
            int indiceConejoAleatorio = ThreadLocalRandom.current().nextInt(conejos.size());
            clsConejo conejoAleatorio = conejos.get(indiceConejoAleatorio);
            
            //Lógica energía
            
            //Escenario 1: El conejo tiene más energía (tiene más chances de escapar)
            if (conejoAleatorio.getEnergia() > this.getEnergia()){
                if (Math.random() < 0.7){
                    this.setEnergia(this.getEnergia() - 15);
                }
                else{
                    this.setEnergia(this.getEnergia() + conejoAleatorio.getEnergia());
                    conejoAleatorio.morir();
                    this.setExitosCaza(this.getExitosCaza() + 1);
                }
            }
            
            //Escenario 2: El conejo tiene menos energía (tiene menos chances de escapar)
            if (conejoAleatorio.getEnergia() > this.getEnergia()){
                if (Math.random() > 0.7){
                    this.setEnergia(this.getEnergia() + conejoAleatorio.getEnergia());
                    conejoAleatorio.morir();
                    this.setExitosCaza(this.getExitosCaza() + 1);
                }
                else{
                    this.setEnergia(this.getEnergia() - 15);
                }
            }
            
            //Escenario 3: El conejo y el lobo tienen la misma energía (el conejo tiene 50% de chances de escapar)
            if (conejoAleatorio.getEnergia() > this.getEnergia()){
                if (Math.random() < 0.5){
                    this.setEnergia(this.getEnergia() - 15);
                }
                else{
                    this.setEnergia(this.getEnergia() + conejoAleatorio.getEnergia());
                    conejoAleatorio.morir();
                    this.setExitosCaza(this.getExitosCaza() + 1);
                }
            }
        }  
    }
    
    @Override
    public void mostrarEstado(){
        System.out.println("Nombre: " + this.getNombre() + "Energia: " + this.getEnergia() + "Cacerias exitosas: " + this.getExitosCaza());
    }
    
    @Override
    public void actuar(clsEcosistema eco){
        comer(eco);
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
