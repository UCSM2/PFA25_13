package modelo.bplus;

public class ElementoUrbanistico {
    private String tipo;    // Ej: "Parque", "Casa", "Edificio"
    private String nombre;  // Ej: "Parque Central", "Casa 23"

    public ElementoUrbanistico(String tipo, String nombre) {
        this.tipo = tipo;
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return tipo + " - " + nombre;
    }
}
