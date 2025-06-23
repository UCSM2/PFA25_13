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

    public static boolean tieneCiclos(CiudadGrafo grafo) {
        Set<Zona> visitados = new HashSet<>();
        Set<Zona> enRecorrido = new HashSet<>();

        for (Zona zona : grafo.getZonas()) {
            if (!visitados.contains(zona)) {
                if (dfsCiclo(zona, visitados, enRecorrido)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean dfsCiclo(Zona actual, Set<Zona> visitados, Set<Zona> enRecorrido) {
        visitados.add(actual);
        enRecorrido.add(actual);

        for (Zona vecino : actual.getConexiones().keySet()) {
            if (!visitados.contains(vecino)) {
                if (dfsCiclo(vecino, visitados, enRecorrido)) {
                    return true;
                }
            } else if (enRecorrido.contains(vecino)) {
                return true;
            }
        }

        enRecorrido.remove(actual);
        return false;
    }

    public static List<Set<Zona>> componentesConexas(CiudadGrafo grafo) {
        Set<Zona> visitados = new HashSet<>();
        List<Set<Zona>> componentes = new ArrayList<>();

        for (Zona zona : grafo.getZonas()) {
            if (!visitados.contains(zona)) {
                Set<Zona> componente = new HashSet<>();
                dfsComponente(zona, componente);
                componentes.add(componente);
                visitados.addAll(componente);
            }
        }

        return componentes;
    }

    private static void dfsComponente(Zona actual, Set<Zona> componente) {
        componente.add(actual);

        for (Zona vecino : actual.getConexiones().keySet()) {
            if (!componente.contains(vecino)) {
                dfsComponente(vecino, componente);
            }
        }

        for (Zona posibleVecino : componente) {
            if (posibleVecino.getConexiones().containsKey(actual) && !componente.contains(posibleVecino)) {
                dfsComponente(posibleVecino, componente);
            }
        }
    }

    public static List<Zona> zonasAisladas(CiudadGrafo grafo) {
        List<Zona> aisladas = new ArrayList<>();

        for (Zona z : grafo.getZonas()) {
            boolean tieneSalida = !z.getConexiones().isEmpty();
            boolean tieneEntrada = false;

            for (Zona otra : grafo.getZonas()) {
                if (otra.getConexiones().containsKey(z)) {
                    tieneEntrada = true;
                    break;
                }
            }

            if (!tieneSalida && !tieneEntrada) {
                aisladas.add(z);
            }
        }

        return aisladas;
    }
    
}
