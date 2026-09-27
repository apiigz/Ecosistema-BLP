/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ecosistema.blp;

/**
 *
 * @author pazga
 */
public interface intMortal {
    boolean estaVivo();
    void morir();
    
    default void verificarMuerte(double energia){
        if (energia <= 0 && !estaVivo()){
            morir();
            System.out.println("Murió...");
        }
    }
}
