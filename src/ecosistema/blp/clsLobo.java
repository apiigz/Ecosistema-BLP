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
        super(velocidad, peso, nombre, energia, edad, viva);
        this.exitosCaza = exitosCaza;
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

            double probabilidad = (this.getEnergia() / 100.0) * 0.7; // Probabilidad b ase del 70% según 100 de energía
            
            //El bono del clima
            if (eco.getClimaActual() == enumClima.INVIERNO){
                probabilidad += 0.20;
            }
            
            //Ahora intenta cazar o lobinho
            if (Math.random() < probabilidad){
                this.setEnergia(this.getEnergia() + conejoAleatorio.getEnergia());
                conejoAleatorio.morir();
                this.setExitosCaza(this.getExitosCaza() + 1);
                System.out.println(getNombre() + " cazó con éxito a " + conejoAleatorio.getNombre());
            }
            else{
                this.setEnergia(this.getEnergia() - 15);
                System.out.println(conejoAleatorio.getNombre() + " se safó de " + this.getNombre());
            }
        }
        else{
            return;
        }
    }
    
    private void exitoCaza(clsConejo conejo){
        this.setEnergia(this.getEnergia() + conejo.getEnergia());
        conejo.morir();
        this.exitosCaza += 1;
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
        System.out.println(this.getNombre() + " se murió ");
    }
    
    @Override
    public boolean estaVivo(){
        return this.getViva();
    }
}
