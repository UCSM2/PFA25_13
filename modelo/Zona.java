package modelo;

import modelo.bplus.BPlusTree;
import modelo.bplus.ElementoUrbanistico;
import java.util.*;

public class Zona {
    private String nombre;
    private Map<Zona, Integer> conexiones;
    private BPlusTree<String, ElementoUrbanistico> arbolBMas;

    public Zona(String nombre) {
        this.nombre = nombre;
        this.conexiones = new HashMap<>();
        this.arbolBMas = new BPlusTree<>(3);
    }

    public String getNombre() {
        return nombre;
    }

    public void conectarCon(Zona destino, int peso) {
        conexiones.put(destino, peso);
    }

    public Map<Zona, Integer> getConexiones() {
        return conexiones;
    }

    public BPlusTree<String, ElementoUrbanistico> getArbolBMas() {
        return arbolBMas;
    }

    /**
     * Retorna los elementos del árbol B+ en forma de tabla (tipo, nombre),
     * útil para mostrar en un JTable.
     */
    public List<String[]> obtenerElementosComoTabla() {
        List<String[]> datos = new ArrayList<>();
        Map<String, ElementoUrbanistico> elementos = arbolBMas.getAllLeaves();
        for (ElementoUrbanistico e : elementos.values()) {
            datos.add(new String[]{e.getTipo(), e.getNombre()});
        }
        return datos;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

