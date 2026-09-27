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
public class clsPlanta extends clsEntidad implements intReproducible {
    private int tamanio;
    
    public clsPlanta(int tamanio, String nombre, double energia, int edad, boolean viva){
        this.tamanio = tamanio;
        super(nombre, energia, edad, viva);
    }
    
    //getters y setters
    public int getTamanio() {return tamanio;};
    public void getTamanio(int tamanio){
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
        if (!eco.getClimaActual().equals("SEQUIA") || !eco.getClimaActual().equals("INVIERNO")){
            return false;
        }
        else{
            return true;
        }
    }
    
    @Override
    public void reproducirse(clsEcosistema eco){
        List<clsPlanta> plantas = eco.getPlantas();
        
        if (puedeReproducirse(eco) && plantas.size() >= 0){
            int nuevoTamanio;
            String nuevoNombre = ("Planta " + (plantas.size() + 1));
            double nuevaEnergia = 1;
            int nuevaEdad = 1;
            boolean nuevoViva = true;
            
            if (Math.random() < 0.9){
                nuevoTamanio = this.getTamanio();
            }
            else{
                //Esto es igual a C#...
                Random random = new Random();
                nuevoTamanio = random.nextInt(5) + 1;
            }
            
            plantas.add(new clsPlanta(nuevoTamanio, nuevoNombre, nuevaEnergia, nuevaEdad, nuevoViva));
        }
    }
}
