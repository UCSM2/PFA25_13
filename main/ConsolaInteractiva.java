package main;

import modelo.*;
import modelo.bplus.ElementoUrbanistico;
import algoritmos.AlgoritmosDeGrafos;
import java.util.*;

public class ConsolaInteractiva {
    private static CiudadGrafo grafo = new CiudadGrafo();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        inicializarDemo();

        int opcion;
        do {
            System.out.println("\n--- Sistema de Zonas Urbanas ---");
            System.out.println("1. Ver zonas y conexiones");
            System.out.println("2. Ver elementos en una zona");
            System.out.println("3. Agregar elemento a una zona");
            System.out.println("4. Ejecutar Dijkstra desde una zona");
            System.out.println("5. Cerrar una vía");
            System.out.println("6. Ver zonas aisladas");
            System.out.println("7. Ver componentes conexas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> mostrarConexiones();
                case 2 -> mostrarElementosZona();
                case 3 -> agregarElementoZona();
                case 4 -> ejecutarDijkstra();
                case 5 -> cerrarVia();
                case 6 -> mostrarZonasAisladas();
                case 7 -> mostrarComponentesConexas();
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private static void inicializarDemo() {
        grafo.agregarZona("Zona A");
        grafo.agregarZona("Zona B");
        grafo.agregarZona("Zona C");
        grafo.agregarZona("Zona D");
        grafo.agregarVia("Zona A", "Zona B", 10);
        grafo.agregarVia("Zona B", "Zona C", 5);
        grafo.agregarVia("Zona C", "Zona A", 8);
    }

    private static void mostrarConexiones() {
        for (Zona z : grafo.getZonas()) {
            System.out.println(z.getNombre() + " -> ");
            for (Zona destino : z.getConexiones().keySet()) {
                System.out.println("  -> " + destino.getNombre() + " (" + z.getConexiones().get(destino) + ")");
            }
        }
    }

    private static void mostrarElementosZona() {
        System.out.print("Nombre de la zona: ");
        Zona z = grafo.getZona(scanner.nextLine());
        if (z != null) {
            System.out.println("Elementos:");
            z.getArbolBMas().printLeaves();
        } else {
            System.out.println("Zona no encontrada.");
        }
    }

    private static void agregarElementoZona() {
        System.out.print("Nombre de la zona: ");
        Zona z = grafo.getZona(scanner.nextLine());
        if (z != null) {
            System.out.print("Tipo (ej: Parque, Edificio): ");
            String tipo = scanner.nextLine();
            System.out.print("Nombre del elemento: ");
            String nombre = scanner.nextLine();
            ElementoUrbanistico elemento = new ElementoUrbanistico(tipo, nombre);
            z.getArbolBMas().insert(tipo, elemento);
            System.out.println("Elemento insertado correctamente.");
        } else {
            System.out.println("Zona no encontrada.");
        }
    }

    private static void ejecutarDijkstra() {
        System.out.print("Zona de inicio: ");
        Zona origen = grafo.getZona(scanner.nextLine());
        if (origen != null) {
            Map<Zona, Integer> distancias = AlgoritmosDeGrafos.dijkstra(grafo, origen);
            System.out.println("Distancias desde " + origen.getNombre() + ":");
            for (Zona z : distancias.keySet()) {
                int d = distancias.get(z);
                System.out.println(" -> " + z.getNombre() + ": " + (d == Integer.MAX_VALUE ? "Inaccesible" : d));
            }
        } else {
            System.out.println("Zona no encontrada.");
        }
    }

    private static void cerrarVia() {
        System.out.print("Origen: ");
        String origen = scanner.nextLine();
        System.out.print("Destino: ");
        String destino = scanner.nextLine();
        grafo.eliminarVia(origen, destino);
        System.out.println("Vía cerrada.");
    }

    private static void mostrarZonasAisladas() {
        List<Zona> aisladas = AlgoritmosDeGrafos.zonasAisladas(grafo);
        System.out.println("Zonas aisladas:");
        if (aisladas.isEmpty()) {
            System.out.println("No hay zonas aisladas.");
        } else {
            for (Zona z : aisladas) {
                System.out.println(" -> " + z.getNombre());
            }
        }
    }

    private static void mostrarComponentesConexas() {
        List<Set<Zona>> componentes = AlgoritmosDeGrafos.componentesConexas(grafo);
        int i = 1;
        for (Set<Zona> comp : componentes) {
            System.out.print("Componente " + i++ + ": ");
            for (Zona z : comp) {
                System.out.print(z.getNombre() + " ");
            }
            System.out.println();
        }
    }
}
