package modelo;

import java.util.*;

public class Zona{
    private String nombre;
    private Map<Zona, Integer> conexiones; //Conexiones a otras zonas(vías)


    public Zona(String nombre){
        this.nombre = nombre;
        this.conexiones = new HashMap<>();
    }

    public String getNombre(){
        return nombre;
    }

    public void conectarCon(Zona destino, int peso){
        conexiones.put(destino, peso);
    }

    public Map<Zona,Integer> getConexiones(){
        return conexiones;
    }

    public String toString(){
        return nombre;
    }
}
