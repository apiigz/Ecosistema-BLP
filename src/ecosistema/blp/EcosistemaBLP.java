/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ecosistema.blp;
import java.util.Scanner;

/**
 *
 * @author pazga
 */
public class EcosistemaBLP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1. CONFIGURACIÓN INICIAL
        System.out.println("=== CONFIGURACIÓN DEL ECOSISTEMA ===");
        int cantPlantas = pedirNumero(sc, "Cantidad inicial de plantas", 5, 30);
        int cantConejos = pedirNumero(sc, "Cantidad inicial de conejos", 2, 15);
        int cantLobos = pedirNumero(sc, "Cantidad inicial de lobos", 1, 5);
        int totalTurnos = pedirNumero(sc, "Cantidad total de turnos", 10, 50);

        System.out.println("\nSeleccione el clima inicial:");
        System.out.println("1. Soleado | 2. Lluvioso | 3. Sequía | 4. Invierno");
        int opcionClima = pedirNumero(sc, "Opción", 1, 4);

        enumClima climaInicial = enumClima.SOLEADO;
        if (opcionClima == 2) climaInicial = enumClima.LLUVIOSO;
        else if (opcionClima == 3) climaInicial = enumClima.SEQUIA;
        else if (opcionClima == 4) climaInicial = enumClima.INVIERNO;

        // Confirmar configuración
        System.out.println("\n--- Resumen de Configuración ---");
        System.out.println("Plantas: " + cantPlantas + " | Conejos: " + cantConejos + " | Lobos: " + cantLobos);
        System.out.println("Clima: " + climaInicial.name() + " | Turnos: " + totalTurnos);
        System.out.print("¿Confirmar e iniciar simulación? (S/N): ");
        String confirmacion = sc.next();
        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Simulación cancelada.");
            return;
        }

        // Instanciar y poblar ecosistema
        clsEcosistema eco = new clsEcosistema();
        eco.setClimaActual(climaInicial);

        for (int i = 0; i < cantPlantas; i++) eco.agregarEntidad("planta");
        for (int i = 0; i < cantConejos; i++) eco.agregarEntidad("conejo");
        for (int i = 0; i < cantLobos; i++) eco.agregarEntidad("lobo");

        sc.nextLine(); // Consumir salto de línea residual

        // 2. LOOP PRINCIPAL DE SIMULACIÓN
        int turno = 0;
        while (turno < totalTurnos && !eco.ecosistemaColapsado()) {
            turno++;
            System.out.println("\n========================================");
            System.out.println("            TURNO " + turno);
            System.out.println("========================================");

            eco.procesarTurno();
            eco.mostrarEstado();

            // 3. SISTEMA DE INTERVENCIÓN (cada 3 turnos)
            if (turno % 3 == 0 && turno < totalTurnos && !eco.ecosistemaColapsado()) {
                menuIntervencion(eco, sc);
            }

            // Esperar Enter para el próximo turno
            System.out.print("\nPresione [ENTER] para avanzar al siguiente turno...");
            sc.nextLine();
        }

        // 4. REPORTE FINAL
        System.out.println("\n========================================");
        System.out.println("        REPORTE FINAL DE SIMULACIÓN     ");
        System.out.println("========================================");

        if (eco.ecosistemaColapsado()) {
            System.out.println("Causa de fin: COLAPSO DEL ECOSISTEMA");
            if (eco.getCantidadPlantas() == 0) System.out.println("Población extinguida: PLANTAS");
            if (eco.getCantidadConejos() == 0) System.out.println("Población extinguida: CONEJOS");
            if (eco.getCantidadLobos() == 0) System.out.println("Población extinguida: LOBOS");
        } else {
            System.out.println("Causa de fin: Turnos completados exitosamente (" + totalTurnos + " turnos)");
        }

        // Lobo con más cacerías exitosas
        clsLobo mejorLobo = null;
        for (clsLobo l : eco.getLobos()) {
            if (mejorLobo == null || l.getExitosCaza() > mejorLobo.getExitosCaza()) {
                mejorLobo = l;
            }
        }
        if (mejorLobo != null) {
            System.out.println("Lobo más exitoso: " + mejorLobo.getNombre() + " (" + mejorLobo.getExitosCaza() + " cazas)");
        } else {
            System.out.println("No quedaron lobos vivos para evaluar cacerías.");
        }

        eco.generarReporteFinal();
    }
    
    public static int pedirNumero(Scanner scanner, String mensaje, int min, int max) {
        int valor;
        do {
            System.out.print(mensaje + " (" + min + " - " + max + "): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Entrada inválida. Ingrese un número entero.");
                scanner.next();
            }
            valor = scanner.nextInt();
        } while (valor < min || valor > max);
        return valor;
    }

    private static void menuIntervencion(clsEcosistema eco, Scanner sc) {
        System.out.println("\n--- INTERVENCIÓN DIVINA (Turno divisible por 3) ---");
        System.out.println("1. Cambiar Clima");
        System.out.println("2. Agregar Entidad");
        System.out.println("3. Solo avanzar");
        int op = pedirNumero(sc, "Elija una acción", 1, 3);

        if (op == 1) {
            System.out.println("1. Soleado | 2. Lluvioso | 3. Sequía | 4. Invierno");
            int c = pedirNumero(sc, "Nuevo clima", 1, 4);
            enumClima nuevo = enumClima.SOLEADO;
            if (c == 2) nuevo = enumClima.LLUVIOSO;
            else if (c == 3) nuevo = enumClima.SEQUIA;
            else if (c == 4) nuevo = enumClima.INVIERNO;

            System.out.print("¿Confirma cambiar el clima a " + nuevo.name() + "? (S/N): ");
            if (sc.next().equalsIgnoreCase("s")) {
                eco.cambiarClima(nuevo);
                System.out.println("Clima modificado.");
            }
            sc.nextLine();
        } else if (op == 2) {
            System.out.println("1. Planta | 2. Conejo | 3. Lobo");
            int ent = pedirNumero(sc, "Tipo de entidad", 1, 3);

            if (ent == 3 && eco.totalLobosAgregados >= 5) {
                System.out.println("No se pueden agregar más de 5 lobos en total durante la simulación.");
                sc.nextLine();
                return;
            }

            System.out.print("¿Confirma agregar esta entidad? (S/N): ");
            if (sc.next().equalsIgnoreCase("s")) {
                if (ent == 1) eco.agregarEntidad("planta");
                else if (ent == 2) eco.agregarEntidad("conejo");
                else if (ent == 3) {
                    eco.agregarEntidad("lobo");
                    eco.totalLobosAgregados++;
                }
                System.out.println("Entidad agregada al ecosistema.");
            }
            sc.nextLine();
        } else {
            System.out.println("Avanzando sin cambios...");
            sc.nextLine();
        }
    }
}
