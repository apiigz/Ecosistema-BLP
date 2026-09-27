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
    void reproducirse(clsEcosistema eco);
    boolean puedeReproducirse(clsEcosistema eco);
    
    default void intentarReproduccion(clsEcosistema eco){
        if (puedeReproducirse(eco)){
            reproducirse(eco);
        }
        else{
            System.out.println("No puede reproducirse");
        }
    }
}
