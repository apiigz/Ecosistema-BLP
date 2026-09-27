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
public class clsEcosistema {
    private List<clsPlanta> plantas = new ArrayList<>();
    private List<clsConejo> conejos = new ArrayList<>();
    private List<clsLobo> lobos = new ArrayList<>();
    private enumClima climaActual;
    private int turnoActual;
    
    public void procesarTurno(){
        turnoActual =+ 1;
    }
    
    public void mostrarEstado(){
        System.out.println("Es...");
    }
    
    public void agregarEntidad(String tipo){
        System.out.println("Agregado: " + tipo);
    }
    
    public void cambiarClima (enumClima nuevo){
        climaActual = nuevo;
    }
    
    public void ecosistemaColapsado(){
        if (plantas.isEmpty() || conejos.isEmpty() || lobos.isEmpty()){
            System.out.println("El ecosistema colapso. Fin de la simulación");
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
