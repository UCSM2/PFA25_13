package modelo;

import modelo.bplus.BPlusTree;
import java.util.*;

public class Zona {
    private String nombre;
    private Map<Zona, Integer> conexiones;
    private BPlusTree<String, String> arbolBMas;

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

    public BPlusTree<String, String> getArbolBMas() {
        return arbolBMas;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
