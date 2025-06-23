package main;

import modelo.*;
import algoritmos.AlgoritmosDeGrafos;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        CiudadGrafo grafo = new CiudadGrafo();

        // Agregar zonas
        grafo.agregarZona("Zona A");
        grafo.agregarZona("Zona B");
        grafo.agregarZona("Zona C");
        grafo.agregarZona("Zona D"); // sin conexión

        // Agregar vías
        grafo.agregarVia("Zona A", "Zona B", 10);
        grafo.agregarVia("Zona B", "Zona C", 5);
        grafo.agregarVia("Zona C", "Zona A", 8); // ciclo

        // Insertar elementos en árboles B+
        Zona zonaA = grafo.getZona("Zona A");
        Zona zonaB = grafo.getZona("Zona B");

        zonaA.getArbolBMas().insert("Parque", "Parque Central");
        zonaA.getArbolBMas().insert("Hospital", "Hospital Sur");

        zonaB.getArbolBMas().insert("Comercial", "Centro Comercial Norte");
        zonaB.getArbolBMas().insert("Parque", "Parque Infantil");

        // Mostrar árbol B+ de zonaB
        System.out.println("\nElementos en Zona B:");
        zonaB.getArbolBMas().printLeaves();

        // Buscar en árbol
        System.out.println("\nBuscar 'Hospital' en Zona A: " + zonaA.getArbolBMas().search("Hospital"));

        // Ejecutar Dijkstra
        System.out.println("\nRutas normales desde Zona A:");
        Map<Zona, Integer> distanciasAntes = AlgoritmosDeGrafos.dijkstra(grafo, zonaA);
        for (Zona z : distanciasAntes.keySet()) {
            System.out.println(" → " + z.getNombre() + ": " + (distanciasAntes.get(z) == Integer.MAX_VALUE ? "Inaccesible" : distanciasAntes.get(z)));
        }

        // Simulación: cerrar vía
        System.out.println("\nCerrando vía: Zona A → Zona B");
        grafo.eliminarVia("Zona A", "Zona B");

        // Recalcular rutas
        System.out.println("\nRutas luego del cierre desde Zona A:");
        Map<Zona, Integer> distanciasDespues = AlgoritmosDeGrafos.dijkstra(grafo, zonaA);
        for (Zona z : distanciasDespues.keySet()) {
            System.out.println(" → " + z.getNombre() + ": " + (distanciasDespues.get(z) == Integer.MAX_VALUE ? "Inaccesible" : distanciasDespues.get(z)));
        }

        // BFS y DFS
        System.out.println();
        AlgoritmosDeGrafos.bfs(grafo, zonaA);
        AlgoritmosDeGrafos.dfs(grafo, zonaA);

        // Ciclos
        System.out.println("\n¿El grafo tiene ciclos? " + (AlgoritmosDeGrafos.tieneCiclos(grafo) ? "Sí" : "No"));

        // Componentes conexas
        List<Set<Zona>> componentes = AlgoritmosDeGrafos.componentesConexas(grafo);
        System.out.println("\nComponentes conexas:");
        int i = 1;
        for (Set<Zona> comp : componentes) {
            System.out.print("Componente " + i++ + ": ");
            for (Zona z : comp) {
                System.out.print(z.getNombre() + " ");
            }
            System.out.println();
        }

        // Zonas aisladas
        List<Zona> aisladas = AlgoritmosDeGrafos.zonasAisladas(grafo);
        System.out.println("\nZonas aisladas:");
        if (aisladas.isEmpty()) {
            System.out.println("No hay zonas aisladas.");
        } else {
            for (Zona z : aisladas) {
                System.out.println(" → " + z.getNombre());
            }
        }
    }
}
