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
public class clsEcosistema {
    private List<clsPlanta> plantas = new ArrayList<>();
    private List<clsConejo> conejos = new ArrayList<>();
    private List<clsLobo> lobos = new ArrayList<>();
    private enumClima climaActual;
    private int turnoActual;
    
    //getters y setters
    public List<clsPlanta> getPlantas() {return plantas;};
    //necesito que me devuelva la cantidad de plantas, así que hago otro getter (haré lo mismo con los otros):
    public int getCantidadPlantas(){
        return plantas.size();
    }
    public void setPlantas(){
        if (getCantidadPlantas() <= 0){
            ecosistemaColapsado();
            this.plantas = plantas;
        }
        else{
            this.plantas = plantas;
        }
    }
    
    public List<clsConejo> getConejos() {return conejos;};
    public int getCantidadConejos(){
        return conejos.size();
    }
    public void setConejos(){
        if(getCantidadConejos() == 0){
            ecosistemaColapsado();
            this.conejos = conejos;
        }
        else{
            this.conejos = conejos;
        }
    }
    
    public List<clsLobo> getLobos() {return lobos;};
    public int getCantidadLobos(){
        return lobos.size();
    }
    public void setLobos(){
        if(getCantidadLobos() == 0){
            ecosistemaColapsado();
            this.lobos = lobos;
        }
        else{
            this.lobos = lobos;
        }
    }
    
    public enumClima getClimaActual() {return climaActual;};
    public String getNombreClimaActual(){return this.climaActual.name();};
    public void setClimaActual(enumClima climaActual){this.climaActual = climaActual;};
    public void setClimaAleatorio(){
        Random random = new Random();
        enumClima[] todosLosClimas = enumClima.values();
        
        int indiceAleatorio = random.nextInt(todosLosClimas.length);
        this.climaActual = todosLosClimas[indiceAleatorio];
    }
    
    public void procesarTurno(){
        turnoActual =+ 1;
    }
    
    public void mostrarEstado(){
        System.out.println("Clima actual: " + getNombreClimaActual() + "Cantidad plantas: " + getCantidadPlantas() + "Cantidad conejos: " + getCantidadConejos() + "Cantidad lobos: " + getCantidadLobos());
    }
    
    public void agregarEntidad(String tipo){
        String entidadElegida = tipo.toLowerCase();
        Random random = new Random();
        
        if (entidadElegida.contains("lobo")){
            int exitosCaza = 0;
            int velocidad = random.nextInt(100 - 50 + 1);
            double peso = random.nextInt(70 - 30 + 1);
            String nombre = ("Lobo " + (lobos.size() + 1));
            double energia = random.nextInt(100 - 60 + 1);
            int edad = random.nextInt(15 - 9 + 1);
            boolean viva = true;
            
            clsLobo loboNuevo = new clsLobo(exitosCaza, velocidad, peso, nombre, energia, edad, viva);
            lobos.add(loboNuevo);
        }
        else if (entidadElegida.contains("conejo")){
            int velocidad = random.nextInt(80 - 50 + 1);;
            double peso = random.nextInt(7 - 3 + 1);
            String nombre = ("Conejo " + (conejos.size() + 1));
            double energia = random.nextInt(90-50 + 1);
            int edad = random.nextInt(4 - 3 + 1);
            boolean viva = true;
            
            clsConejo conejoNuevo = new clsConejo(velocidad, peso, nombre, energia, edad, viva);
            conejos.add(conejoNuevo);
        }
        else if (entidadElegida.contains("planta")){
            int tamanio = random.nextInt(5 - 4 + 1);
            String nombre = ("Planta " + (plantas.size() + 1));
            double energia = 1;
            int edad = random.nextInt(10 - 9 + 1);
            boolean viva = true;
            
            clsPlanta nuevaPlanta = new clsPlanta(tamanio, nombre, energia, edad, viva);
            plantas.add(nuevaPlanta);
        }
        else{
            System.out.println("Introduzca el nombre de una entidad para agregar");
            return;
        }
    }
    
    public void cambiarClima (enumClima nuevo){
        this.climaActual = nuevo;
    }
    
    public boolean ecosistemaColapsado(){
        if (plantas.isEmpty() || conejos.isEmpty() || lobos.isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }
    
    public void generarReporteFinal(){
        String clAct = climaActual.name();
        String tnAct = String.valueOf(turnoActual);
        
        System.out.println("Resumen completo:");
        System.out.println("Lobos restantes: " + lobos.size());
        System.out.println("Conejos restantes: " + conejos.size());
        System.out.println("Plantas restantes: " + plantas.size());
        System.out.println("Clima actual: " + clAct);
        System.out.println("Cantidad de turnos: " + tnAct);
    }
}
