package main;

import modelo.*;
import javax.swing.*;
import java.awt.*;

public class InterfazSwing extends JFrame {
    private CiudadGrafo grafo = new CiudadGrafo();
    private JTextArea output = new JTextArea();
    private JComboBox<String> zonasBox = new JComboBox<>();
    private JTextField zonaInput = new JTextField(10);
    private JTextField tipoInput = new JTextField(10);
    private JTextField nombreElementoInput = new JTextField(10);

    public InterfazSwing() {
        setTitle("Sistema de Zonas Urbanas - Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior para agregar zonas
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Zona:"));
        topPanel.add(zonaInput);
        JButton agregarZonaBtn = new JButton("Agregar Zona");
        topPanel.add(agregarZonaBtn);

        agregarZonaBtn.addActionListener(e -> {
            String nombre = zonaInput.getText().trim();
            if (!nombre.isEmpty()) {
                grafo.agregarZona(nombre);
                zonasBox.addItem(nombre);
                output.append("Zona agregada: " + nombre + "\n");
                zonaInput.setText("");
            }
        });

        // Panel medio para acciones
        JPanel midPanel = new JPanel();
        midPanel.add(new JLabel("Zonas:"));
        midPanel.add(zonasBox);
        midPanel.add(new JLabel("Tipo:"));
        midPanel.add(tipoInput);
        midPanel.add(new JLabel("Nombre:"));
        midPanel.add(nombreElementoInput);
        JButton agregarElementoBtn = new JButton("Agregar Elemento");
        midPanel.add(agregarElementoBtn);

        agregarElementoBtn.addActionListener(e -> {
            String zona = (String) zonasBox.getSelectedItem();
            Zona z = grafo.getZona(zona);
            if (z != null) {
                z.getArbolBMas().insert(tipoInput.getText(), nombreElementoInput.getText());
                output.append("Elemento insertado en " + zona + "\n");
            }
        });

        JButton mostrarArbolBtn = new JButton("Mostrar árbol B+");
        mostrarArbolBtn.addActionListener(e -> {
            String zona = (String) zonasBox.getSelectedItem();
            Zona z = grafo.getZona(zona);
            if (z != null) {
                output.append("Árbol B+ en " + zona + ":\n");
                z.getArbolBMas().printLeaves(); // Esto imprime en consola
            }
        });
        midPanel.add(mostrarArbolBtn);

        // 🔹 Nuevo botón: Ver conexiones entre zonas
        JButton verConexionesBtn = new JButton("Ver Conexiones");
        verConexionesBtn.addActionListener(e -> {
            output.append("\nConexiones entre zonas:\n");
            for (Zona z : grafo.getZonas()) {
                for (Zona destino : z.getConexiones().keySet()) {
                    int peso = z.getConexiones().get(destino);
                    output.append(z.getNombre() + " → " + destino.getNombre() + " (" + peso + ")\n");
                }
            }
        });
        midPanel.add(verConexionesBtn);

        // Área de salida
        output.setEditable(false);
        add(topPanel, BorderLayout.NORTH);
        add(midPanel, BorderLayout.CENTER);
        add(new JScrollPane(output), BorderLayout.SOUTH);

        setSize(750, 450);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazSwing());
    }
}
