package algoritmos;

import modelo.CiudadGrafo;
import modelo.Zona;

import java.util.*;

public class AlgoritmosDeGrafos {
    
    public static Map<Zona, Integer>dijkstra(CiudadGrafo grafo, Zona origen){
        Map<Zona, Integer> distancias = new HashMap<>();
        PriorityQueue<ZonaDistancia> cola = new PriorityQueue<>(Comparator.comparingInt(z -> z.distancia));
        Set<Zona> visitados = new HashSet<>();

        for (Zona z:grafo.getZonas()){
            distancias.put(z, Integer.MAX_VALUE);
        }
        distancias.put(origen, 0);
        cola.add(new ZonaDistancia(origen, 0));

        while (!cola.isEmpty()){
            ZonaDistancia actual = cola.poll();
            if(visitados.contains(actual.zona)) continue;
            visitados.add(actual.zona);

            for (Map.Entry<Zona, Integer> entrada: actual.zona.getConexiones().entrySet()){
                Zona vecino = entrada.getKey();
                int peso = entrada.getValue();
                int nuevaDist = distancias.get(actual.zona) + peso;

                if (nuevaDist < distancias.get(vecino)){
                    distancias.put(vecino, nuevaDist);
                    cola.add(new ZonaDistancia(vecino, nuevaDist));
                }
            }
        }

        return distancias;
    }

    private static class ZonaDistancia{
        Zona zona;
        int distancia;

        ZonaDistancia(Zona zona, int distancia){
            this.zona = zona;
            this. distancia = distancia;
        }
    }

    public static void bfs(CiudadGrafo grafo, Zona inicio){
        Set<Zona> visitados = new HashSet<>();
        Queue<Zona> cola  = new LinkedList<>();
        cola.add(inicio);
        visitados.add(inicio);

        System.out.println("Recorrido BFS desde " + inicio.getNombre() + ":");

        while (!cola.isEmpty()){
            Zona actual = cola.poll();
            System.out.println(actual.getNombre() + " → ");

            for(Zona vecino: actual.getConexiones().keySet()){
                if(!visitados.contains(vecino)){
                    cola.add(vecino);
                    visitados.add(vecino);
                }
            }
        }

        System.out.println("FIN");
    }

    public static void dfs(CiudadGrafo grafo, Zona inicio){
        Set<Zona> visitados = new HashSet<>();
        System.out.println("Recorrido DFS desde " + inicio.getNombre() + ":");
        dfsRecursivo(inicio, visitados);
        System.out.println("FIN");
    }
    private static void dfsRecursivo(Zona actual, Set<Zona> visitados) {
        System.out.print(actual.getNombre() + " → ");
        visitados.add(actual);

        for (Zona vecino : actual.getConexiones().keySet()) {
            if (!visitados.contains(vecino)) {
                dfsRecursivo(vecino, visitados);
            }
        }
    }
    
}
