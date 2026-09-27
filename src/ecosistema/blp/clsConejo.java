/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecosistema.blp;
import java.util.List;
import java.util.ArrayList;

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
        List<clsPlanta> plantas = eco.getPlantas();
        boolean comio = false;
        
        // en C# era (foreach (tipo variable in coleccion), y lo más parecido en Java es esto
        for(clsPlanta planta:plantas){ // prompt ia: "se puede aplicar un bucle foreach en Java, como lo es el bucle foreach C# (términos de lógica y sintaxis)?"
            if (planta.getViva()){
                int energiaGanada = planta.serComida();
                this.setEnergia(this.getEnergia() + energiaGanada);
                comio = true;
                System.out.println(this.getNombre() + " comió a " + planta.getNombre());
                break;
            }
        }
        if (!comio){
            this.setEnergia(this.getEnergia() - 15);
        }
        
        if (this.getEnergia() <= 0){
            morir();
        }
    }
    
    @Override
    public void mostrarEstado(){
        if (this.getEnergia() < 20){
            System.out.println("Nombre: " + getNombre() + "Energia: " + getEnergia() + "Peligro: ¡¡Peligro!!");
        }
        else{
            System.out.println("Nombre: " + getNombre() + "Energia: " + getEnergia() + "Peligro: Sin peligro :)");
        }
    }
    
    @Override
    public void actuar(clsEcosistema eco){
        comer(eco);
        reproducirse(eco);
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
    
    @Override
    public boolean puedeReproducirse(clsEcosistema eco){
        double energia = this.getEnergia();
        if(energia >= 60){
            return true;
        }
        else{
            return false;
        }
    }
    
    @Override
    public void reproducirse(clsEcosistema eco){
        List<clsConejo> conejos = eco.getConejos();
        
        if (puedeReproducirse(eco) && conejos.size() >= 2){
            int nuevaVelocidad;
            double nuevoPeso = 50;
            String nuevoNombre = ("Conejo " + (conejos.size() + 1));
            double nuevaEnergia = 100;
            int nuevaEdad = 1;
            boolean nuevoViva = true;
            
            if (Math.random() < 0.5){
                nuevaVelocidad = this.getVelocidad();
            }
            else{
                int posicionConejoPadre = conejos.indexOf(this);
                
                int posicionConejoMadre = posicionConejoPadre + 1;
                
                if (posicionConejoMadre >= conejos.size()){
                    posicionConejoMadre = 0;
                }
                
                clsConejo madre = conejos.get(posicionConejoMadre);
                nuevaVelocidad = madre.getVelocidad();
            }
            
            conejos.add(new clsConejo(nuevaVelocidad, nuevoPeso, nuevoNombre, nuevaEnergia, nuevaEdad, nuevoViva));
        }
    }
}
