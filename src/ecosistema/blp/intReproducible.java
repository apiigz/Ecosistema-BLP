/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ecosistema.blp;

/**
 *
 * @author pazga
 */
public interface intReproducible {
    clsEcosistema reproducirse();
    boolean puedeReproducirse();
    
    default void intentarReproduccion(clsEcosistema eco){
        if (puedeReproducirse()){
            reproducirse();
        }
        else{
            System.out.println("No puede reproducirse");
        }
    }
}
