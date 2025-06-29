package modelo;

import java.util.*;

public class CiudadGrafo {
    private Map<String, Zona> zonas;

    public CiudadGrafo() {
        zonas = new HashMap<>();
    }

    public void agregarZona(String nombre) {
        zonas.putIfAbsent(nombre, new Zona(nombre));
    }

    public void agregarVia(String origen, String destino, int peso) {
        Zona z1 = zonas.get(origen);
        Zona z2 = zonas.get(destino);
        if (z1 != null && z2 != null) {
            z1.conectarCon(z2, peso);
        }
    }

    public void eliminarVia(String origen, String destino) {
        Zona z1 = zonas.get(origen);
        Zona z2 = zonas.get(destino);
        if (z1 != null && z2 != null) {
            z1.getConexiones().remove(z2);
        }
    }

    public void eliminarZona(String nombre) {
        Zona zona = zonas.get(nombre);
        if (zona != null) {
            // Eliminar referencias a esta zona desde otras zonas
            for (Zona z : zonas.values()) {
                z.getConexiones().remove(zona);
            }
            zonas.remove(nombre);
        }
    }

    public Zona getZona(String nombre) {
        return zonas.get(nombre);
    }

    public Collection<Zona> getZonas() {
        return zonas.values();
    }

    public void mostrarGrafo() {
        for (Zona z : zonas.values()) {
            System.out.println(z.getNombre() + " conectado a:");
            for (Map.Entry<Zona, Integer> entry : z.getConexiones().entrySet()) {
                System.out.println("  → " + entry.getKey().getNombre() + " (peso " + entry.getValue() + ")");
            }
        }
    }
}
